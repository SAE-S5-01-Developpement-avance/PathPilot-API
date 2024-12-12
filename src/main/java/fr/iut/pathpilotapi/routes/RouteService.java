/*
 * RouteService.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.client.ClientService;
import fr.iut.pathpilotapi.salesman.Salesman;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * service to manipulate routes
 */
@Service
@RequiredArgsConstructor
public class RouteService {

    public static final String CLIENT_NOT_BELONGS_TO_SALESMAN = "The client does not belongs to the salesman";

    private final RouteRepository routeRepository;
    private final ClientService clientService;

    /**
     * Get all routes from the database
     * @return a list of all routes
     */
    public Page<Route> getAllRoutesFromSalesman(Pageable pageable, int salesmanId) {
        return routeRepository.findAllBySalesman(salesmanId, pageable);
    }

    /**
     * Get all route from the database owned by the salesman
     * @param pageable
     * @param salesman who owns the route
     * @return a list of all routes
     */
    public Page<Route> getAllRoutesFromSalesman(Pageable pageable, Salesman salesman) {
        return getAllRoutesFromSalesman(pageable, salesman.getId());
    }

    /**
     * Create a new Route in the database.
     * @param route    the Route to create
     * @param salesman who creates the route
     * @return the newly created Route
     */
    public Route addRoute(Route route, Salesman salesman) {

        boolean isRouteValid = route.getClients_schedule().stream()
                .map( clientDTO -> clientService.getClientById(clientDTO.getClient()))
                .allMatch(client -> clientService.clientBelongToSalesman(client, salesman));

        if (!isRouteValid) {
            throw new IllegalArgumentException(CLIENT_NOT_BELONGS_TO_SALESMAN);
        }

        return routeRepository.save(route);
    }

    /**
     * Get a Route by its id.
     * @param id the id of the Route
     * @return the Route
     * @throws IllegalArgumentException if the Route is not found
     */
    public Route getRouteById(int id) {
        return routeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Route not found"));
    }

    public boolean delete(Route route, Salesman salesman) {
        try {
            if (route.getSalesman() != salesman.getId()) {
                return false;
            }
            routeRepository.delete(route);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
