/*
 * ItineraryService.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.itineraries;

import fr.iut.pathpilotapi.clients.ClientService;
import fr.iut.pathpilotapi.exceptions.ObjectNotFoundException;
import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.itineraries.dto.ItineraryRequestModel;
import fr.iut.pathpilotapi.itineraries.dto.MatrixDistancesResponse;
import fr.iut.pathpilotapi.itineraries.dto.MatrixLocationsRequest;
import fr.iut.pathpilotapi.salesman.Salesman;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

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

    private final WebClient oRSWebClient;

    /**
     * Get all itineraries from the database owned by the salesman
     *
     * @param pageable
     * @param salesman who owns the Itinerary
     * @return a list of all itineraries
     */
    public Page<Itinerary> getAllItinerariesFromSalesman(Salesman salesman, Pageable pageable) {
        return itineraryRepository.findAllBySalesmanId(salesman.getId(), pageable);
    }

    /**
     * Create a new itinerary in the database.
     *
     * @param itinerary the itinerary to create
     * @param salesman  who creates the Itinerary
     * @return the newly created Itinerary
     */
    public Itinerary createItinerary(ItineraryRequestModel itinerary, Salesman salesman, List<List<Double>> distances) {
        Itinerary newItinerary = new Itinerary();
        List<ClientDTO> clients = itinerary.getClients_schedule().stream()
                // If a client isn't found or doesn't belong to the salesman, an exception is throw.
                // So with that we can be sure the itinerary we want to creat is valid.
                .map(clientId -> new ClientDTO(clientService.findByIdAndConnectedSalesman(clientId, salesman)))
                .toList();
        //ArrayList<ArrayList<Double>> clientsDistances = new ArrayList<>();
        //// TODO make the algorithm to calculate the optimized itinerary
        //List<Integer> remainingClients = new ArrayList<>();
        //for (int i = 1; i < clients.size(); i++) {
        //    remainingClients.add(i);
        //}
        //List<Integer> bestPath = new ArrayList<>();
        //findBestPathForItinerary(clientsDistances, new ArrayList<>(),remainingClients, 0,Double.MAX_VALUE,
        //        bestPath);

        newItinerary.setClients_schedule(clients);
        newItinerary.setSalesmanId(salesman.getId());
        newItinerary.setSalesman_home(new GeoJsonPoint(salesman.getLatHomeAddress(), salesman.getLongHomeAddress()));
        return itineraryRepository.save(newItinerary);
    }

    /**
     *
     * @param clientsDistances
     * @param currentClientsVisited
     * @param remainingClients
     * @param currentDistance
     */
    private void findBestPathForItinerary(ArrayList<ArrayList<Double>> clientsDistances,
                                          List<Integer> currentClientsVisited, List<Integer> remainingClients,
                                          double currentDistance, double bestDistance, List<Integer> bestPath) {
        if (remainingClients.isEmpty()) {
            currentDistance += clientsDistances.get(currentClientsVisited.get(currentClientsVisited.size() - 1)).get(0);
            if (currentDistance < bestDistance) {
                bestDistance = currentDistance;
                bestPath =  new ArrayList<>(currentClientsVisited);
            }
            return;
        }

        for (int i = 0; i < remainingClients.size(); i++) {
            int client = remainingClients.get(i);
            List<Integer> newPath = new ArrayList<>(currentClientsVisited);
            newPath.add(client);
            List<Integer> newRemaining = new ArrayList<>(remainingClients);
            newRemaining.remove(i);
            double newDistance = currentDistance;
            if (!currentClientsVisited.isEmpty()) {
                newDistance += clientsDistances.get(currentClientsVisited.get(currentClientsVisited.size() - 1)).get(client);
            } else {
                newDistance += clientsDistances.get(0).get(client);
            }
            if (newDistance < bestDistance) {
                findBestPathForItinerary(clientsDistances,newPath, newRemaining, newDistance,bestDistance, bestPath);
            }
        }
    }

    /**
     * Get an itinerary by its id and the connected salesman
     *
     * @param id       the id of the itinerary
     * @param salesman the connected salesman
     * @return the itinerary
     * @throws IllegalArgumentException if the itinerary is not found
     */
    public Itinerary findByIdAndConnectedSalesman(String id, Salesman salesman) {
        Itinerary itinerary = itineraryRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(String.format(ITINERARY_NOT_FOUND, id)));

        // Check if the itinerary belongs to the connected salesman
        if (!itineraryBelongToSalesman(itinerary, salesman)) {
            throw new IllegalArgumentException(String.format(ITINERARY_WITH_ID_NOT_BELONGS_TO_SALESMAN, id));
        }
        return itinerary;
    }

    /**
     * Check if the itinerary belongs to the salesman.
     *
     * @param itinerary the itinerary to check
     * @param salesman  the salesman to check
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
     * @param itineraryId the itinerary id
     * @param salesman    the connected salesman
     * @throws ObjectNotFoundException  if the itinerary is not found
     * @throws IllegalArgumentException if the itinerary does not belong to the salesman
     */
    public void deleteByIdAndConnectedSalesman(String itineraryId, Salesman salesman) {
        // Perform the delete operation
        itineraryRepository.delete(findByIdAndConnectedSalesman(itineraryId, salesman));
    }

    public Mono<List<List<Double>>> getDistances(ItineraryRequestModel itineraryRequestModel, String profile) {
        return oRSWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/matrix/driving-car")
                        .queryParam("profile", profile)
                        .build())
                .bodyValue(itineraryRequestModel)
                .retrieve()
                .bodyToMono(MatrixDistancesResponse.class)
                .map(matrixResponse -> matrixResponse.getDistances())
                .onErrorResume(e -> Mono.just(new ArrayList<>()));
    }
}
