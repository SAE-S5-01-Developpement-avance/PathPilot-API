package fr.iut.pathpilotapi.clients;

import fr.iut.pathpilotapi.WithMockSalesman;
import fr.iut.pathpilotapi.clients.dto.ClientRequestModel;
import fr.iut.pathpilotapi.clients.entity.Client;
import fr.iut.pathpilotapi.clients.repository.ClientCategoryRepository;
import fr.iut.pathpilotapi.clients.repository.ClientRepository;
import fr.iut.pathpilotapi.clients.service.ClientCategoryService;
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

import static fr.iut.pathpilotapi.Constants.MAX_LENGTH;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the ClientRestController class.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class ClientControllerIntegrationTest {

    private static final String API_CLIENTS_URL = "/clients";
    private static final String EMAIL_SALESMAN_CONNECTED = "john.doe@test.com";
    private static final String PASSWORD_SALESMAN_CONNECTED = "12345";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SalesmanRepository salesmanRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientCategoryRepository clientCategoryRepository;
    @Autowired
    private ClientCategoryService clientCategoryService;

    @BeforeTestExecution
    void saveSalesman() {
        salesmanRepository.save(
                IntegrationTestUtils.createSalesman(EMAIL_SALESMAN_CONNECTED, PASSWORD_SALESMAN_CONNECTED)
        );
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testGetAllClientsPage() throws Exception {
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
                .andExpect(jsonPath("_embedded.clientResponseModelList", hasSize(1)));
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testGetClientsFromSalesmanEmptyPage() throws Exception {
        //Given an empty database
        // When we're getting all clients from the salesman and there are none
        mockMvc.perform(get(API_CLIENTS_URL))
                // Then we should get a 204 No Content status
                .andExpect(status().isOk());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testGetAllClients() throws Exception {
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        Client client1 = IntegrationTestUtils.createClient();
        Client client2 = IntegrationTestUtils.createClient();
        Client client3 = IntegrationTestUtils.createClient();
        client1.setSalesman(salesmanConnected);
        client2.setSalesman(salesmanConnected);

        // Given three clients in the database and two linked to the connected salesman
        clientRepository.saveAll(List.of(client1, client2, client3));

        // When we're getting all clients
        mockMvc.perform(get(API_CLIENTS_URL + "/all"))

                // Then we should get the client back
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.clientResponseModelList", hasSize(2)));
    }

    @Test
    @Transactional
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddClient() throws Exception {
        // Given a client
        ClientRequestModel clientRM = IntegrationTestUtils.createClientRM();
        String clientCategory = clientCategoryService.findByName("PROSPECT").getName();
        clientRM.setClientCategory(clientCategory);

        // When we're adding the client
        mockMvc.perform(post(API_CLIENTS_URL)
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(clientRM)))

                // Then we should get the client back
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyName", Matchers.is(clientRM.getCompanyName())));
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
                .andExpect(status().isOk());
        assertFalse(clientRepository.findAll().contains(client), "The database should be empty");
    }


    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testGetClientById() throws Exception {
        // Given a client in the database linked to the connected salesman
        Salesman salesmanConnected = salesmanRepository.findByEmailAddress(EMAIL_SALESMAN_CONNECTED).orElseThrow();

        Client client1 = IntegrationTestUtils.createClient();

        client1.setSalesman(salesmanConnected);

        // Given two clients in the database and one linked to the connected salesman
        clientRepository.saveAll(List.of(client1));

        // When we're getting all clients
        mockMvc.perform(get(API_CLIENTS_URL + "/" + client1.getId()))

                // Then we should get the client back
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", Matchers.is(client1.getId())));
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddClientButWithDescriptionTooLong() throws Exception {
        // Given a description that is too long
        String descriptionToLong = "a".repeat(1001);

        // When we're adding the client with the description
        ClientRequestModel clientRM = IntegrationTestUtils.createClientRM();
        clientRM.setDescription(descriptionToLong);

        mockMvc.perform(post(API_CLIENTS_URL)
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(clientRM)))

                // Then we should get an error
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddClientButWithLatitudeOverTheMax() throws Exception {
        // Given an invalid latitude
        double invalidLatitude = 91.0;

        // When we're adding the client with the invalid latitude
        ClientRequestModel clientRM = IntegrationTestUtils.createClientRM();
        clientRM.setLatHomeAddress(invalidLatitude);

        mockMvc.perform(post(API_CLIENTS_URL)
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(clientRM)))

                // Then we should get an error
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddClientButWithLongitudeOverTheMax() throws Exception {
        // Given an invalid longitude
        double invalidLongitude = 181.0;

        // When we're adding the client with the invalid longitude
        ClientRequestModel clientRM = IntegrationTestUtils.createClientRM();
        clientRM.setLongHomeAddress(invalidLongitude);

        mockMvc.perform(post(API_CLIENTS_URL)
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(clientRM)))

                // Then we should get an error
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddClientButWithLatitudeUnderTheMin() throws Exception {
        // Given an invalid latitude
        double invalidLatitude = -91.0;

        // When we're adding the client with the invalid latitude
        ClientRequestModel clientRM = IntegrationTestUtils.createClientRM();
        clientRM.setLatHomeAddress(invalidLatitude);

        mockMvc.perform(post(API_CLIENTS_URL)
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(clientRM)))

                // Then we should get an error
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddClientButWithLongitudeUnderTheMin() throws Exception {
        // Given an invalid longitude
        double invalidLongitude = -181.0;

        // When we're adding the client with the invalid longitude
        ClientRequestModel clientRM = IntegrationTestUtils.createClientRM();
        clientRM.setLongHomeAddress(invalidLongitude);

        mockMvc.perform(post(API_CLIENTS_URL)
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(clientRM)))

                // Then we should get an error
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddClientButWithInvalidClientCategory() throws Exception {
        // Given an invalid client category
        String invalidClientCategory = "INVALID";

        // When we're adding the client with the invalid client category
        ClientRequestModel clientRM = IntegrationTestUtils.createClientRM();
        clientRM.setClientCategory(invalidClientCategory);

        mockMvc.perform(post(API_CLIENTS_URL)
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(clientRM)))

                // Then we should get an error
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddClientButWithInvalidPhoneNumber() throws Exception {
        // Given an invalid phone number
        String invalidPhoneNumber = "INVALID";

        // When we're adding the client with the invalid phone number
        ClientRequestModel clientRM = IntegrationTestUtils.createClientRM();
        clientRM.setPhoneNumber(invalidPhoneNumber);

        mockMvc.perform(post(API_CLIENTS_URL)
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(clientRM)))

                // Then we should get an error
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddClientButWithEmptyCompanyName() throws Exception {
        // Given an empty company name
        String emptyCompanyName = "";

        // When we're adding the client with the empty company name
        ClientRequestModel clientRM = IntegrationTestUtils.createClientRM();
        clientRM.setCompanyName(emptyCompanyName);
        assertTrue(clientRM.getCompanyName().isEmpty());

        mockMvc.perform(post(API_CLIENTS_URL)
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(clientRM)))

                // Then we should get an error
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddClientButWithContactFirstNameToLong() throws Exception {
        // Given a contact name that is too long
        String contactFirstNameToLong = "a".repeat(MAX_LENGTH + 1);

        // When we're adding the client with the contact name
        ClientRequestModel clientRM = IntegrationTestUtils.createClientRM();
        clientRM.setContactFirstName(contactFirstNameToLong);

        mockMvc.perform(post(API_CLIENTS_URL)
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(clientRM)))

                // Then we should get an error
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockSalesman(email = EMAIL_SALESMAN_CONNECTED, password = PASSWORD_SALESMAN_CONNECTED)
    void testAddClientButWithContactLastNameToLong() throws Exception {
        // Given a contact name that is too long
        String contactLastNameToLong = "a".repeat(MAX_LENGTH + 1);

        // When we're adding the client with the contact name
        ClientRequestModel clientRM = IntegrationTestUtils.createClientRM();
        clientRM.setContactLastName(contactLastNameToLong);

        mockMvc.perform(post(API_CLIENTS_URL)
                        .contentType("application/json")
                        .content(IntegrationTestUtils.asJsonString(clientRM)))

                // Then we should get an error
                .andExpect(status().isBadRequest());
    }
}