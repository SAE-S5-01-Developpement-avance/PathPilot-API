/*
 * RouteService.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.GeoCord;
import fr.iut.pathpilotapi.clients.MongoClient;
import fr.iut.pathpilotapi.clients.repository.MongoClientRepository;
import fr.iut.pathpilotapi.exceptions.ObjectNotFoundException;
import fr.iut.pathpilotapi.exceptions.SalesmanBelongingException;
import fr.iut.pathpilotapi.itineraries.Itinerary;
import fr.iut.pathpilotapi.itineraries.ItineraryService;
import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.routes.dto.ClientState;
import fr.iut.pathpilotapi.routes.dto.RouteClient;
import fr.iut.pathpilotapi.salesman.Salesman;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Service to manipulate routes
 */
@Service
@RequiredArgsConstructor
public class RouteService {

    public static final String ROUTE_NOT_BELONGS_TO_SALESMAN = "Route with ID: %s does not belong to the connected salesman.";

    private final ItineraryService itineraryService;

    private final MongoTemplate mongoTemplate;

    private final RouteRepository routeRepository;

    private final MongoClientRepository mongoClientRepository;

    /**
     * Delete all routes from the database owned by the salesman and by itineraryId
     *
     * @param salesman who owns the routes
     * @param pageable the pageable object that specifies the page to retrieve with size and sorting
     * @return a page of all routes that belong to the salesman
     */
    public Page<Route> getAllRoutesFromSalesman(Salesman salesman, Pageable pageable) {
        return routeRepository.findAllBySalesmanId(salesman.getId(), pageable);
    }

    /**
     * Create a new Route in the database.
     *
     * @param itineraryId the ID of the itinerary to create the route from
     * @param salesman    who creates the route
     * @return the newly created Route
     */
    public Route createRoute(String itineraryId, Salesman salesman) {
        Route route = new Route();
        route.setState(RouteState.NOT_STARTED);
        Itinerary itinerary = itineraryService.findByIdAndConnectedSalesman(itineraryId, salesman);

        // Retrieve Itinerary data
        route.setSalesmanId(salesman.getId());
        route.setSalesman_home(new GeoJsonPoint(salesman.getLongHomeAddress(), salesman.getLatHomeAddress()));
        LinkedList<RouteClient> routeClients = new LinkedList<>();
        for (ClientDTO client : itinerary.getClients_schedule()) {
            routeClients.add(new RouteClient(client, ClientState.EXPECTED));
        }
        route.setClients(routeClients);

        ArrayList<Point> positions = new ArrayList<>();
        positions.add(new Point(salesman.getLongHomeAddress(), salesman.getLatHomeAddress()));
        route.setSalesmanPositions(new GeoJsonLineString(positions));

        route.setStartDate(new Date());

        return routeRepository.save(route);
    }

    /**
     * Starts a Route in the database.
     *
     * @param routeId         the ID of the route to start the route
     * @param currentPosition the current position of the salesman
     * @param salesman        who started the route
     */
    public void startRoute(String routeId, GeoCord currentPosition, Salesman salesman) {
        updateRouteStateWithSalesmanCord(routeId, currentPosition, RouteState.IN_PROGRESS, salesman, true);
    }

    /**
     * Resumes a Route in the database.
     *
     * @param routeId         the ID of the route to resume the route
     * @param currentPosition the current position of the salesman
     * @param salesman        who resumes the route
     */
    public void resumeRoute(String routeId, GeoCord currentPosition, Salesman salesman) {
        updateRouteStateWithSalesmanCord(routeId, currentPosition, RouteState.IN_PROGRESS, salesman, false);
    }

    /**
     * Update the route state with the salesman current position
     *
     * @param routeId         the ID of the route to update the state
     * @param currentPosition the current position of the salesman
     * @param state           the state to set
     * @param salesman        who updates
     * @param setStartDate    if true, set the start date
     */
    private void updateRouteStateWithSalesmanCord(String routeId, GeoCord currentPosition, RouteState state, Salesman salesman, boolean setStartDate) {
        Route route = findByIdAndConnectedSalesman(routeId, salesman);
        route.setState(state);
        if (setStartDate) {
            route.setStartDate(new Date());
        }
        List<Point> points = new ArrayList<>(route.getSalesmanPositions().getCoordinates());
        points.add(new Point(currentPosition.longitude(), currentPosition.latitude()));
        route.setSalesmanPositions(new GeoJsonLineString(points));
        routeRepository.save(route);

        Update update = new Update().set("state", state)
                .set("salesman_current_position", new GeoJsonPoint(currentPosition.longitude(), currentPosition.latitude()));
        if (setStartDate) {
            update.set("startDate", new Date());
        }
        mongoTemplate.updateFirst(query(where("id").is(routeId)), update, Route.class);
    }

    /**
     * Pauses a Route in the database.
     *
     * @param routeId  the ID of the route to pause the route
     * @param salesman who pause the route
     */
    public void pauseRoute(String routeId, Salesman salesman) {
        updateRouteState(routeId, RouteState.PAUSED, salesman);
    }

    /**
     * Completely stops a Route in the database.
     *
     * @param routeId  the ID of the route to stop the route
     * @param salesman who stop the route
     */
    public void stopRoute(String routeId, Salesman salesman) {
        updateRouteState(routeId, RouteState.STOPPED, salesman);
    }

    /**
     * Set the route state to one of the {@link RouteState} values
     *
     * @param routeId  the ID of the route to set the state
     * @param state    the state to set
     * @param salesman who set the state
     */
    public void updateRouteState(String routeId, RouteState state, Salesman salesman) {
        Route route = findByIdAndConnectedSalesman(routeId, salesman);
        route.setState(state);
        routeRepository.save(route);

        mongoTemplate.updateFirst(query(where("id").is(routeId)),
                new Update().set("state", state),
                Route.class);
    }


