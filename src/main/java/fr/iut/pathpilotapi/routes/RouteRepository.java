/*
 * RouteRepository.java                                  06 dec. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends MongoRepository<Route, String> {

    /**
     * Finds a page of Route entities by the salesman's ID.
     *
     * @param salesmanId the ID of the salesman
     * @param pageable the pagination information
     * @return a page of Route entities
     */
    Page<Route> findAllBySalesmanId(Integer salesmanId, Pageable pageable);
}
