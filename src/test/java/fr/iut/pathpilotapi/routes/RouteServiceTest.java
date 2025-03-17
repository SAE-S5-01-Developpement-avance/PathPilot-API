/*
 * RouteServiceTest.java                                 28 Jan 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.GeoCord;
import fr.iut.pathpilotapi.clients.ClientCategory;
import fr.iut.pathpilotapi.clients.MongoClient;
import fr.iut.pathpilotapi.clients.repository.MongoClientRepository;
import fr.iut.pathpilotapi.exceptions.ObjectNotFoundException;
import fr.iut.pathpilotapi.itineraries.Itinerary;
import fr.iut.pathpilotapi.itineraries.ItineraryService;
import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.routes.dto.ClientState;
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
import org.springframework.data.geo.Distance;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private MongoClientRepository mongoClientRepository;

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

    @Test
    void testUpdateSalesmanPosition() {
        // Given a salesman, a route, and a new position
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        GeoCord newPosition = new GeoCord(44.0, 2.0);
        Route route = IntegrationTestUtils.createRoute(salesman, List.of());
        route.setId("routeId");
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));
        when(routeRepository.save(any(Route.class))).thenReturn(route);
        when(mongoClientRepository.findByLocationNear(any(GeoJsonPoint.class), any(Distance.class))).thenReturn(List.of());

        // When updating the salesman's position
        routeService.updateSalesmanPosition(route.getId(), salesman, newPosition);

        // Then the route should be updated with the new position
        assertEquals(2, route.getSalesmanPositions().getCoordinates().size());
        assertEquals(new GeoJsonPoint(2.0, 44.0), route.getSalesmanPositions().getCoordinates().get(1));
        verify(routeRepository, times(1)).save(route);
    }

    @Test
    void testUpdateSalesmanPositionRouteNotFound() {
        // Given a salesman and a route ID that does not exist
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        GeoCord newPosition = new GeoCord(44.0, 2.0);
        String routeId = "invalidRouteId";
        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        // When updating the salesman's position
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            routeService.updateSalesmanPosition(routeId, salesman, newPosition);
        });

        // Then an exception should be thrown with the message "Route not found with ID: invalidRouteId"
        assertEquals("Route not found with ID: " + routeId, exception.getMessage());
        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void testFindNearbyClients() {
        // Given a salesman, a route, a point, and a distance
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, Collections.emptyList());
        route.setId("routeId");
        GeoJsonPoint point = new GeoJsonPoint(2.0, 44.0);
        double distanceInKm = 1.0;
        MongoClient client = new MongoClient();
        client.setCategory(new ClientCategory("PROSPECT"));
        client.setId(1);
        client.setLocation(point);
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));
        when(mongoClientRepository.findByLocationNear(point, new Distance(distanceInKm))).thenReturn(List.of(client));

        // When finding nearby clients
        Page<MongoClient> nearbyClients = routeService.findNearbyClients(route.getId(), salesman, point, List.of(), new Distance(distanceInKm));

        // Then the result should contain the expected clients
        assertNotNull(nearbyClients);
        assertEquals(1, nearbyClients.getTotalElements());
        assertEquals(client, nearbyClients.getContent().get(0));
    }

    @Test
    void testFindNearbyClientsNoResults() {
        // Given a salesman, a route, a point, and a distance
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, Collections.emptyList());
        route.setId("routeId");
        GeoJsonPoint point = new GeoJsonPoint(2.0, 44.0);
        double distanceInKm = 1.0;
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));
        when(mongoClientRepository.findByLocationNear(point, new Distance(distanceInKm))).thenReturn(Collections.emptyList());

        // When finding nearby clients
        Page<MongoClient> nearbyClients = routeService.findNearbyClients(route.getId(), salesman, point, List.of(), new Distance(distanceInKm));

        // Then the result should be an empty list
        assertNotNull(nearbyClients);
        assertTrue(nearbyClients.isEmpty());
    }

    @Test
    void findNearbyClientsWhileAvoidingSome() {
        // Given a salesman, a route, a point, a distance, and a list of clients to avoid
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, Collections.emptyList());
        route.setId("routeId");
        GeoJsonPoint point = new GeoJsonPoint(2.0, 44.0);
        double distanceInKm = 1.0;
        MongoClient client = new MongoClient();
        client.setCategory(new ClientCategory("PROSPECT"));
        client.setId(1);
        client.setLocation(point);
        MongoClient clientToAvoid = new MongoClient();
        clientToAvoid.setCategory(new ClientCategory("PROSPECT"));
        clientToAvoid.setId(2);
        clientToAvoid.setLocation(new GeoJsonPoint(2.1, 44.1));
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));
        when(mongoClientRepository.findByLocationNear(point, new Distance(distanceInKm))).thenReturn(List.of(client));

        // When finding nearby clients
        Page<MongoClient> nearbyClients = routeService.findNearbyClients(route.getId(), salesman, point, List.of(clientToAvoid), new Distance(distanceInKm));

        // Then the result should contain the expected clients
        assertNotNull(nearbyClients);
        assertEquals(1, nearbyClients.getTotalElements());
        assertEquals(client, nearbyClients.getContent().get(0));
    }

    @Test
    void testStartRoute() {
        // Given a salesman, a route, and a RouteStartRequestModel
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, Collections.emptyList());
        route.setId("routeId");
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));

        GeoCord currentPosition = new GeoCord(45.0, 44.0);

        // When starting the route
        routeService.startRoute(route.getId(), currentPosition, salesman);

        // Then the route state should be IN_PROGRESS and the start date should be set
        assertEquals(RouteState.IN_PROGRESS, route.getState());
        assertNotNull(route.getStartDate());
        assertEquals(currentPosition.latitude(), route.getSalesmanPositions().getCoordinates().getLast().getY());
        assertEquals(currentPosition.longitude(), route.getSalesmanPositions().getCoordinates().getLast().getX());
        verify(routeRepository, times(1)).save(route);
    }

    @Test
    void testStartRouteButRouteNotFound() {
        // Given a salesman and a RouteStartRequestModel with a non-existing route ID
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        String routeId = "nonExistingRouteId";
        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        GeoCord currentPosition = new GeoCord(45.0, 44.0);

        // When starting the route
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            routeService.startRoute(routeId, currentPosition, salesman);
        });

        // Then an exception should be thrown with the message "Route not found with ID: nonExistingRouteId"
        assertEquals("Route not found with ID: " + routeId, exception.getMessage());
    }

    @Test
    void testStopRoute() {
        // Given a salesman and a route
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, Collections.emptyList());
        route.setId("routeId");
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));

        // When stopping the route
        routeService.stopRoute(route.getId(), salesman);

        // Then the route state should be STOPPED
        assertEquals(RouteState.STOPPED, route.getState());
        verify(routeRepository, times(1)).save(route);
    }

    @Test
    void testStopRouteButRouteNotFound() {
        // Given a salesman and a non-existing route ID
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        String routeId = "nonExistingRouteId";
        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        // When stopping the route
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            routeService.stopRoute(routeId, salesman);
        });

        // Then an exception should be thrown with the message "Route not found with ID: nonExistingRouteId"
        assertEquals("Route not found with ID: " + routeId, exception.getMessage());
    }

    @Test
    void testStopRouteButRouteDoesNotBelongToSalesman() {
        // Given two salesmen and a route that belongs to the first salesman
        Salesman salesman1 = IntegrationTestUtils.createSalesman();
        salesman1.setId(1);
        Salesman salesman2 = IntegrationTestUtils.createSalesman();
        salesman2.setId(2);
        Route route = IntegrationTestUtils.createRoute(salesman1, Collections.emptyList());
        route.setId("routeId");
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));

        // When stopping the route
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            routeService.stopRoute(route.getId(), salesman2);
        });

        // Then an exception should be thrown with the message "Route with ID: routeId does not belong to the connected salesman."
        assertEquals(String.format(RouteService.ROUTE_NOT_BELONGS_TO_SALESMAN, route.getId()), exception.getMessage());
    }

    @Test
    void testPauseRoute() {
        // Given a salesman and a route
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, Collections.emptyList());
        route.setId("routeId");
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));

        // When pausing the route
        routeService.pauseRoute(route.getId(), salesman);

        // Then the route state should be PAUSED
        assertEquals(RouteState.PAUSED, route.getState());
        verify(routeRepository, times(1)).save(route);
    }

    @Test
    void testPauseRouteButRouteNotFound() {
        // Given a salesman and a non-existing route ID
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        String routeId = "nonExistingRouteId";
        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        // When pausing the route
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            routeService.pauseRoute(routeId, salesman);
        });

        // Then an exception should be thrown with the message "Route not found with ID: nonExistingRouteId"
        assertEquals("Route not found with ID: " + routeId, exception.getMessage());
    }

    @Test
    void testPauseRouteButRouteDoesNotBelongToSalesman() {
        // Given two salesmen and a route that belongs to the first salesman
        Salesman salesman1 = IntegrationTestUtils.createSalesman();
        salesman1.setId(1);
        Salesman salesman2 = IntegrationTestUtils.createSalesman();
        salesman2.setId(2);
        Route route = IntegrationTestUtils.createRoute(salesman1, Collections.emptyList());
        route.setId("routeId");
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));

        // When pausing the route
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            routeService.pauseRoute(route.getId(), salesman2);
        });

        // Then an exception should be thrown with the message "Route with ID: routeId does not belong to the connected salesman."
        assertEquals(String.format(RouteService.ROUTE_NOT_BELONGS_TO_SALESMAN, route.getId()), exception.getMessage());
    }

    @Test
    void testResumeRoute() {
        // Given a salesman and a route
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, Collections.emptyList());
        route.setId("routeId");
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));

        GeoCord currentPosition = new GeoCord(45.0, 44.0);

        // When pausing the route
        routeService.resumeRoute(route.getId(), currentPosition, salesman);

        // Then the route state should be IN_PROGRESS
        assertEquals(RouteState.IN_PROGRESS, route.getState());
        verify(routeRepository, times(1)).save(route);
    }

    @Test
    void testResumeRouteButRouteNotFound() {
        // Given a salesman and a non-existing route ID
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesman.setId(1);
        String routeId = "nonExistingRouteId";
        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        GeoCord currentPosition = new GeoCord(45.0, 44.0);

        // When pausing the route
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            routeService.resumeRoute(routeId, currentPosition, salesman);
        });

        // Then an exception should be thrown with the message "Route not found with ID: nonExistingRouteId"
        assertEquals("Route not found with ID: " + routeId, exception.getMessage());
    }

    @Test
    void testResumeRouteButRouteDoesNotBelongToSalesman() {
        // Given two salesmen and a route that belongs to the first salesman
        Salesman salesman1 = IntegrationTestUtils.createSalesman();
        salesman1.setId(1);
        Salesman salesman2 = IntegrationTestUtils.createSalesman();
        salesman2.setId(2);
        Route route = IntegrationTestUtils.createRoute(salesman1, Collections.emptyList());
        route.setId("routeId");
        when(routeRepository.findById(route.getId())).thenReturn(Optional.of(route));

        GeoCord currentPosition = new GeoCord(45.0, 44.0);

        // When pausing the route
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            routeService.resumeRoute(route.getId(), currentPosition, salesman2);
        });

        // Then an exception should be thrown with the message "Route with ID: routeId does not belong to the connected salesman."
        assertEquals(String.format(RouteService.ROUTE_NOT_BELONGS_TO_SALESMAN, route.getId()), exception.getMessage());
    }
}