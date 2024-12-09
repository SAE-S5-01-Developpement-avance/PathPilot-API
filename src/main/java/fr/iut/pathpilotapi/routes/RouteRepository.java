/*
 * RouteRepository.java                                  06 dec. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RouteRepository extends MongoRepository<Route, String> {

    Page<Route> findAllBySalesman(int salesmanId, Pageable pageable);
}
