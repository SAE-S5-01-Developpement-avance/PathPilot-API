/*
 * ItineraryRepository.java                                  06 dec. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.clients.repository;

import fr.iut.pathpilotapi.clients.MongoClient;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoClientRepository extends MongoRepository<MongoClient, Integer> {

}
