/*
 * ItineraryService.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.itineraries;

import fr.iut.pathpilotapi.clients.ClientService;
import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.itineraries.dto.ItineraryRequestModel;
import fr.iut.pathpilotapi.salesman.Salesman;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to manipulate Itineraries
 */
@Service
@RequiredArgsConstructor
public class ItineraryService {

    public static final String ITINERARY_NOT_BELONGS_TO_SALESMAN = "Itinerary does not belong to the connected salesman.";

    public static final String ITINERARY_WITH_ID_NOT_BELONGS_TO_SALESMAN = "Itinerary with ID: %s does not belong to the connected salesman.";

    public static final String ITINERARY_NOT_FOUND = "Itinerary not found with ID: %s";

    private final ItineraryRepository itineraryRepository;

    private final ClientService clientService;

    /**
     * Get all itineraries from the database owned by the salesman
     * @param pageable
     * @param salesman who owns the Itinerary
     * @return a list of all itineraries
     */
    public Page<Itinerary> getAllItinerariesFromSalesman(Salesman salesman, Pageable pageable) {
        return itineraryRepository.findAllBySalesmanId(salesman.getId(), pageable);
    }

    /**
     * Create a new itinerary in the database.
     * @param itinerary    the itinerary to create
     * @param salesman who creates the Itinerary
     * @return the newly created Itinerary
     */
    public Itinerary createItinerary(ItineraryRequestModel itinerary, Salesman salesman) {

        boolean isItineraryValid = itinerary.getClients_schedule().stream()
                .map( clientId -> clientService.findByIdAndConnectedSalesman(clientId, salesman))
                .allMatch(client -> clientService.clientBelongToSalesman(client, salesman));

        if (!isItineraryValid) {
            throw new IllegalArgumentException(ITINERARY_NOT_BELONGS_TO_SALESMAN);
        }
        Itinerary newItinerary = new Itinerary();
        List<ClientDTO> clients = itinerary.getClients_schedule().stream()
                .map(clientId -> new ClientDTO(clientService.findByIdAndConnectedSalesman(clientId, salesman)))
                .toList();
        newItinerary.setClients_schedule(clients);
        newItinerary.setSalesmanId(salesman.getId());
        newItinerary.setSalesman_home(new GeoJsonPoint(salesman.getLatHomeAddress(), salesman.getLongHomeAddress()));

        // TODO make the algorithm to calculate the optimized itinerary
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
        Itinerary itinerary = itineraryRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(String.format(ITINERARY_NOT_FOUND, id)));

        // Check if the itinerary belongs to the connected salesman
        if (!itineraryBelongToSalesman(itinerary, salesman)) {
            throw new IllegalArgumentException(String.format(ITINERARY_WITH_ID_NOT_BELONGS_TO_SALESMAN, id));
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
        return salesman.getId().equals(itinerary.getSalesmanId());
    }

    /**
     * Delete an itinerary, if the connected salesman is the one related to the itinerary.
     *
     * @param itineraryId  the itinerary id
     * @param salesman the connected salesman
     * @throws IllegalArgumentException if the itinerary is not found or does not belong to the salesman
     */
    public void deleteByIdAndConnectedSalesman(String itineraryId, Salesman salesman) {
        Itinerary itinerary = itineraryRepository.findById(itineraryId).orElseThrow(
                () -> new IllegalArgumentException(String.format(ITINERARY_NOT_FOUND, itineraryId)));

        // Check if the client belongs to the connected salesman
        if (!itineraryBelongToSalesman(itinerary, salesman)) {
            throw new IllegalArgumentException(String.format(ITINERARY_WITH_ID_NOT_BELONGS_TO_SALESMAN, itineraryId));
        }

        // Perform the delete operation
        itineraryRepository.delete(itinerary);
    }
}