    /**
     * Find a route by its ID and the connected salesman
     *
     * @param id       the ID of the route
     * @param salesman the connected salesman
     * @return the route
     * @throws ObjectNotFoundException if the route is not found
     */
    public Route findByIdAndConnectedSalesman(String id, Salesman salesman) {
        Route route = routeRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Route not found with ID: " + id));

        // Check if the route belongs to the connected salesman
        if (!routeBelongToSalesman(route, salesman)) {
            throw new SalesmanBelongingException(String.format(ROUTE_NOT_BELONGS_TO_SALESMAN, id));
        }
        return route;
    }

    /**
     * Check if the route belongs to the salesman.
     *
     * @param route    the route to check
     * @param salesman the salesman to check
     * @return true if the route belongs to the salesman, false otherwise
     * @throws IllegalArgumentException if the route is null
     */
    public boolean routeBelongToSalesman(Route route, Salesman salesman) {
        if (route == null) {
            throw new ObjectNotFoundException("Route does not exist");
        }
        return salesman.getId().equals(route.getSalesmanId());
    }

    /**
     * Delete a route, if the connected salesman is the one related to the route.
     *
     * @param routeId  the route ID
     * @param salesman the connected salesman
     * @throws ObjectNotFoundException  if the route is not found
     * @throws IllegalArgumentException if the route does not belong to the salesman
     */
    public void deleteByIdAndConnectedSalesman(String routeId, Salesman salesman) {
        // Perform the delete operation
        routeRepository.delete(findByIdAndConnectedSalesman(routeId, salesman));
    }

    /**
     * Set a client as visited in a route
     *
     * @param clientId the client ID
     * @param routeId  the route ID
     * @param salesman the connected salesman
     * @throws IllegalArgumentException if the client is not in the route
     * @throws IllegalArgumentException if the route does not belong to the salesman
     */
    public void setClientVisited(Integer clientId, String routeId, Salesman salesman) {
        // we check if the client is in the route and the route belongs to the salesman
        RouteClient routeClient = getClientInRoute(clientId, routeId, salesman);
        routeClient.setState(ClientState.VISITED);
        routeRepository.save(findByIdAndConnectedSalesman(routeId, salesman));
        mongoTemplate.updateFirst(query(where("id").is(routeId).and("clients.client.id").is(clientId)),
                new Update().set("clients.$.state", ClientState.VISITED), Route.class);
    }

    /**
     * Set a client as skipped in a route
     *
     * @param clientId the client ID
     * @param routeId  the route ID
     * @param salesman the connected salesman
     * @throws IllegalArgumentException if the client is not in the route
     * @throws IllegalArgumentException if the route does not belong to the salesman
     */
    public void setClientSkipped(Integer clientId, String routeId, Salesman salesman) {
        RouteClient routeClient = getClientInRoute(clientId, routeId, salesman);
        routeClient.setState(ClientState.SKIPPED);
        routeRepository.save(findByIdAndConnectedSalesman(routeId, salesman));
        mongoTemplate.updateFirst(query(where("id").is(routeId).and("clients.client.id").is(clientId)),
                new Update().set("clients.$.state", ClientState.SKIPPED), Route.class);
    }

    /**
     * Find nearby clients from a point
     *
     * @param point          the point to search from
     * @param clientsToAvoid list of clients to avoid
     * @param distance
     * @return the list of nearby clients
     */
    public Page<MongoClient> findNearbyClients(String routeId, Salesman salesman, GeoJsonPoint point, List<MongoClient> clientsToAvoid, Distance distance) {
        Route route = findByIdAndConnectedSalesman(routeId, salesman);

        List<MongoClient> clients = mongoClientRepository.findByLocationNear(point, distance).stream()
                .filter(client -> {
                    if (!client.getCategory().getName().equals("PROSPECT")) {
                        return false;
                    }
                    return !clientsToAvoid.contains(client);
                }).collect(Collectors.toList());
        if (clients.isEmpty()) {
            return Page.empty();
        }

        return new PageImpl<>(clients, PageRequest.of(0, clients.size()), clients.size());
    }


    /**
     * Update the salesman position in the route
     *
     * @param routeId                 the route ID
     * @param salesman                the connected salesman
     * @param currentSalesmanPosition the new position of the salesman
     * @return the list of nearby clients
     * @throws IllegalArgumentException if the route does not belong to the salesman
     */
    public void updateSalesmanPosition(String routeId, Salesman salesman, GeoCord currentSalesmanPosition) {
        Route route = findByIdAndConnectedSalesman(routeId, salesman);
        GeoJsonPoint newPoint = new GeoJsonPoint(currentSalesmanPosition.longitude(), currentSalesmanPosition.latitude());

        ArrayList<Point> positions = new ArrayList<>(route.getSalesmanPositions().getCoordinates());
        positions.add(newPoint);

        GeoJsonLineString updatedLineString = new GeoJsonLineString(positions);
        route.setSalesmanPositions(updatedLineString);

        routeRepository.save(route);
    }

    /**
     * Get the client in the route
     *
     * @param clientId the client ID
     * @param routeId  the route ID
     * @param salesman the connected salesman
     * @return the RouteClient object
     * @throws IllegalArgumentException if the client is not in the route
     * @throws IllegalArgumentException if the route does not belong to the salesman
     */
    private RouteClient getClientInRoute(Integer clientId, String routeId, Salesman salesman) {
        Route route = this.findByIdAndConnectedSalesman(routeId, salesman);

        return route.getClients().stream()
                .filter(client -> client.getClient().getId().equals(clientId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Client with ID: " + clientId + " is not in the route with ID: " + routeId));
    }
}