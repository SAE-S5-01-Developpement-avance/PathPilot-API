/*
 * RouteService.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.exceptions.ObjectNotFoundException;
import fr.iut.pathpilotapi.itineraries.Itinerary;
import fr.iut.pathpilotapi.itineraries.ItineraryService;
import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.routes.dto.ClientState;
import fr.iut.pathpilotapi.routes.dto.RouteClient;
import fr.iut.pathpilotapi.routes.dto.RouteStartRequestModel;
import fr.iut.pathpilotapi.salesman.Salesman;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Service to manipulate routes
 */
@Service
@RequiredArgsConstructor
public class RouteService {

    public static final String ROUTE_NOT_BELONGS_TO_SALESMAN = "Route with ID: %s does not belong to the connected salesman.";

    private final RouteRepository routeRepository;

    private final ItineraryService itineraryService;

    private final MongoTemplate mongoTemplate;

    /**
     * Get all routes from the database owned by the salesman
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

        route.setSalesman_current_position(route.getSalesman_home());
        route.setStartDate(null);

        return routeRepository.save(route);
    }

    /**
     * Starts a Route in the database.
     *
     * @param routeRM the ID of the route to start the route
     * @param salesman    who started the route
     */
    public Route startRoute(RouteStartRequestModel routeRM, Salesman salesman) {
        Route route = findByIdAndConnectedSalesman(routeRM.routeId(), salesman);
        route.setState(RouteState.IN_PROGRESS);
        route.setStartDate(new Date());
        route.setSalesman_current_position(new GeoJsonPoint(routeRM.currentPosition().longitude(), routeRM.currentPosition().latitude()));

        return routeRepository.save(route);
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
            throw new IllegalArgumentException(String.format(ROUTE_NOT_BELONGS_TO_SALESMAN, id));
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
            throw new IllegalArgumentException("Route does not exist");
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