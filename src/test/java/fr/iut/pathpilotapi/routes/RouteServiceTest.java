/*
 * RouteServiceTest.java                                 28 Jan 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.itineraries.Itinerary;
import fr.iut.pathpilotapi.itineraries.ItineraryService;
import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.routes.dto.ClientState;
import fr.iut.pathpilotapi.routes.dto.RouteClient;
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
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private MongoTemplate mongoTemplate;

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
        // Given a page request and a salesman
        PageRequest pageRequest = PageRequest.of(0, 10);
        Salesman salesman = new Salesman();
        Page<Route> expectedPage = new PageImpl<>(Collections.emptyList());
        when(routeRepository.findAllBySalesmanId(salesman.getId(), pageRequest)).thenReturn(expectedPage);

        // When getting all routes from the salesman
        Page<Route> result = routeService.getAllRoutesFromSalesman(salesman, pageRequest);

        // Then the result should be the expected page and the repository should be called once
        assertEquals(expectedPage, result);
        verify(routeRepository, times(1)).findAllBySalesmanId(salesman.getId(), pageRequest);
    }

    @Test
    void testCreateRoute() {
        // Given a salesman, clients, itinerary, and a route
        Salesman salesman = IntegrationTestUtils.createSalesman();
        List<ClientDTO> clients = Collections.emptyList();
        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesman, clients);
        Route route = new Route();
        route.setSalesmanId(salesman.getId());
        when(itineraryService.findByIdAndConnectedSalesman(itinerary.getId(), salesman)).thenReturn(itinerary);
        when(routeRepository.save(any(Route.class))).thenReturn(route);

        // When creating a route
        Route result = routeService.createRoute(itinerary.getId(), salesman);

        // Then the result should not be null, the salesman ID should match, and the repository should save the route once
        assertNotNull(result);
        assertEquals(salesman.getId(), result.getSalesmanId());
        verify(routeRepository, times(1)).save(any(Route.class));
    }

    @Test
    void testFindByIdAndConnectedSalesman() {
        // Given a salesman and a route
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        List<ClientDTO> clients = Collections.emptyList();
        Route route = IntegrationTestUtils.createRoute(salesman, clients);
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));

        // When finding a route by ID and connected salesman
        Route result = routeService.findByIdAndConnectedSalesman(route.getId(), salesman);

        // Then the result should be the expected route and the repository should be called once
        assertEquals(route, result);
        verify(routeRepository, times(1)).findById(route.getId());
    }

    @Test
    void testFindByIdAndConnectedSalesmanNotFound() {
        // Given a salesman and a route ID that does not exist
        Salesman salesman = IntegrationTestUtils.createSalesman();
        String routeId = "1";
        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        // When finding a route by ID and connected salesman
        Exception exception = assertThrows(IllegalArgumentException.class, () -> routeService.findByIdAndConnectedSalesman(routeId, salesman));

        // Then an exception should be thrown with the message "Route not found with ID: 1"
        assertEquals("Route not found with ID: " + routeId, exception.getMessage());
        verify(routeRepository, times(1)).findById(routeId);
    }

    @Test
    void testDeleteByIdAndConnectedSalesman() {
        // Given a salesman and a route
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        List<ClientDTO> clients = Collections.emptyList();
        Route route = IntegrationTestUtils.createRoute(salesman, clients);
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));
        doNothing().when(routeRepository).delete(route);

        // When deleting a route by ID and connected salesman
        routeService.deleteByIdAndConnectedSalesman(route.getId(), salesman);

        // Then the repository should find the route and delete it once
        verify(routeRepository, times(1)).findById(route.getId());
        verify(routeRepository, times(1)).delete(route);
    }

    @Test
    void testDeleteByIdAndConnectedSalesmanNotFound() {
        // Given a salesman and a route ID that does not exist
        Salesman salesman = IntegrationTestUtils.createSalesman();
        String routeId = "1";
        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        // When deleting a route by ID and connected salesman
        Exception exception = assertThrows(IllegalArgumentException.class, () -> routeService.deleteByIdAndConnectedSalesman(routeId, salesman));

        // Then an exception should be thrown with the message "Route not found with ID: 1" and the repository should not delete any route
        assertEquals("Route not found with ID: " + routeId, exception.getMessage());
        verify(routeRepository, times(1)).findById(routeId);
        verify(routeRepository, never()).delete(any(Route.class));
    }

    @Test
    void testRouteBelongToSalesman() {
        // Given a salesman and a route
        Salesman salesman = new Salesman();
        salesman.setId(1);
        Route route = new Route();
        route.setSalesmanId(1);

        // When checking if the route belongs to the salesman
        boolean result = routeService.routeBelongToSalesman(route, salesman);

        // Then the result should be true
        assertTrue(result);
    }

    @Test
    void testRouteNotBelongToSalesman() {
        // Given two salesmen and a route
        Salesman salesman1 = new Salesman();
        salesman1.setId(1);
        Salesman salesman2 = new Salesman();
        salesman2.setId(2);
        Route route = new Route();
        route.setSalesmanId(1);

        // When checking if the route belongs to the second salesman
        boolean result = routeService.routeBelongToSalesman(route, salesman2);

        // Then the result should be false
        assertFalse(result);
    }

    @Test
    void testRouteBelongToSalesmanWhenRouteIsNull() {
        // Given a route that is null
        Salesman salesman = new Salesman();
        Route routeNull = null;

        // Then an exception is thrown when we call the method
        assertThrows(IllegalArgumentException.class, () -> {
            // When checking if the null route belongs to the salesman
            routeService.routeBelongToSalesman(routeNull, salesman);
        });
    }

    @Test
    void testFindRouteButRouteDoesntBelongToSalesman() {
        // Given two salesmen and a route that belongs to the first salesman
        Salesman salesman1 = new Salesman();
        salesman1.setEmailAddress("first@email.com");
        Salesman salesman2 = new Salesman();
        salesman2.setEmailAddress("second@email.com");
        salesman2.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman1, List.of());
        route.setId("id");
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));

        // Then an exception is thrown when we call the method
        assertThrows(IllegalArgumentException.class, () -> {
            // When finding a route by ID and connected salesman
            routeService.findByIdAndConnectedSalesman(route.getId(), salesman2);
        });
    }

    @Test
    void testSetClientVisited() {
        // given a route, a client, and a state
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        ClientDTO client = new ClientDTO();
        client.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, List.of(client));
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));

        // when setting the client as visited
        routeService.setClientVisited(client.getId(), route.getId(), salesman);

        // then the client state should be VISITED
        assertEquals(ClientState.VISITED, route.getClients().getFirst().getState());
    }

    @Test
    void testSetClientVisitedButClientNotFound() {
        // given a route, a client, and a state
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        ClientDTO client = new ClientDTO();
        client.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, List.of(client));
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));

        // then an exception is thrown when we call the method
        assertThrows(IllegalArgumentException.class, () -> {
            // when setting the client as visited
            routeService.setClientVisited(2, route.getId(), salesman);
        });
    }

    @Test
    void testSetClientVisitedButRouteNotFound() {
        // given a route, a client, and a state
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        ClientDTO client = new ClientDTO();
        client.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, List.of(client));
        when(routeRepository.findById(route.getId())).thenReturn(Optional.empty());

        // then an exception is thrown when we call the method
        assertThrows(IllegalArgumentException.class, () -> {
            // when setting the client as visited
            routeService.setClientVisited(client.getId(), route.getId(), salesman);
        });
    }

    @Test
    void testSetClientVisitedButClientNotInRoute() {
        // given a route, a client, and a state
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        ClientDTO client = new ClientDTO();
        client.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, List.of(client));
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));

        // then an exception is thrown when we call the method
        assertThrows(IllegalArgumentException.class, () -> {
            // when setting the client as visited
            routeService.setClientVisited(2, route.getId(), salesman);
        });
    }

    @Test
    void testSetClientSkipped() {
        // given a route, a client, and a state
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        ClientDTO client = new ClientDTO();
        client.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, List.of(client));
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));

        // when setting the client as skipped
        routeService.setClientSkipped(client.getId(), route.getId(), salesman);

        // then the client state should be SKIPPED
        assertEquals(ClientState.SKIPPED, route.getClients().getFirst().getState());
    }

    @Test
    void testSetClientSkippedButClientNotFound() {
        // given a route, a client, and a state
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        ClientDTO client = new ClientDTO();
        client.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, List.of(client));
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));

        // then an exception is thrown when we call the method
        assertThrows(IllegalArgumentException.class, () -> {
            // when setting the client as skipped
            routeService.setClientSkipped(2, route.getId(), salesman);
        });
    }

    @Test
    void testSetClientSkippedButRouteNotFound() {
        // given a route, a client, and a state
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        ClientDTO client = new ClientDTO();
        client.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, List.of(client));
        when(routeRepository.findById(route.getId())).thenReturn(Optional.empty());

        // then an exception is thrown when we call the method
        assertThrows(IllegalArgumentException.class, () -> {
            // when setting the client as skipped
            routeService.setClientSkipped(client.getId(), route.getId(), salesman);
        });
    }

    @Test
    void testSetClientSkippedButClientNotInRoute() {
        // given a route, a client, and a state
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        ClientDTO client = new ClientDTO();
        client.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, List.of(client));
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));

        // then an exception is thrown when we call the method
        assertThrows(IllegalArgumentException.class, () -> {
            // when setting the client as skipped
            routeService.setClientSkipped(2, route.getId(), salesman);
        });
    }
}