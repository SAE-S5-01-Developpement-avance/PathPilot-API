/*
 * ItineraryService.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.itineraries;

import fr.iut.pathpilotapi.client.ClientService;
import fr.iut.pathpilotapi.itineraries.dto.ItineraryRequestModel;
import fr.iut.pathpilotapi.salesman.Salesman;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Service to manipulate Itineraries
 */
@Service
@RequiredArgsConstructor
public class ItineraryService {

    public static final String ITINERARY_NOT_BELONGS_TO_SALESMAN = "Itinerary does not belong to the connected salesman.";

    private final ItineraryRepository itineraryRepository;

    private final ClientService clientService;

    /**
     * Get all itineraries from the database owned by the salesman
     * @param pageable
     * @param salesman who owns the Itinerary
     * @return a list of all itineraries
     */
    public Page<Itinerary> getAllItinerariesFromSalesman(Salesman salesman, Pageable pageable) {
        return itineraryRepository.findAllBySalesman(salesman.getId(), pageable);
    }

    /**
     * Create a new itinerary in the database.
     * @param itinerary    the itinerary to create
     * @param salesman who creates the Itinerary
     * @return the newly created Itinerary
     */
    public Itinerary addItinerary(ItineraryRequestModel itinerary, Salesman salesman) {

        boolean isItineraryValid = itinerary.getClientsSchedule().stream()
                .map( clientDTO -> clientService.findByIdAndConnectedSalesman(clientDTO.getId(), salesman))
                .allMatch(client -> clientService.clientBelongToSalesman(client, salesman));

        if (!isItineraryValid) {
            throw new IllegalArgumentException(ITINERARY_NOT_BELONGS_TO_SALESMAN);
        }
        Itinerary newItinerary = new Itinerary();
        newItinerary.setClients_schedule(itinerary.getClientsSchedule());
        newItinerary.setSalesman_id(salesman.getId());
        newItinerary.setSalesman_home(new GeoJsonPoint(salesman.getLatHomeAddress(), salesman.getLongHomeAddress()));

        // TODO make the algorithm to calculate the optimized route
        return itineraryRepository.save(newItinerary);
    }

    /**
     * Get an itinerary by its id and the connected salesman
     *
     * @param id the id of the itinerary
     * @param salesman the connected salesman
     * @return the itinerary
     * @throws IllegalArgumentException if the itinerary is not found
     */
    public Itinerary findByIdAndConnectedSalesman(String id, Salesman salesman) {
        Itinerary itinerary = itineraryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Itinerary not found with ID: " + id));

        // Check if the itinerary belongs to the connected salesman
        if (!itineraryBelongToSalesman(itinerary, salesman)) {
            throw new IllegalArgumentException("Itinerary with ID: " + id + " does not belong to the connected salesman.");
        }
        return itinerary;
    }


    /**
     * Check if the itinerary belongs to the salesman.
     *
     * @param itinerary   the itinerary to check
     * @param salesman the salesman to check
     * @return true if the itinerary belongs to the salesman, false otherwise
     */
    public boolean itineraryBelongToSalesman(Itinerary itinerary, Salesman salesman) {
        if (itinerary == null) {
            throw new IllegalArgumentException("Itinerary does not exist");
        }
        return salesman.getId().equals(itinerary.getSalesman_id());
    }

    /**
     * Delete an itinerary
     * @param itinerary to delete
     * @param salesman who owns the itinerary
     * @return true if deleted, false otherwise
     */
    public boolean delete(Itinerary itinerary, Salesman salesman) {
        try {
            if (!Objects.equals(itinerary.getSalesman_id(), salesman.getId())) {
                return false;
            }
            itineraryRepository.delete(itinerary);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
