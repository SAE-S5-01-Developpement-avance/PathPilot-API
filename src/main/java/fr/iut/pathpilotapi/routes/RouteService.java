/*
 * RouteService.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.routes.Route;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;

    /**
     * Get all route from the database
     *
     * @return a list of all routes
     */
    public List<Route> getAllRoutesFromSalesman(Pageable pageable, int salesmanId) {
        return routeRepository.findAll((Sort) pageable).stream()
                .filter(route -> route.getSalesman().getSalesman_id() == salesmanId)
                .toList();
    }

    /**
     * Create a new Route in the database.
     *
     * @param Route the Route to create
     * @return the newly created Route
     */
    public Route addRoute(Route Route) {
        return routeRepository.save(Route);
    }

    /**
     * Get a Route by its id.
     *
     * @param id the id of the Route
     * @return the Route
     * @throws IllegalArgumentException if the Route is not found
     */
    public Route getRouteById(String id) {
        return routeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Route not found"));
    }
}
