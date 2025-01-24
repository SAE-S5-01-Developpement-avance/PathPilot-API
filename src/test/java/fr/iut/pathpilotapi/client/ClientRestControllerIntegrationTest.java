package fr.iut.pathpilotapi.client;

import fr.iut.pathpilotapi.WithMockSalesman;
import fr.iut.pathpilotapi.client.repository.ClientRepository;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
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

/**
 * Integration tests for the ClientRestController class.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class ClientRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SalesmanRepository salesmanRepository;
    @Autowired
    private ClientRepository clientRepository;

    private static final String API_CLIENTS_URL = "/clients";
    private static final String EMAIL_SALESMAN_CONNECTED = "john.doe@test.com";
    private static final String PASSWORD_SALESMAN_CONNECTED = "12345";
    private static Salesman salesman;

    @BeforeTestExecution
    void saveSalesman() {
        salesman = IntegrationTestUtils.createSalesman(EMAIL_SALESMAN_CONNECTED, PASSWORD_SALESMAN_CONNECTED);
        salesmanRepository.save(salesman);
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testGetAllClientsPage() throws Exception {
        // Given a client in the database linked to the connected salesman
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        Client client1 = IntegrationTestUtils.createClient();
        Client client2 = IntegrationTestUtils.createClient();

        client1.setSalesman(salesmanConnected);

        // Given two clients in the database and one linked to the connected salesman
        clientRepository.saveAll(List.of(client1, client2));

        // When we're getting all clients
        mockMvc.perform(get(API_CLIENTS_URL))

                // Then we should get the client back
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.clientList", hasSize(1)));
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testGetAllClients() throws Exception {
        // Given a client in the database linked to the connected salesman
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        Client client1 = IntegrationTestUtils.createClient();
        Client client2 = IntegrationTestUtils.createClient();

        client1.setSalesman(salesmanConnected);

        // Given two clients in the database and one linked to the connected salesman
        clientRepository.saveAll(List.of(client1, client2));

        // When we're getting all clients
        mockMvc.perform(get(API_CLIENTS_URL + "/all"))

                // Then we should get the client back
                .andExpect(status().isOk())
                .andExpect( jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddClient() throws Exception {
        // Given a client
        Client client = IntegrationTestUtils.createClient();

        // When we're adding the client
        mockMvc.perform(post(API_CLIENTS_URL)
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(client)))

                // Then we should get the client back
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyName", Matchers.is(client.getCompanyName())));
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testDeleteClientId() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();
        Client client = IntegrationTestUtils.createClient();
        client.setSalesman(salesmanConnected);

        // Given a client in the database
        clientRepository.save(client);

        // When we're deleting the client
        mockMvc.perform(delete(API_CLIENTS_URL + "/" + client.getId()))

                // Then we should get the deleted client back and the database should be empty
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(client.getId()));
        assertFalse(clientRepository.findAll().contains(client), "The database should be empty");
    }
}