package fr.iut.pathpilotapi.client;

import fr.iut.pathpilotapi.WithMockSalesman;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
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
    private SalesmanRepository salesmanRepository;
    @Autowired
    private ClientRepository clientRepository;

    private static final String API_CLIENTS_URL = "/api/clients";
    private static final String email = "john.doe@test.com";
    private static final String password = "12345";
    private static Salesman salesman;

    @BeforeTestExecution
    void saveSalesman() {
        salesman = IntegrationTestUtils.createSalesman(email, password);
        salesmanRepository.save(salesman);
    }

    /**
     * Prepare the data for the tests.
     * <p>
     * This method will save a salesman and two clients in the database.
     * One of the clients will be associated with the salesman.
     * The clients will be saved in the database.
     */
    private void preparerDonne() {
        Client client1 = IntegrationTestUtils.createClient();
        Client client2 = IntegrationTestUtils.createClient();

        client1.setSalesman(salesman);

        clientRepository.saveAll(List.of(client1, client2));
    }

    @Test
    @WithMockUser(username = email, password = password)
    void testGetAllClients() throws Exception {
        preparerDonne();

        mockMvc.perform(get(API_CLIENTS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.clientList", hasSize(1)));
    }

    @Test
    @WithMockSalesman(email = email, password = password)
    void testAddClient() throws Exception {
        Client client = IntegrationTestUtils.createClient();

        mockMvc.perform(post(API_CLIENTS_URL)
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(client)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyName", Matchers.is(client.getCompanyName())));
    }


    @Test
    @WithMockSalesman(email = email, password = password)
    void testDeleteClient() throws Exception {
        Client client = IntegrationTestUtils.createClient();
        client.setSalesman(salesman);
        clientRepository.save(client);

        mockMvc.perform(delete(API_CLIENTS_URL)
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(client.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(client.getId()));
    }

    @Test
    @WithMockSalesman(email = email, password = password)
    void testDeleteClientId() throws Exception {
        Client client = IntegrationTestUtils.createClient();
        client.setSalesman(salesman);
        clientRepository.save(client);

        mockMvc.perform(delete(API_CLIENTS_URL + "/" + client.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(client.getId()));
    }
}