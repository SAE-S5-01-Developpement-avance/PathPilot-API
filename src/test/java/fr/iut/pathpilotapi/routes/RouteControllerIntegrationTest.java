package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.GeoCord;
import fr.iut.pathpilotapi.WithMockSalesman;
import fr.iut.pathpilotapi.clients.Client;
import fr.iut.pathpilotapi.clients.ClientCategory;
import fr.iut.pathpilotapi.clients.MongoClient;
import fr.iut.pathpilotapi.clients.repository.ClientCategoryRepository;
import fr.iut.pathpilotapi.clients.repository.ClientRepository;
import fr.iut.pathpilotapi.clients.repository.MongoClientRepository;
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

import java.util.ArrayList;
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
    @Autowired
    private ClientCategoryRepository clientCategoryRepository;
    @Autowired
    private MongoClientRepository mongoClientRepository;

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

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testStartRoute() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given a route in the database
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Route route = IntegrationTestUtils.createRoute(salesmanConnected, List.of(clientDTO));
        routeRepository.save(route);

        // When starting the route
        mockMvc.perform(patch(API_ROUTE_URL + "/" + route.getId() + "/start")
                        .content(IntegrationTestUtils.asJsonString(new GeoCord(48.8566, 2.3522)))
                        .contentType("application/json"))
                // Then the route state should be IN_PROGRESS
                .andExpect(status().isOk());

        mockMvc.perform(get(API_ROUTE_URL + "/" + route.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("IN_PROGRESS"));
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testStopRoute() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given a route in the database
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Route route = IntegrationTestUtils.createRoute(salesmanConnected, List.of(clientDTO));
        routeRepository.save(route);

        // When stopping the route
        mockMvc.perform(patch(API_ROUTE_URL + "/" + route.getId() + "/stop"))
                // Then the route state should be STOPPED
                .andExpect(status().isOk());

        mockMvc.perform(get(API_ROUTE_URL + "/" + route.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("STOPPED"));
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testStopRouteWithInvalidRoute() throws Exception {
        // When stopping a non-existing route
        mockMvc.perform(patch(API_ROUTE_URL + "/invalidRouteId/stop"))
                // Then we should get a 404 Not Found status
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testStopRouteButRouteDoesNotBelongToSalesman() throws Exception {
        Salesman anotherSalesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(anotherSalesman);

        // Given a route that belongs to another salesman
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(anotherSalesman);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Route route = IntegrationTestUtils.createRoute(anotherSalesman, List.of(clientDTO));
        routeRepository.save(route);

        // When stopping the route
        mockMvc.perform(patch(API_ROUTE_URL + "/" + route.getId() + "/stop"))
                // Then we should get a 400 Bad Request status
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testPauseRoute() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given a route in the database
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Route route = IntegrationTestUtils.createRoute(salesmanConnected, List.of(clientDTO));
        routeRepository.save(route);

        // When pausing the route
        mockMvc.perform(patch(API_ROUTE_URL + "/" + route.getId() + "/pause"))
                // Then the route state should be PAUSED
                .andExpect(status().isOk());

        mockMvc.perform(get(API_ROUTE_URL + "/" + route.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("PAUSED"));
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testPauseRouteWithInvalidRoute() throws Exception {
        // When stopping a non-existing route
        mockMvc.perform(patch(API_ROUTE_URL + "/invalidRouteId/pause"))
                // Then we should get a 404 Not Found status
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testPauseRouteButRouteDoesNotBelongToSalesman() throws Exception {
        Salesman anotherSalesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(anotherSalesman);

        // Given a route that belongs to another salesman
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(anotherSalesman);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Route route = IntegrationTestUtils.createRoute(anotherSalesman, List.of(clientDTO));
        routeRepository.save(route);

        // When pausing the route
        mockMvc.perform(patch(API_ROUTE_URL + "/" + route.getId() + "/pause"))
                // Then we should get a 400 Bad Request status
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testResumeRoute() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        // Given a route in the database
        Client client1 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());

        Route route = IntegrationTestUtils.createRoute(salesmanConnected, List.of(clientDTO));
        routeRepository.save(route);

        // When starting the route
        mockMvc.perform(patch(API_ROUTE_URL + "/" + route.getId() + "/resume")
                        .content(IntegrationTestUtils.asJsonString(new GeoCord(48.8566, 2.3522)))
                        .contentType("application/json"))
                // Then the route state should be IN_PROGRESS
                .andExpect(status().isOk());

        mockMvc.perform(get(API_ROUTE_URL + "/" + route.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("IN_PROGRESS"));
    }

    // RouteControllerIntegrationTest.java
    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testSetSalesManPositionReturnsNearbyClients() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();
        salesmanConnected.setLatHomeAddress(0.0);
        salesmanConnected.setLongHomeAddress(0.0);
        salesmanConnected = salesmanRepository.save(salesmanConnected);

        double DEGRE_TO_BE_1000M = 0.0089;
        List<Client> clientsNearby = new ArrayList<>();
        List<List<Double>> positions = List.of(
                List.of(0.0, 0.0), // 0 m away from (0, 0)
                List.of(DEGRE_TO_BE_1000M, 0.0), // 989.6 m away from (0, 0)
                List.of(0.0, DEGRE_TO_BE_1000M), // 989.6 m away from (0, 0)
                List.of(0.006, 0.006) // 943,5 m away from (0, 0)
        );

        ClientCategory prospect = clientCategoryRepository.save(new ClientCategory("PROSPECT"));

        for (List<Double> position : positions) {
            Client client = IntegrationTestUtils.createClient();
            client.setSalesman(salesmanConnected);
            client.setLatHomeAddress(position.get(0));
            client.setLongHomeAddress(position.get(1));
            client.setClientCategory(prospect);
            clientsNearby.add(client);
        }
        Client clientNotNearby = IntegrationTestUtils.createClient();
        clientNotNearby.setSalesman(salesmanConnected);
        clientNotNearby.setClientCategory(prospect);
        clientNotNearby.setLatHomeAddress(10.0);
        clientNotNearby.setLongHomeAddress(10.0);

        clientRepository.saveAll(clientsNearby).forEach(client ->
                mongoClientRepository.save(new MongoClient(client.getId(), client.getLatHomeAddress(), client.getLongHomeAddress()))
        );
        clientNotNearby = clientRepository.save(clientNotNearby);
        mongoClientRepository.save(new MongoClient(clientNotNearby.getId(), clientNotNearby.getLatHomeAddress(), clientNotNearby.getLongHomeAddress()));


        Route route = IntegrationTestUtils.createRoute(salesmanConnected, new ArrayList<>());
        routeRepository.save(route);

        mockMvc.perform(put(API_ROUTE_URL + "/" + route.getId() + "/updateSalesmanPosition")
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(new GeoCord(0.0, 0.0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.clientResponseModelList", hasSize(positions.size())));
    }


    @AfterEach
    void tearDown() {
        itineraryRepository.deleteAll();
        clientRepository.deleteAll();
        salesmanRepository.deleteAll();
        mongoClientRepository.deleteAll();
    }
}