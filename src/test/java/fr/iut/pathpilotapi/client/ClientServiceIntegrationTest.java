package fr.iut.pathpilotapi.client;

import fr.iut.pathpilotapi.client.repository.ClientRepository;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test the ClientService class with a real database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class ClientServiceIntegrationTest {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientService clientService;
    @Autowired
    private SalesmanRepository salesmanRepository;

    @Test
    void testGetAllClientsPageable() {
        assertEquals(0, clientRepository.findAll().size(), "The database should be empty");
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);

        //Given a client in the database
        Client client = IntegrationTestUtils.createClient();
        client.setSalesman(salesman);
        clientRepository.save(client);

        //When we're getting all clients
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Client> clients = clientService.getAllClientsBySalesmanPageable(client.getSalesman(), pageRequest).getContent();

        //Then the client should be in the list
        assertEquals(1, clients.size(), "There should be one client in the database");
        assertEquals(client, clients.getFirst(), "The client should be the one in the database");
    }

    @Test
    void testGetAllClients() {
        assertEquals(0, clientRepository.findAll().size(), "The database should be empty");
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);

        //Given a client in the database
        Client client = IntegrationTestUtils.createClient();
        client.setSalesman(salesman);
        clientRepository.save(client);

        //When we're getting all clients
        List<Client> clients = clientService.getAllClientsBySalesman(client.getSalesman());

        //Then the client should be in the list
        assertEquals(1, clients.size(), "There should be one client in the database");
        assertEquals(client, clients.getFirst(), "The client should be the one in the database");
    }

    @Test
    void testAddClient() {
        // Given a client
        Client client = IntegrationTestUtils.createClient();
        client.setClientCategory(new ClientCategory("test"));
        Salesman salesman = IntegrationTestUtils.createSalesman();

        // When we're adding the client
        Client createdClient = clientService.addClient(client, salesman);

        // Then the client should be in the database
        assertEquals(client, createdClient, "The client should be the one we added");
        assertTrue(clientRepository.findById(createdClient.getId()).isPresent(), "The client should be in the database");
    }

    @Test
    void testDeleteById() {
        // Given a client in the database
        Client client = IntegrationTestUtils.createClient();
        client.setClientCategory(new ClientCategory("test"));
        clientRepository.save(client);

        // When we're deleting the client
        boolean deleted = clientService.deleteByIdAndConnectedSalesman(client.getId(), client.getSalesman());

        // Then the client should not be in the database
        assertTrue(deleted, "The client should be deleted");
        assertTrue(clientRepository.findById(client.getId()).isEmpty(), "The client should not be in the database");
    }

    @Test
    void testFindById() {
        // Given a client in the database
        Client client = IntegrationTestUtils.createClient();
        clientRepository.save(client);

        // When we're getting the client by its id
        Client foundClient = clientService.findByIdAndConnectedSalesman(client.getId(), client.getSalesman());

        // Then the client should be the one in the database
        assertEquals(client, foundClient, "The client should be the one in the database");
    }

}