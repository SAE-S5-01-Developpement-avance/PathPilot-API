package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.WithMockSalesman;
import fr.iut.pathpilotapi.client.Client;
import fr.iut.pathpilotapi.client.ClientRepository;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.apiguardian.api.API;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class RouteRestControllerIntegrationTest {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SalesmanRepository salesmanRepository;
    @Autowired
    private ClientRepository clientRepository;

    private static final String API_ROUTE_URL = "/api/routes";
    private static final String EMAIL_SALESMAN_CONNECTED = "john.doe@test.com";
    private static final String PASSWORD_SALESMAN_CONNECTED = "12345";
    private static Salesman salesman;
    @Autowired
    private RouteService routeService;
    @Autowired
    private RouteRepository routeRepository;

    @BeforeTestExecution
    void saveSalesman() {
        salesman = IntegrationTestUtils.createSalesman(EMAIL_SALESMAN_CONNECTED, PASSWORD_SALESMAN_CONNECTED);
        salesmanRepository.save(salesman);
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testGetRoutesFromSalesman() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();
        Client client1 = IntegrationTestUtils.createClient();

        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);
        Route route = IntegrationTestUtils.createRoute(salesmanConnected, List.of(clientCreated));

        // Given a route in the database
        routeRepository.save(route);

        // When we're getting all the route from the salesman
        mockMvc.perform(get(API_ROUTE_URL))

                // Then we should get the route back
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.routeList", hasSize(1)));
        // TODO check if the return route is the one we create
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddRoute() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();
        Client client1 = IntegrationTestUtils.createClient();

        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);

        // Given a route
        Route route = IntegrationTestUtils.createRoute(salesmanConnected, List.of(clientCreated));

        mockMvc.perform(post(API_ROUTE_URL)
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(route)))

                // Then we should get the client back
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.salesman", Matchers.is(route.getSalesman())));
    }

        @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testDeleteClientId() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();
        Client client1 = IntegrationTestUtils.createClient();

        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);
        Route route = IntegrationTestUtils.createRoute(salesmanConnected, List.of(clientCreated));

        // Given a route in the database
        route = routeRepository.save(route);

        // When we're deleting the client
        mockMvc.perform(delete(API_ROUTE_URL + "/" + route.get_id()))

                // Then we should get the deleted client back and the database should be empty
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(route.get_id()));
        assertFalse(clientRepository.findAll().contains(route), "The database should be empty");
    }
}