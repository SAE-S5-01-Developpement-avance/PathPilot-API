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

    /**
     * Finds a list of MongoClient entities near a specified location within a given distance.
     *
     * @param location the {@link GeoJsonPoint} representing the location to search near
     * @param distance the {@link Distance} within which to search for MongoClient entities
     * @return a list of MongoClient entities found near the specified location within the given distance
     */
    List<MongoClient> findByLocationNear(GeoJsonPoint location, Distance distance);
}
