package fr.iut.pathpilotapi.itineraries;

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
        List<Itinerary> itineraries = itineraryService.getAllItinerariesFromSalesmanPageable(salesman, pageRequest).getContent();

        assertEquals(1, itineraries.size(), "There should be one itinerary in the database");
        assertEquals(itinerary, itineraries.getFirst(), "The itinerary should be the one in the database");
    }

    @Test
    void testGetAllItinerariesBySalesman() {
        // Given a salesman with some itineraries
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);

        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesman, List.of());
        itineraryRepository.save(itinerary);

        // When we want to get all itineraries of this salesman
        List<Itinerary> itineraries = itineraryService.getAllItinerariesFromSalesman(salesman);

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
}