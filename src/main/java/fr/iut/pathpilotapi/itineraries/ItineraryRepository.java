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


    /**
     * Finds a page of Itinerary entities by the salesman's ID.
     *
     * @param salesmanId the ID of the salesman
     * @param pageable the pagination information
     * @return a page of Itinerary entities
     */
    Page<Itinerary> findAllBySalesmanId(Integer salesmanId, Pageable pageable);

    /**
     * Finds all Itinerary entities by the salesman's ID.
     *
     * @param salesmanId the ID of the salesman
     * @return a list of Itinerary entities
     */
    List<Itinerary> findAllItinerariesBySalesmanId(Integer salesmanId);
}
