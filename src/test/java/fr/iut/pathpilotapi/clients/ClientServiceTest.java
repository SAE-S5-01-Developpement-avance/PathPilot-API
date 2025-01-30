/*
 * ClientServiceTest.java                                 28 nov. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.clients;

import fr.iut.pathpilotapi.auth.exceptions.ObjectNotFoundException;
import fr.iut.pathpilotapi.clients.dto.ClientRequestModel;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientCategoryService clientCategoryService;

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
        // Given a client request model and a client category
        ClientRequestModel clientRM = IntegrationTestUtils.createClientRM();
        ClientCategory category = new ClientCategory("TEST");
        clientRM.setClientCategory(category.getName());

        // When we're adding the client
        when(clientCategoryService.findByName(clientRM.getClientCategory())).thenReturn(category);
        when(clientRepository.save(any(Client.class))).thenReturn(new Client());

        Client result = clientService.addClient(clientRM, new Salesman());

        // Then the client should be added
        assertNotNull(result);
        verify(clientCategoryService, times(1)).findByName(clientRM.getClientCategory());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void testDeleteByIdAndConnectedSalesman() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        Client client = IntegrationTestUtils.createClient(salesman);
        client.setSalesman(salesman);

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        clientService.deleteByIdAndConnectedSalesman(client.getId(), salesman);

        verify(clientRepository, times(1)).findById(client.getId());
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    void testDeleteByIdAndConnectedSalesmanNotFound() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        Client client = IntegrationTestUtils.createClient(salesman);
        client.setSalesman(salesman);

        when(clientRepository.findById(client.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ObjectNotFoundException.class, () -> clientService.deleteByIdAndConnectedSalesman(client.getId(), salesman));

        assertEquals("Client not found with ID: " + client.getId(), exception.getMessage());
        verify(clientRepository, times(1)).findById(client.getId());
        verify(clientRepository, never()).delete(any(Client.class));
    }

    @Test
    void testFindClientButClientDoesntBelongToSalesman() {
        Salesman salesman1 = new Salesman();
        salesman1.setEmailAddress("first@email.com");

        Salesman salesman2 = new Salesman();
        salesman2.setEmailAddress("second@email.com");
        salesman2.setId(1);

        Client client = IntegrationTestUtils.createClient(salesman1);
        client.setId(1);

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        final Salesman finalSalesman = salesman2;
        assertThrows(IllegalArgumentException.class, () -> {
            clientService.findByIdAndConnectedSalesman(client.getId(), finalSalesman);
        });
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

    @Test
    void testClientBelongToSalesmanWhenClientIsNull() {
        // Given a route that is null
        Salesman salesman = new Salesman();
        Client clientNull = null;

        // Then an exception is throw when we call the method
        assertThrows(IllegalArgumentException.class, () -> {
            clientService.clientBelongToSalesman(clientNull, salesman);
        });
    }

    @Test
    void getAllClientsButACLientDoesntExist() {
        // Given a list of client but one of them doesn't exist
        Client client1 = new Client();
        Client client2 = new Client();
        Client client3 = new Client();
        client1.setId(1);
        client2.setId(2);
        client3.setId(3);
        List<Integer> clientsSchedule = List.of(client1.getId(), client2.getId(), client3.getId(), 123);
        // The client 123 doesn't exist
        when(clientRepository.findAllById(clientsSchedule)).thenReturn(List.of(client1, client2, client3));

        Salesman salesman = new Salesman();

        // Then an exception is throw when the getAllClients is called with this list
        assertThrows(IllegalArgumentException.class, () -> {
            clientService.getAllClients(clientsSchedule, salesman);
        });
    }

    @Test
    void getAllClientsButAClientDoesntBelongToSalesman() {
        // Given a list of client but one of them doesn't exist
        Client client1 = new Client();
        Client client2 = new Client();
        Client client3 = new Client();
        client1.setId(1);
        client2.setId(2);
        client3.setId(3);

        Salesman salesman = new Salesman();

        client1.setSalesman(salesman);
        client2.setSalesman(salesman);

        List<Integer> clientsSchedule = List.of(client1.getId(), client2.getId(), client3.getId());
        // The client 123 doesn't exist
        when(clientRepository.findAllById(clientsSchedule)).thenReturn(List.of(client1, client2, client3));

        // Then an exception is throw when the getAllClients is called with this list
        assertThrows(IllegalArgumentException.class, () -> {
            clientService.getAllClients(clientsSchedule, salesman);
        });
    }

    @Test
    void getAllClients() {
        // Given a list of client but one of them doesn't exist
        Client client1 = new Client();
        Client client2 = new Client();
        Client client3 = new Client();
        client1.setId(1);
        client2.setId(2);
        client3.setId(3);

        Salesman salesman = new Salesman();

        client1.setSalesman(salesman);
        client2.setSalesman(salesman);
        client3.setSalesman(salesman);

        List<Integer> clientsSchedule = List.of(client1.getId(), client2.getId(), client3.getId());
        // The client 123 doesn't exist
        when(clientRepository.findAllById(clientsSchedule)).thenReturn(List.of(client1, client2, client3));

        assertEquals(List.of(client1, client2, client3), clientService.getAllClients(clientsSchedule, salesman));
    }
}