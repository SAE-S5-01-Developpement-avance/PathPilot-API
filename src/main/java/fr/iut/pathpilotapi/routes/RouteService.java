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
import fr.iut.pathpilotapi.salesman.Salesman;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;

/**
 * Service to manipulate routes
 */
@Service
@RequiredArgsConstructor
public class RouteService {

    public static final String ROUTE_NOT_BELONGS_TO_SALESMAN = "Route with ID: %s does not belong to the connected salesman.";

    private final RouteRepository routeRepository;

    private final ItineraryService itineraryService;

    /**
     * Get all route from the database owned by the salesman
     *
     * @param salesman who owns the route
     * @param pageable the pageable object that specifies the page to retrieve with size and sorting
     * @return a page of all routes that belongs to the salesman
     */
    public Page<Route> getAllRoutesFromSalesman(Salesman salesman, Pageable pageable) {
        return routeRepository.findAllBySalesmanId(salesman.getId(), pageable);
    }

    /**
     * Create a new Route in the database.
     *
     * @param itineraryId the Route to create
     * @param salesman    who creates the route
     * @return the newly created Route
     */
    public Route createRoute(String itineraryId, Salesman salesman) {
        Route route = new Route();
        Itinerary itinerary = itineraryService.findByIdAndConnectedSalesman(itineraryId, salesman);

        //Retrieve Itinerary data
        route.setSalesmanId(salesman.getId());
        route.setSalesman_home(new GeoJsonPoint(salesman.getLongHomeAddress(), salesman.getLatHomeAddress()));
        LinkedList<RouteClient> routeClients = new LinkedList<>();
        for (ClientDTO client: itinerary.getClients_schedule()) {
            routeClients.add(new RouteClient(client, ClientState.EXPECTED));
        }
        route.setClients(routeClients);

        route.setSalesman_current_position(route.getSalesman_home());
        route.setStartDate(new Date());

        return routeRepository.save(route);
    }

    /**
     * Find a route by its id and the connected salesman
     *
     * @param id       the id of the route
     * @param salesman the connected salesman
     * @return the route
     * @throws ObjectNotFoundException if the route is not found
     */
    public Route findByIdAndConnectedSalesman(String id, Salesman salesman) {
        Route route = routeRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Route not found with ID: " + id));

        // Check if the itinerary belongs to the connected salesman
        if (!routeBelongToSalesman(route, salesman)) {
            throw new IllegalArgumentException("Route with ID: " + id + " does not belong to the connected salesman.");
        }
        return route;
    }


    /**
     * Check if the route belongs to the salesman.
     *
     * @param route    the route to check
     * @param salesman the salesman to check
     * @return true if the route belongs to the salesman, false otherwise
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
     * @param routeId  the route id
     * @param salesman the connected salesman
     * @throws ObjectNotFoundException  if the route is not found
     * @throws IllegalArgumentException if the route does not belong to the salesman
     */
    public void deleteByIdAndConnectedSalesman(String routeId, Salesman salesman) {
        // Perform the delete operation
        routeRepository.delete(findByIdAndConnectedSalesman(routeId, salesman));
    }
}
