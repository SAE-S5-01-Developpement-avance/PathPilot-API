/*
 * ItineraryRepository.java                                  06 dec. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.clients.repository;

import fr.iut.pathpilotapi.clients.MongoClient;
import org.springframework.data.geo.Distance;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoClientRepository extends MongoRepository<MongoClient, Integer> {
    List<MongoClient> findByLocationNear(GeoJsonPoint location, Distance distance);
}
