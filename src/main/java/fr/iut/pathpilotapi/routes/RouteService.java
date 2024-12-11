/*
 * RouteService.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.client.ClientRepository;
import fr.iut.pathpilotapi.client.ClientService;
import fr.iut.pathpilotapi.routes.dto.ClientDTO;
import fr.iut.pathpilotapi.salesman.Salesman;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final ClientService clientService;
    private final ClientRepository clientRepository;

    /**
     * Get all route from the database
     *
     * @return a list of all routes
     */
    public Page<Route> getAllRoutesFromSalesman(Pageable pageable, int salesmanId) {
        return routeRepository.findAllBySalesman(salesmanId, pageable);
    }

    /**
     * Create a new Route in the database.
     *
     * @param route    the Route to create
     * @return the newly created Route
     */
    public Route addRoute(Route route, Salesman salesman) {
        boolean routeValide = route.getClients_schedule().stream()
                .map( cilentDto -> clientRepository.findById(cilentDto.getClient()).orElse(null))
                .allMatch(client -> clientService.clientBelongToSalesman(client, salesman));


        if (!routeValide) {
            throw new IllegalArgumentException("the client does not belongs to the salesman");
        }
        return routeRepository.save(route);
    }

    /**
     * Get a Route by its id.
     *
     * @param id the id of the Route
     * @return the Route
     * @throws IllegalArgumentException if the Route is not found
     */
    public Route getRouteById(int id) {
        return routeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Route not found"));
    }

    public boolean delete(Route route) {
        try {
            routeRepository.delete(route);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
