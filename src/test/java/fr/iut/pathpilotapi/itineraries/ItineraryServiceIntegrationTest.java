package fr.iut.pathpilotapi.itineraries;

import fr.iut.pathpilotapi.GeoCord;
import fr.iut.pathpilotapi.clients.Client;
import fr.iut.pathpilotapi.clients.repository.ClientRepository;
import fr.iut.pathpilotapi.exceptions.ObjectNotFoundException;
import fr.iut.pathpilotapi.itineraries.dto.ItineraryRequestModel;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class ItineraryServiceIntegrationTest {

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private ItineraryService itineraryService;

    @Autowired
    private SalesmanRepository salesmanRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void testGetAllItinerariesFromSalesman() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);

        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesman, List.of());
        itineraryRepository.save(itinerary);

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Itinerary> itineraries = itineraryService.getAllItinerariesFromSalesman(salesman, pageRequest).getContent();

        assertEquals(1, itineraries.size(), "There should be one itinerary in the database");
        assertEquals(itinerary, itineraries.getFirst(), "The itinerary should be the one in the database");
    }

    @Test
    void testCreateItinerary() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);

        Client client = IntegrationTestUtils.createClient();
        client.setSalesman(salesman);
        client = clientRepository.save(client);

        ItineraryRequestModel itineraryRequest = new ItineraryRequestModel();
        itineraryRequest.setClients_schedule(List.of(client.getId()));

        Itinerary createdItinerary = itineraryService.createItinerary(itineraryRequest, salesman, Collections.emptyList());

        assertNotNull(createdItinerary, "The itinerary should be created");
        assertEquals(1, createdItinerary.getClients_schedule().size(), "The itinerary should have one client");
        assertEquals(client.getId(), createdItinerary.getClients_schedule().getFirst().getId(), "The client should be the one in the itinerary");
    }

    @Test
    void testFindByIdAndConnectedSalesman() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);

        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesman, List.of());
        itinerary = itineraryRepository.save(itinerary);

        Itinerary foundItinerary = itineraryService.findByIdAndConnectedSalesman(itinerary.getId(), salesman);

        assertNotNull(foundItinerary, "The itinerary should be found");
        assertEquals(itinerary.getId(), foundItinerary.getId(), "The itinerary should be the one in the database");
    }

    @Test
    void testDeleteByIdAndConnectedSalesman() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);

        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesman, List.of());
        itineraryRepository.save(itinerary);

        itineraryService.deleteByIdAndConnectedSalesman(itinerary.getId(), salesman);

        assertThrows(ObjectNotFoundException.class, () -> itineraryService.findByIdAndConnectedSalesman(itinerary.getId(), salesman), "The itinerary should be deleted");
    }

    @AfterEach
    void tearDown() {
        itineraryRepository.deleteAll();
        clientRepository.deleteAll();
        salesmanRepository.deleteAll();
    }

    @Test
    void testCreateItineraryWithBestItinerary() {
        final GeoCord PARIS = new GeoCord(48.864716, 2.349014);
        final GeoCord LYON = new GeoCord(45.763420, 4.834277);
        final GeoCord RODEZ = new GeoCord(44.350601, 2.575000);
        final GeoCord TOULOUSE = new GeoCord(43.604500, 1.444000);

        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setLatHomeAddress(TOULOUSE.latitude());
        salesman.setLongHomeAddress(TOULOUSE.longitude());
        salesman = salesmanRepository.save(salesman);

        Client client1 = IntegrationTestUtils.createClient();
        Client client2 = IntegrationTestUtils.createClient();
        Client client3 = IntegrationTestUtils.createClient();

        client1.setSalesman(salesman);
        client2.setSalesman(salesman);
        client3.setSalesman(salesman);
        client1.setGeoCord(PARIS);
        client2.setGeoCord(LYON);
        client3.setGeoCord(RODEZ);

        client1 = clientRepository.save(client1);
        client2 = clientRepository.save(client2);
        client3 = clientRepository.save(client3);

        ItineraryRequestModel itineraryRequest = new ItineraryRequestModel();
        itineraryRequest.setClients_schedule(List.of(client1.getId(), client2.getId(), client3.getId()));

        List<List<Double>> matrixDistances = itineraryService.getDistances(itineraryRequest.getClients_schedule(), List.of("distance"), "driving-car", salesman).block();
        Itinerary createdItinerary = itineraryService.createItinerary(itineraryRequest, salesman, matrixDistances);

        assertNotNull(createdItinerary, "The itinerary should be created");
        assertEquals(3, createdItinerary.getClients_schedule().size(), "The itinerary should have three clients");
        assertEquals(client1.getId(), createdItinerary.getClients_schedule().getFirst().getId(), "The first client should be the one in the itinerary");
        assertEquals(client3.getId(), createdItinerary.getClients_schedule().getLast().getId(), "The second client should be the one in the itinerary");
    }
}