/*
 * RouteRepository.java                                  06 dec. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends MongoRepository<Route, String> {

    /**
     * Finds a page of {@link Route} entities by the salesman's ID.
     *
     * @param salesmanId the ID of the salesman
     * @param pageable the pagination information
     * @return a page of Route entities
     */
    Page<Route> findAllBySalesmanId(Integer salesmanId, Pageable pageable);

    /**
     * Finds all {@link Route} entities by the salesman's ID.
     *
     * @param salesmanId the ID of the salesman
     * @return a list of {@link Route} entities
     */
    List<Route> findAllRoutesBySalesmanId(Integer salesmanId);

    @DeleteQuery(value = "{ 'salesmanId': ?0, 'clients.client.id': ?1 }")
    void deleteAllByClientIdAndConnectedSalesman(Integer salesmanId, Integer clientId);
}
