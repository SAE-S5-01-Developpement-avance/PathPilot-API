/*
 * RouteServiceTest.java                                 28 Jan 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.services;

import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.routes.Route;
import fr.iut.pathpilotapi.routes.RouteRepository;
import fr.iut.pathpilotapi.routes.RouteService;
import fr.iut.pathpilotapi.itineraries.Itinerary;
import fr.iut.pathpilotapi.itineraries.ItineraryService;
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

class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private ItineraryService itineraryService;

    @InjectMocks
    private RouteService routeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRoutesFromSalesman() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Salesman salesman = new Salesman();

        Page<Route> expectedPage = new PageImpl<>(Collections.emptyList());

        when(routeRepository.findAllBySalesmanId(salesman.getId(), pageRequest)).thenReturn(expectedPage);

        Page<Route> result = routeService.getAllRoutesFromSalesman(salesman, pageRequest);

        assertEquals(expectedPage, result);
        verify(routeRepository, times(1)).findAllBySalesmanId(salesman.getId(), pageRequest);
    }

    @Test
    void testCreateRoute() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        List<ClientDTO> clients = Collections.emptyList();
        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesman, clients);
        Route route = new Route();
        route.setSalesmanId(salesman.getId());

        when(itineraryService.findByIdAndConnectedSalesman(itinerary.getId(), salesman)).thenReturn(itinerary);
        when(routeRepository.save(any(Route.class))).thenReturn(route);

        Route result = routeService.createRoute(itinerary.getId(), salesman);

        assertNotNull(result);
        assertEquals(salesman.getId(), result.getSalesmanId());
        verify(routeRepository, times(1)).save(any(Route.class));
    }

    @Test
    void testFindByIdAndConnectedSalesman() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        List<ClientDTO> clients = Collections.emptyList();
        Route route = IntegrationTestUtils.createRoute(salesman, clients);

        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));

        Route result = routeService.findByIdAndConnectedSalesman(route.getId(), salesman);

        assertEquals(route, result);
        verify(routeRepository, times(1)).findById(route.getId());
    }

    @Test
    void testFindByIdAndConnectedSalesmanNotFound() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        String routeId = "1";

        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> routeService.findByIdAndConnectedSalesman(routeId, salesman));

        assertEquals("Route not found with ID: " + routeId, exception.getMessage());
        verify(routeRepository, times(1)).findById(routeId);
    }

    @Test
    void testDeleteByIdAndConnectedSalesman() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        List<ClientDTO> clients = Collections.emptyList();
        Route route = IntegrationTestUtils.createRoute(salesman, clients);

        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));
        doNothing().when(routeRepository).delete(route);

        routeService.deleteByIdAndConnectedSalesman(route.getId(), salesman);

        verify(routeRepository, times(1)).findById(route.getId());
        verify(routeRepository, times(1)).delete(route);
    }

    @Test
    void testDeleteByIdAndConnectedSalesmanNotFound() {
        Salesman salesman = IntegrationTestUtils.createSalesman();
        String routeId = "1";

        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> routeService.deleteByIdAndConnectedSalesman(routeId, salesman));

        assertEquals("Route not found with ID: " + routeId, exception.getMessage());
        verify(routeRepository, times(1)).findById(routeId);
        verify(routeRepository, never()).delete(any(Route.class));
    }

    @Test
    void testRouteBelongToSalesman() {
        Salesman salesman = new Salesman();
        salesman.setId(1);
        Route route = new Route();
        route.setSalesmanId(1);

        boolean result = routeService.routeBelongToSalesman(route, salesman);

        assertTrue(result);
    }

    @Test
    void testRouteNotBelongToSalesman() {
        Salesman salesman1 = new Salesman();
        salesman1.setId(1);
        Salesman salesman2 = new Salesman();
        salesman2.setId(2);
        Route route = new Route();
        route.setSalesmanId(1);

        boolean result = routeService.routeBelongToSalesman(route, salesman2);

        assertFalse(result);
    }
}