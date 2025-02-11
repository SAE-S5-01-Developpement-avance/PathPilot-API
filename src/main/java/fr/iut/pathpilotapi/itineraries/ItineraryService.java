/*
 * ItineraryService.java                                  18 oct. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.itineraries;

import fr.iut.pathpilotapi.algorithme.Algorithme;
import fr.iut.pathpilotapi.algorithme.BruteForce;
import fr.iut.pathpilotapi.clients.ClientService;
import fr.iut.pathpilotapi.exceptions.ObjectNotFoundException;
import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.itineraries.dto.ItineraryRequestModel;
import fr.iut.pathpilotapi.itineraries.dto.MatrixDistancesResponse;
import fr.iut.pathpilotapi.itineraries.dto.MatrixLocationsRequest;
import fr.iut.pathpilotapi.salesman.Salesman;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Service to manipulate Itineraries
 */
@Service
@RequiredArgsConstructor
public class ItineraryService {

    private static final Logger logger = LoggerFactory.getLogger(ItineraryService.class);

    public static final String ITINERARY_NOT_BELONGS_TO_SALESMAN = "Itinerary does not belong to the connected salesman.";

    public static final String ITINERARY_WITH_ID_NOT_BELONGS_TO_SALESMAN = "Itinerary with ID: %s does not belong to the connected salesman.";

    public static final String ITINERARY_NOT_FOUND = "Itinerary not found with ID: %s";

    private final ItineraryRepository itineraryRepository;

    private final ClientService clientService;

    private final WebClient oRSWebClient;

    private final Algorithme algorithme = new BruteForce();

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
     * @param distances matrix of the distances between all the clients and the salesman
     * @return the newly created Itinerary
     */
    public Itinerary createItinerary(ItineraryRequestModel itinerary, Salesman salesman, List<List<Double>> distances) {
        Itinerary newItinerary = new Itinerary();
        List<ClientDTO> clients = itinerary.getClients_schedule().stream()
                // If a client isn't found or doesn't belong to the salesman, an exception is throw.
                // So with that we can be sure the itinerary we want to creat is valid.
                .map(clientId -> new ClientDTO(clientService.findByIdAndConnectedSalesman(clientId, salesman)))
                .toList();

        List<Integer> orderedClientsId = new ArrayList<>();
        if (!distances.isEmpty()) {
            algorithme.setMatrixLocationsRequest(distances);
            algorithme.computeBestPath();
            List<Integer> indexClientBestPath = algorithme.getBestPath();

            for (int i : indexClientBestPath) {
                orderedClientsId.add(clients.get(i - 1).getId());
            }
        } else {
            for (ClientDTO client : clients) {
                orderedClientsId.add(client.getId());
            }
        }

        logger.debug("Liste avant {}", orderedClientsId);
        newItinerary.setClients_schedule(clientService.getAllClients(orderedClientsId, salesman).stream().map(ClientDTO::new).toList());
        newItinerary.setSalesmanId(salesman.getId());
        newItinerary.setSalesman_home(new GeoJsonPoint(salesman.getLatHomeAddress(), salesman.getLongHomeAddress()));
        return itineraryRepository.save(newItinerary);
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

    /**
     * @param clientsId
     * @param metrics
     * @param profile
     * @param salesman
     * @return
     */
    public Mono<List<List<Double>>> getDistances(List<Integer> clientsId, List<String> metrics, String profile,
                                                 Salesman salesman) {
        List<List<Double>> clientsLocations = new ArrayList<>();

        clientsLocations.add(Arrays.asList(salesman.getLatHomeAddress(), salesman.getLongHomeAddress()));
        clientsLocations.addAll(clientService.getClientsLocationsByIds(clientsId, salesman));

        return oRSWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/matrix/driving-car")
                        .queryParam("profile", profile)
                        .build())
                .bodyValue(new MatrixLocationsRequest(clientsLocations, List.of("distance")))
                .retrieve()
                .bodyToMono(MatrixDistancesResponse.class)
                .map(MatrixDistancesResponse::getDistances)
                .onErrorResume(e -> Mono.just(new ArrayList<>()));
    }
}
