package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.WithMockSalesman;
import fr.iut.pathpilotapi.clients.Client;
import fr.iut.pathpilotapi.clients.repository.ClientRepository;
import fr.iut.pathpilotapi.itineraries.Itinerary;
import fr.iut.pathpilotapi.itineraries.ItineraryRepository;
import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.routes.dto.RouteRequestModel;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class RouteControllerIntegrationTest {

    private static final String API_ROUTE_URL = "/routes";
    private static final String EMAIL_SALESMAN_CONNECTED = "john.doe@test.com";
    private static final String PASSWORD_SALESMAN_CONNECTED = "12345";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SalesmanRepository salesmanRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private ItineraryRepository itineraryRepository;

    @BeforeTestExecution
    void saveSalesman() {
        salesmanRepository.save(
                IntegrationTestUtils.createSalesman(EMAIL_SALESMAN_CONNECTED, PASSWORD_SALESMAN_CONNECTED)
        );
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testGetRoutesFromSalesman() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given a client to retrieve it through the route via ClientDTO, a clientDTO and a route in the database
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Route route = IntegrationTestUtils.createRoute(salesmanConnected, List.of(clientDTO));
        routeRepository.save(route);

        // When we're getting all the route from the salesman
        mockMvc.perform(get(API_ROUTE_URL))
                // Then we should get the route back
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.routeResponseModelList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.routeResponseModelList[0].id").value(route.getId()))
                .andExpect(jsonPath("$._embedded.routeResponseModelList[0].clients[0].client.id").value(clientCreated.getId()));
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testGetRoutesFromSalesman_EmptyPage() throws Exception {
        //Given an empty database
        // When we're getting all routes from the salesman and there are none
        mockMvc.perform(get(API_ROUTE_URL))
                // Then we should get a 204 No Content status
                .andExpect(status().isOk());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testGetRoute() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given a route in the database
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Route route = IntegrationTestUtils.createRoute(salesmanConnected, List.of(clientDTO));
        routeRepository.save(route);

        // When we're getting the route by ID
        mockMvc.perform(get(API_ROUTE_URL + "/" + route.getId()))
                // Then we should get the route back
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(route.getId()))
                .andExpect(jsonPath("$.clients[0].client.id").value(clientCreated.getId()));
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testCreateRoute() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given an itinerary in the database
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesmanConnected, List.of(clientDTO));
        itineraryRepository.save(itinerary);

        RouteRequestModel routeRequestModel = new RouteRequestModel(itinerary.getId());

        // When we're creating a new route
        mockMvc.perform(post(API_ROUTE_URL)
                        .content(IntegrationTestUtils.asJsonString(routeRequestModel))
                        .contentType("application/json"))
                // Then we should get the created route back
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.clients[0].client.id").value(clientCreated.getId()));
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testCreateRouteNoIdGiven() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given an itinerary in the database
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesmanConnected, List.of(clientDTO));
        itineraryRepository.save(itinerary);

        RouteRequestModel routeRequestModel = new RouteRequestModel(null);

        // When we're creating a new route with an invalid itinerary ID
        mockMvc.perform(post(API_ROUTE_URL)
                        .content(IntegrationTestUtils.asJsonString(routeRequestModel))
                        .contentType("application/json"))
                // Then we should get a 400 Bad Request status
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testCreateRouteInvalidId() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given an itinerary in the database
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesmanConnected, List.of(clientDTO));
        itineraryRepository.save(itinerary);

        RouteRequestModel routeRequestModel = new RouteRequestModel("invalidId");

        // When we're creating a new route with an invalid itinerary ID
        mockMvc.perform(post(API_ROUTE_URL)
                        .content(IntegrationTestUtils.asJsonString(routeRequestModel))
                        .contentType("application/json"))
                // Then we should get a 404 Not Found status
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testDeleteRoute() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given a route in the database
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Route route = IntegrationTestUtils.createRoute(salesmanConnected, List.of(clientDTO));
        routeRepository.save(route);

        // When we're deleting the route by ID
        mockMvc.perform(delete(API_ROUTE_URL + "/" + route.getId()))
                // Then the route should be deleted
                .andExpect(status().isOk());

        // Verify the route is deleted
        mockMvc.perform(get(API_ROUTE_URL + "/" + route.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testSetClientVisited() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given a route in the database
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Route route = IntegrationTestUtils.createRoute(salesmanConnected, List.of(clientDTO));
        routeRepository.save(route);

        // When setting the client as visited
        mockMvc.perform(put(API_ROUTE_URL + "/" + route.getId() + "/clients/" + clientCreated.getId() + "/visited"))
                // Then the client state should be VISITED
                .andExpect(status().isOk());

        mockMvc.perform(get(API_ROUTE_URL + "/" + route.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clients[0].state").value("VISITED"));
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testSetClientSkipped() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given a route in the database
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Route route = IntegrationTestUtils.createRoute(salesmanConnected, List.of(clientDTO));
        routeRepository.save(route);

        // When setting the client as skipped
        mockMvc.perform(put(API_ROUTE_URL + "/" + route.getId() + "/clients/" + clientCreated.getId() + "/skipped"))
                // Then the client state should be SKIPPED
                .andExpect(status().isOk());

        mockMvc.perform(get(API_ROUTE_URL + "/" + route.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clients[0].state").value("SKIPPED"));
    }

    @AfterEach
    void tearDown() {
        itineraryRepository.deleteAll();
        clientRepository.deleteAll();
        salesmanRepository.deleteAll();
    }
}