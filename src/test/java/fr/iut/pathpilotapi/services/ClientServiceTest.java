/*
 * ClientServiceTest.java                                 28 nov. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.services;

import fr.iut.pathpilotapi.client.Client;
import fr.iut.pathpilotapi.client.ClientCategory;
import fr.iut.pathpilotapi.client.repository.ClientCategoryRepository;
import fr.iut.pathpilotapi.client.repository.ClientRepository;
import fr.iut.pathpilotapi.client.ClientService;
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
        PageRequest pageRequest = PageRequest.of(0, 10);
        Salesman salesman = new Salesman();

        Page<Client> expectedPage = new PageImpl<>(Collections.emptyList());

        //Used to mock the method call to the repository (because we want to test the service, not the repository)
        when(clientRepository.findAllBySalesman(salesman, pageRequest)).thenReturn(expectedPage);

        //Call the method to test
        Page<Client> result = clientService.getAllClientsBySalesmanPageable(salesman, pageRequest);

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
    void testDeleteById() {
        Client client = new Client();

        //Used to mock the method call to the repository (because we want to test the service, not the repository)
        doNothing().when(clientRepository).delete(client);

        boolean result = clientService.deleteByIdAndConnectedSalesman(client.getId(), client.getSalesman());

        assertTrue(result);
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    void testDeleteByIdNotFound() {
        Client client = new Client();

        //Used to mock the method call to the repository (because we want to test the service, not the repository)
        doThrow(new IllegalArgumentException("Client not found")).when(clientRepository).delete(client);

        boolean result = clientService.deleteByIdAndConnectedSalesman(client.getId(), client.getSalesman());

        assertFalse(result);
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    void testFindById() {
        Integer clientId = 1;
        Client client = new Client();

        //Used to mock the method call to the repository (because we want to test the service, not the repository)
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        Client result = clientService.findByIdAndConnectedSalesman(clientId, client.getSalesman());

        assertEquals(client, result);
        verify(clientRepository, times(1)).findById(clientId);
    }

    @Test
    void testGetClientByIdNotFound() {
        Integer clientId = 1;

        //Used to mock the method call to the repository (because we want to test the service, not the repository)
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> clientService.findByIdAndConnectedSalesman(clientId, new Salesman()));

        assertEquals("Client not found", exception.getMessage());
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

    @Test
    void testDeleteClientThrowsException() {
        Client client = new Client();

        //Used to mock the method call to the repository (because we want to test the service, not the repository)
        doThrow(new RuntimeException("Unexpected error")).when(clientRepository).delete(client);

        boolean result = clientService.deleteByIdAndConnectedSalesman(client.getId(), client.getSalesman());

        assertFalse(result);
        verify(clientRepository, times(1)).delete(client);
    }
}