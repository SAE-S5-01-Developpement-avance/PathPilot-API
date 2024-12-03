/*
 * ClientServiceTest.java                                 28 nov. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.services;

import fr.iut.pathpilotapi.client.Client;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllClients() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Client> expectedPage = new PageImpl<>(Collections.emptyList());
        when(clientRepository.findAll(pageRequest)).thenReturn(expectedPage);

        Page<Client> result = clientService.getAllClients(pageRequest);

        assertEquals(expectedPage, result);
        verify(clientRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void testAddClient() {
        Client client = new Client(1 ,"IKEA", 48.8566, 2.3522, "CLIENT", "Description A", "Doe", "John","0123456789", new ArrayList<Salesman>());
        when(clientRepository.save(client)).thenReturn(client);

        Client result = clientService.addClient(client);

        assertEquals(client, result);
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testDeleteById() {
        Integer clientId = 1;
        doNothing().when(clientRepository).deleteById(clientId);

        boolean result = clientService.deleteById(clientId);

        assertTrue(result);
        verify(clientRepository, times(1)).deleteById(clientId);
    }

    @Test
    void testDeleteByIdNotFound() {
        Integer clientId = 1;
        doThrow(new IllegalArgumentException("Client not found")).when(clientRepository).deleteById(clientId);

        boolean result = clientService.deleteById(clientId);

        assertFalse(result);
        verify(clientRepository, times(1)).deleteById(clientId);
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

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.getClientById(clientId);
        });

        assertEquals("Client not found", exception.getMessage());
        verify(clientRepository, times(1)).findById(clientId);
    }
}