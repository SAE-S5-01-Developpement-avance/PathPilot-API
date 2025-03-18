package fr.iut.pathpilotapi.itineraries;

import fr.iut.pathpilotapi.clients.entity.Client;
import fr.iut.pathpilotapi.clients.service.ClientService;
import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.itineraries.dto.ItineraryRequestModel;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItineraryServiceTest {

    @Mock
    private ItineraryRepository itineraryRepository;
    @Mock
    private SalesmanRepository salesmanRepository;

    @Mock
    private ClientService clientService;

    @Mock
    private WebClient oRSWebCLient;

    @InjectMocks
    private ItineraryService itineraryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllItinerariesFromSalesman() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Salesman salesman = new Salesman();

        Page<Itinerary> expectedPage = new PageImpl<>(Collections.emptyList());

        when(itineraryRepository.findAllBySalesmanId(salesman.getId(), pageRequest)).thenReturn(expectedPage);

        Page<Itinerary> result = itineraryService.getAllItinerariesFromSalesmanPageable(salesman, pageRequest);

        assertEquals(expectedPage, result);
        verify(itineraryRepository, times(1)).findAllBySalesmanId(salesman.getId(), pageRequest);
    }

    @Test
    void testGetAllItinerariesBySalesman() {
        // Given a salesman with some itineraries
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        List<ClientDTO> clients = Collections.emptyList();
        Itinerary itinerary1 = IntegrationTestUtils.createItinerary(salesman, clients);
        Itinerary itinerary2 = IntegrationTestUtils.createItinerary(salesman, clients);
        List<Itinerary> itineraries = List.of(itinerary1, itinerary2);

        // When we want to get all itineraries of this salesman
        when(itineraryRepository.findAllItinerariesBySalesmanId(salesman.getId())).thenReturn(itineraries);
        List<Itinerary> result = itineraryService.getAllItinerariesFromSalesman(salesman);

        assertEquals(itineraries, result);
        verify(itineraryRepository, times(1)).findAllItinerariesBySalesmanId(salesman.getId());
    }

    @Test
    void testCreateItinerary() {
        Salesman salesman = IntegrationTestUtils.createSalesman();

        Client client = IntegrationTestUtils.createClient();
        client.setId(1);
        client.setSalesman(salesman);

        ClientDTO clientDTO = IntegrationTestUtils.createClientDTO(1);
        List<ClientDTO> clientsSchedule = List.of(clientDTO);

        ItineraryRequestModel itineraryRequestModel = IntegrationTestUtils.createItineraryRequestModel(clientsSchedule);
        Itinerary itinerary = new Itinerary();
        itinerary.setSalesmanId(salesman.getId());

        when(clientService.findByIdAndConnectedSalesman(client.getId(), salesman)).thenReturn(client);
        when(itineraryRepository.save(any(Itinerary.class))).thenReturn(itinerary);

        Itinerary result = itineraryService.createItinerary(itineraryRequestModel, salesman, Collections.emptyList());

        assertNotNull(result);
        assertEquals(salesman.getId(), result.getSalesmanId());
        verify(itineraryRepository, times(1)).save(any(Itinerary.class));
    }

    @Test
    void testCreateItineraryWithClientsNotBelongToSalesman() {
        // Given two Salesmen
        Salesman salesman = IntegrationTestUtils.createSalesman();
        Salesman anotherSalesman = IntegrationTestUtils.createSalesman();

        // When we create an itinerary with a client that belong to a different salesman
        Client client = IntegrationTestUtils.createClient();
        client.setId(1);
        client.setSalesman(anotherSalesman); // Different salesman

        ClientDTO clientDTO = IntegrationTestUtils.createClientDTO(1);
        List<ClientDTO> clientsSchedule = List.of(clientDTO);

        ItineraryRequestModel itineraryRequestModel = IntegrationTestUtils.createItineraryRequestModel(clientsSchedule);

        when(clientService.findByIdAndConnectedSalesman(client.getId(), salesman)).thenThrow(new IllegalArgumentException(ItineraryService.ITINERARY_NOT_BELONGS_TO_SALESMAN));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> itineraryService.createItinerary(itineraryRequestModel, salesman, Collections.emptyList()));

        assertEquals(ItineraryService.ITINERARY_NOT_BELONGS_TO_SALESMAN, exception.getMessage());
        verify(itineraryRepository, never()).save(any(Itinerary.class));
    }

    @Test
    void testFindByIdAndConnectedSalesman() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        List<ClientDTO> clients = Collections.emptyList();
        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesman, clients);

        when(itineraryRepository.findById(itinerary.getId())).thenReturn(Optional.of(itinerary));

        Itinerary result = itineraryService.findByIdAndConnectedSalesman(itinerary.getId(), salesman);

        assertEquals(itinerary, result);
        verify(itineraryRepository, times(1)).findById(itinerary.getId());
    }

    @Test
    void testFindByIdAndConnectedSalesmanNotFound() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        String itineraryId = "1";

        when(itineraryRepository.findById(itineraryId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> itineraryService.findByIdAndConnectedSalesman(itineraryId, salesman));

        assertEquals("Itinerary not found with ID: " + itineraryId, exception.getMessage());
        verify(itineraryRepository, times(1)).findById(itineraryId);
    }

    @Test
    void testDeleteByIdAndConnectedSalesman() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);

        List<ClientDTO> clients = Collections.emptyList();
        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesman, clients);

        when(itineraryRepository.findById(itinerary.getId())).thenReturn(Optional.of(itinerary));
        doNothing().when(itineraryRepository).delete(itinerary);

        itineraryService.deleteByIdAndConnectedSalesman(itinerary.getId(), salesman);

        verify(itineraryRepository, times(1)).findById(itinerary.getId());
        verify(itineraryRepository, times(1)).delete(itinerary);
    }

    @Test
    void testDeleteByIdAndConnectedSalesmanNotFound() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        List<ClientDTO> clients = Collections.emptyList();
        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesman, clients);

        when(itineraryRepository.findById(itinerary.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> itineraryService.deleteByIdAndConnectedSalesman(itinerary.getId(), salesman));

        assertEquals("Itinerary not found with ID: " + itinerary.getId(), exception.getMessage());
        verify(itineraryRepository, times(1)).findById(itinerary.getId());
        verify(itineraryRepository, never()).delete(any(Itinerary.class));
    }

    @Test
    void testItineraryBelongToSalesman() {
        Salesman salesman = new Salesman();
        salesman.setId(1);
        Itinerary itinerary = new Itinerary();
        itinerary.setSalesmanId(1);

        boolean result = itineraryService.itineraryBelongToSalesman(itinerary, salesman);

        assertTrue(result);
    }

    @Test
    void testItineraryNotBelongToSalesman() {
        Salesman salesman1 = new Salesman();
        salesman1.setId(1);
        Salesman salesman2 = new Salesman();
        salesman2.setId(2);
        Itinerary itinerary = new Itinerary();
        itinerary.setSalesmanId(1);

        boolean result = itineraryService.itineraryBelongToSalesman(itinerary, salesman2);

        assertFalse(result);
    }

    @Test
    void testItineraryBelongToSalesmanButNull() {
        // Given a salesman and an itinerary null
        Salesman salesman = new Salesman();
        Itinerary itineraryNull = null;

        // Then when we call the method an exception is expected
        assertThrows(IllegalArgumentException.class, () -> {
            itineraryService.itineraryBelongToSalesman(itineraryNull, salesman);
        });
    }

    @Test
    void testFindItineraryButItineraryDoesntBelongToSalesman() {
        Salesman salesman1 = new Salesman();
        salesman1.setEmailAddress("first@email.com");

        Salesman salesman2 = new Salesman();
        salesman2.setEmailAddress("second@email.com");
        salesman2.setId(1);

        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesman1, List.of());
        itinerary.setId("id");
        when(itineraryRepository.findById(itinerary.getId())).thenReturn(Optional.of(itinerary));

        final Salesman finalSalesman = salesman2;
        assertThrows(IllegalArgumentException.class, () -> {
            itineraryService.findByIdAndConnectedSalesman(itinerary.getId(), finalSalesman);
        });
    }
}