/*
 * ClientServiceTest.java                                 28 nov. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.services;

import fr.iut.pathpilotapi.clients.Client;
import fr.iut.pathpilotapi.clients.ClientCategory;
import fr.iut.pathpilotapi.clients.ClientService;
import fr.iut.pathpilotapi.clients.repository.ClientCategoryRepository;
import fr.iut.pathpilotapi.clients.repository.ClientRepository;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientCategoryRepository clientCategoryRepository;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllClients() {
        // Given a page request and no clients in the database
        PageRequest pageRequest = PageRequest.of(0, 10);
        Salesman salesman = new Salesman();

        Page<Client> expectedPage = new PageImpl<>(Collections.emptyList());

        //Used to mock the method call to the repository (because we want to test the service, not the repository)
        when(clientRepository.findAllBySalesman(salesman, pageRequest)).thenReturn(expectedPage);

        // When we're getting all the clients with the given page request.
        //Call the method to test
        Page<Client> result = clientService.getAllClientsBySalesmanPageable(salesman, pageRequest);

        // Then the result should be an empty page
        assertEquals(expectedPage, result);
        verify(clientRepository, times(1)).findAllBySalesman(salesman, pageRequest);
    }

    @Test
    void testAddClient() {
        Client client = IntegrationTestUtils.createClient();
        client.setClientCategory(new ClientCategory("test"));
        //Used to mock the method call to the repository (because we want to test the service, not the repository)
        when(clientRepository.save(client)).thenReturn(client);

        Client result = clientService.addClient(client, new Salesman());

        assertEquals(client, result);
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testDeleteByIdAndConnectedSalesman() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        Client client = IntegrationTestUtils.createClient(salesman);
        client.setSalesman(salesman);

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        doNothing().when(clientRepository).delete(client);

        boolean result = clientService.deleteByIdAndConnectedSalesman(client.getId(), salesman);

        assertTrue(result);
        verify(clientRepository, times(1)).findById(client.getId());
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    void testDeleteByIdAndConnectedSalesmanNotFound() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        Client client = IntegrationTestUtils.createClient(salesman);
        client.setSalesman(salesman);

        when(clientRepository.findById(client.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> clientService.deleteByIdAndConnectedSalesman(client.getId(), salesman));

        assertEquals("Client not found with ID: " + client.getId(), exception.getMessage());
        verify(clientRepository, times(1)).findById(client.getId());
        verify(clientRepository, never()).delete(any(Client.class));
    }

    @Test
    void testFindById() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        Client client = IntegrationTestUtils.createClient(salesman);
        client.setSalesman(salesman);

        //Used to mock the method call to the repository (because we want to test the service, not the repository)
        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        Client result = clientService.findByIdAndConnectedSalesman(client.getId(), client.getSalesman());

        assertEquals(client, result);
        verify(clientRepository, times(1)).findById(client.getId());
    }

    @Test
    void testGetClientByIdNotFound() {
        Integer clientId = 1;

        //Used to mock the method call to the repository (because we want to test the service, not the repository)
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> clientService.findByIdAndConnectedSalesman(clientId, new Salesman()));

        assertEquals("Client not found with ID: 1", exception.getMessage());
        verify(clientRepository, times(1)).findById(clientId);
    }

    @Test
    void testIsClientBelongToSalesman() {
        Salesman salesman = new Salesman();
        salesman.setId(1);
        Client client = new Client();
        client.setSalesman(salesman);

        boolean result = clientService.clientBelongToSalesman(client, salesman);

        assertTrue(result);
    }

    @Test
    void testIsClientNotBelongToSalesman() {
        Salesman salesman1 = new Salesman();
        salesman1.setId(1);
        Salesman salesman2 = new Salesman();
        salesman2.setId(2);
        Client client = new Client();
        client.setSalesman(salesman1);

        boolean result = clientService.clientBelongToSalesman(client, salesman2);

        assertFalse(result);
    }
}