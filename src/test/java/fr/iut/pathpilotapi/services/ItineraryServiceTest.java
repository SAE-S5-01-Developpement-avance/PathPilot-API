package fr.iut.pathpilotapi.services;

import fr.iut.pathpilotapi.client.ClientService;
import fr.iut.pathpilotapi.itineraries.Itinerary;
import fr.iut.pathpilotapi.itineraries.ItineraryRepository;
import fr.iut.pathpilotapi.itineraries.ItineraryService;
import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.itineraries.dto.ItineraryRequestModel;
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

class ItineraryServiceTest {

    @Mock
    private ItineraryRepository itineraryRepository;

    @Mock
    private ClientService clientService;

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

        when(itineraryRepository.findAllBySalesman(salesman.getId(), pageRequest)).thenReturn(expectedPage);

        Page<Itinerary> result = itineraryService.getAllItinerariesFromSalesman(salesman, pageRequest);

        assertEquals(expectedPage, result);
        verify(itineraryRepository, times(1)).findAllBySalesman(salesman.getId(), pageRequest);
    }

    @Test
    void testAddItinerary() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        List<ClientDTO> clientsSchedule = Collections.emptyList();
        ItineraryRequestModel itineraryRequestModel = IntegrationTestUtils.createItineraryRequestModel(clientsSchedule);
        Itinerary itinerary = new Itinerary();
        itinerary.setSalesman_id(salesman.getId());

        when(clientService.findByIdAndConnectedSalesman(anyString(), eq(salesman))).thenReturn(IntegrationTestUtils.createClient());
        when(itineraryRepository.save(any(Itinerary.class))).thenReturn(itinerary);

        Itinerary result = itineraryService.addItinerary(itineraryRequestModel, salesman);

        assertNotNull(result);
        assertEquals(salesman.getId(), result.getSalesman_id());
        verify(itineraryRepository, times(1)).save(any(Itinerary.class));
    }

    @Test
    void testFindByIdAndConnectedSalesman() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesman);

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
        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesman);

        when(itineraryRepository.findById(itinerary.getId())).thenReturn(Optional.of(itinerary));
        doNothing().when(itineraryRepository).delete(itinerary);

        boolean result = itineraryService.delete(itinerary, salesman);

        assertTrue(result);
        verify(itineraryRepository, times(1)).findById(itinerary.getId());
        verify(itineraryRepository, times(1)).delete(itinerary);
    }

    @Test
    void testDeleteByIdAndConnectedSalesmanNotFound() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesman);

        when(itineraryRepository.findById(itinerary.getId())).thenReturn(Optional.empty());

        boolean result = itineraryService.delete(itinerary, salesman);

        assertFalse(result);
        verify(itineraryRepository, times(1)).findById(itinerary.getId());
        verify(itineraryRepository, never()).delete(any(Itinerary.class));
    }

    @Test
    void testItineraryBelongToSalesman() {
        Salesman salesman = new Salesman();
        salesman.setId(1);
        Itinerary itinerary = new Itinerary();
        itinerary.setSalesman_id(1);

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
        itinerary.setSalesman_id(1);

        boolean result = itineraryService.itineraryBelongToSalesman(itinerary, salesman2);

        assertFalse(result);
    }
}