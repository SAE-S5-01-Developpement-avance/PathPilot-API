package fr.iut.pathpilotapi.itineraries.routes;

import fr.iut.pathpilotapi.WithMockSalesman;
import fr.iut.pathpilotapi.clients.Client;
import fr.iut.pathpilotapi.clients.repository.ClientRepository;
import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.routes.Route;
import fr.iut.pathpilotapi.routes.RouteRepository;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
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
    @Autowired
    private RouteRepository routeRepository;

    private static final String API_ROUTE_URL = "/routes";
    private static final String EMAIL_SALESMAN_CONNECTED = "john.doe@test.com";
    private static final String PASSWORD_SALESMAN_CONNECTED = "12345";

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
        Client client1 = IntegrationTestUtils.createClient();

        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());
        Route route = IntegrationTestUtils.createRoute(salesmanConnected, List.of(clientDTO));

        // Given a route in the database
        routeRepository.save(route);

        // When we're getting all the route from the salesman
        mockMvc.perform(get(API_ROUTE_URL))
                // Then we should get the route back
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.routeResponseModelList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.routeResponseModelList[0].id").value(route.getId()))
                .andExpect(jsonPath("$._embedded.routeResponseModelList[0].salesman_id").value(route.getSalesmanId()))
                .andExpect(jsonPath("$._embedded.routeResponseModelList[0].expected_clients[0].id").value(clientCreated.getId()));
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddRoute() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();
        Client client1 = IntegrationTestUtils.createClient();

        client1.setSalesman(salesmanConnected);
        Client clientCreated = clientRepository.save(client1);
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientCreated.getId());
        Route route = IntegrationTestUtils.createRoute(salesmanConnected, List.of(clientDTO));

        // Given a route in the database
        routeRepository.save(route);

        // When we're adding a new route
        mockMvc.perform(post(API_ROUTE_URL)
                .param("itineraryId", route.getId()))
                // Then we should get the route back
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(route.getId()))
                .andExpect(jsonPath("$.salesman_id").value(route.getSalesmanId()))
                .andExpect(jsonPath("$.expected_clients[0].id").value(clientCreated.getId()));
    }
}