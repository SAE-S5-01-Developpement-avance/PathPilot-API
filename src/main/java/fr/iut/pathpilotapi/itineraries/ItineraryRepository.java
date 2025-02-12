/*
 * ItineraryRepository.java                                  06 dec. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.itineraries;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ItineraryRepository extends MongoRepository<Itinerary, String> {

    Page<Itinerary> findAllBySalesmanId(Integer salesmanId, Pageable pageable);

    List<Itinerary> findAllItinerariesBySalesmanId(Integer salesmanId);
}
