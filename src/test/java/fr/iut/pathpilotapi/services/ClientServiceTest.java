/*
 * ClientServiceTest.java                                 28 nov. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.services;

import fr.iut.pathpilotapi.client.Client;
import fr.iut.pathpilotapi.client.ClientCategoryRepository;
import fr.iut.pathpilotapi.client.ClientRepository;
import fr.iut.pathpilotapi.client.ClientService;
import fr.iut.pathpilotapi.salesman.Salesman;
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
        when(clientRepository.findAllBySalesman(salesman, pageRequest)).thenReturn(expectedPage);

        Page<Client> result = clientService.getAllClientsBySalesman(salesman, pageRequest);

        assertEquals(expectedPage, result);
        verify(clientRepository, times(1)).findAllBySalesman(salesman, pageRequest);
    }

    @Test
    void testAddClient() {
        Client client = new Client();
        when(clientRepository.save(client)).thenReturn(client);

        Client result = clientService.addClient(client, new Salesman());

        assertEquals(client, result);
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testDeleteById() {
        Client client = new Client();
        doNothing().when(clientRepository).delete(client);

        boolean result = clientService.delete(client);

        assertTrue(result);
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    void testDeleteByIdNotFound() {
        Client client = new Client();
        doThrow(new IllegalArgumentException("Client not found")).when(clientRepository).delete(client);

        boolean result = clientService.delete(client);

        assertFalse(result);
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    void testGetClientById() {
        Integer clientId = 1;
        Client client = new Client();
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        Client result = clientService.getClientById(clientId);

        assertEquals(client, result);
        verify(clientRepository, times(1)).findById(clientId);
    }

    @Test
    void testGetClientByIdNotFound() {
        Integer clientId = 1;
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> clientService.getClientById(clientId));

        assertEquals("Client not found", exception.getMessage());
        verify(clientRepository, times(1)).findById(clientId);
    }

    @Test
    void testIsClientBelongToSalesman() {
        Salesman salesman = new Salesman();
        salesman.setId(1);
        Client client = new Client();
        client.setSalesman(salesman);

        boolean result = clientService.isClientBelongToSalesman(client, salesman);

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

        boolean result = clientService.isClientBelongToSalesman(client, salesman2);

        assertFalse(result);
    }

    @Test
    void testDeleteClientException() {
        Client client = new Client();
        doThrow(new RuntimeException("Unexpected error")).when(clientRepository).delete(client);

        boolean result = clientService.delete(client);

        assertFalse(result);
        verify(clientRepository, times(1)).delete(client);
    }
}