package fr.iut.pathpilotapi.itineraries;

import fr.iut.pathpilotapi.WithMockSalesman;
import fr.iut.pathpilotapi.clients.Client;
import fr.iut.pathpilotapi.clients.repository.ClientRepository;
import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.itineraries.dto.ItineraryRequestModel;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class ItineraryControllerIntegrationTest {

    private static final String API_ITINERARY_URL = "/itineraries";
    private static final String EMAIL_SALESMAN_CONNECTED = "john.doe@test.com";
    private static final String PASSWORD_SALESMAN_CONNECTED = "12345";
    private static Salesman salesman;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SalesmanRepository salesmanRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ItineraryRepository itineraryRepository;

    @BeforeTestExecution
    void saveSalesman() {
        salesman = IntegrationTestUtils.createSalesman(EMAIL_SALESMAN_CONNECTED, PASSWORD_SALESMAN_CONNECTED);
        salesmanRepository.save(salesman);
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddItinerary() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given two clients in the database
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);

        Client client2 = IntegrationTestUtils.createClient();
        client2.setSalesman(salesmanConnected);

        // Save the clients and retrieve the IDs
        client1 = clientRepository.save(client1);
        client2 = clientRepository.save(client2);

        ItineraryRequestModel itineraryRequest = new ItineraryRequestModel();
        itineraryRequest.setClients_schedule(List.of(client1.getId(), client2.getId()));

        // When we're adding a new itinerary
        mockMvc.perform(post(API_ITINERARY_URL)
                        .content(IntegrationTestUtils.asJsonString(itineraryRequest))
                        .contentType("application/json"))
                // Then we should get the itinerary back
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.clients_schedule").isNotEmpty())
                .andExpect(jsonPath("$.clients_schedule[0].id").value(client1.getId()));
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testGetItinerary() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given an itinerary in the database
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesmanConnected, List.of(clientDTO));
        itineraryRepository.save(itinerary);

        // When we're getting the itinerary by ID
        mockMvc.perform(get(API_ITINERARY_URL + "/" + itinerary.getId()))
                // Then we should get the itinerary back
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itinerary.getId()))
                .andExpect(jsonPath("$.clients_schedule[0].id").value(clientCreated.getId()));
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testGetItinerariesFromSalesman() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given an itinerary in the database
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesmanConnected, List.of(clientDTO));
        itineraryRepository.save(itinerary);

        // When we're getting all itineraries from the salesman
        mockMvc.perform(get(API_ITINERARY_URL))
                // Then we should get the itineraries back
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.itineraryResponseModelList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.itineraryResponseModelList[0].id").value(itinerary.getId()))
                .andExpect(jsonPath("$._embedded.itineraryResponseModelList[0].clients_schedule[0].id").value(clientCreated.getId()));
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testGetItinerariesFromSalesman_EmptyPage() throws Exception {
        //Given an empty database
        // When we're getting all itineraries from the salesman and there are none
        mockMvc.perform(get(API_ITINERARY_URL))
                // Then we should get a 204 No Content status
                .andExpect(status().isOk());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testDeleteItinerary() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given an itinerary in the database
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesmanConnected, List.of(clientDTO));
        itineraryRepository.save(itinerary);

        // When we're deleting the itinerary by ID
        mockMvc.perform(delete(API_ITINERARY_URL + "/" + itinerary.getId()))
                // Then the itinerary should be deleted
                .andExpect(status().isOk());

        // Verify the itinerary is deleted
        mockMvc.perform(get(API_ITINERARY_URL + "/" + itinerary.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddItineraryWithToMuchClient() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given nine clients in the database
        int toMuchClients = 9;
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < toMuchClients; i++) {
            Client client = IntegrationTestUtils.createClient();
            client.setSalesman(salesmanConnected);
            clients.add(client);
        }
        clients = clientRepository.saveAll(clients);

        ItineraryRequestModel itineraryRequest = new ItineraryRequestModel();
        itineraryRequest.setClients_schedule(clients.stream().map(Client::getId).toList());

        // When we're adding a new itinerary
        mockMvc.perform(post(API_ITINERARY_URL)
                        .content(IntegrationTestUtils.asJsonString(itineraryRequest))
                        .contentType("application/json"))
                // Then we should get an error
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddItineraryWithNoClient() throws Exception {

        // Given an itinerary request with no clients
        ItineraryRequestModel itineraryRequest = new ItineraryRequestModel();
        itineraryRequest.setClients_schedule(List.of());

        // When we're adding a new itinerary
        mockMvc.perform(post(API_ITINERARY_URL)
                        .content(IntegrationTestUtils.asJsonString(itineraryRequest))
                        .contentType("application/json"))
                // Then we should get an error
                .andExpect(status().isBadRequest());
    }

    @AfterEach
    void tearDown() {
        itineraryRepository.deleteAll();
        clientRepository.deleteAll();
        salesmanRepository.deleteAll();
    }
}