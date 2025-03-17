package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.GeoCord;
import fr.iut.pathpilotapi.clients.Client;
import fr.iut.pathpilotapi.clients.ClientCategory;
import fr.iut.pathpilotapi.clients.MongoClient;
import fr.iut.pathpilotapi.clients.repository.ClientRepository;
import fr.iut.pathpilotapi.clients.repository.MongoClientRepository;
import fr.iut.pathpilotapi.exceptions.ObjectNotFoundException;
import fr.iut.pathpilotapi.itineraries.Itinerary;
import fr.iut.pathpilotapi.itineraries.ItineraryRepository;
import fr.iut.pathpilotapi.itineraries.ItineraryService;
import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.routes.dto.ClientState;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class RouteServiceIntegrationTest {

    @Autowired
    private SalesmanRepository salesmanRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RouteService routeService;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private MongoClientRepository mongoClientRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Salesman salesman;
    private ArrayList<Client> clients;
    @Autowired
    private ItineraryRepository itineraryRepository;
    @Autowired
    private ItineraryService itineraryService;

    @BeforeEach
    public void setUpSalesman() {
        salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        clients = new ArrayList<>(clientRepository.saveAll(IntegrationTestUtils.createClients(salesman)));
    }

    @Test
    public void testAddRoute() {
        // Given a correct itinerary and a salesman
        Itinerary itinerary = IntegrationTestUtils.createItinerary(salesman, clients.stream().map(ClientDTO::new).toList());
        itineraryRepository.save(itinerary);

        // When a route is created
        Route routeCreated = routeService.createRoute(itinerary.getId(), salesman);

        // Then the route is created and saved in the database
        assertNotNull(routeCreated);
        assertEquals(itinerary.getSalesman_home(), routeCreated.getSalesman_home());
        routeCreated.getClients().forEach(routeClient ->
                assertTrue(itinerary.getClients_schedule().contains(routeClient.getClient()))
        );
        assertEquals(itinerary, itineraryService.findByIdAndConnectedSalesman(itinerary.getId(), salesman));
    }

    @Test
    public void testGetRoute() {
        Route route = IntegrationTestUtils.createRoute(salesman, clients.stream().map(ClientDTO::new).toList());
        routeRepository.save(route);

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Route> routeRetrieved = routeService.getAllRoutesFromSalesman(salesman, pageRequest).getContent();

        assertTrue(routeRetrieved.stream().anyMatch(r -> r.getId().equals(route.getId())));
        assertEquals(1, routeRetrieved.size());
        assertEquals(route, routeRetrieved.get(0));
    }

    @Test
    public void testAddRouteWithInvalidItinerary() {
        Salesman anotherSalesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(anotherSalesman);
        Client clientWhoBelongToAnotherSalesman = clientRepository.save(IntegrationTestUtils.createClient(anotherSalesman));

        Itinerary itinerary = IntegrationTestUtils.createItinerary(anotherSalesman, List.of(new ClientDTO(clientWhoBelongToAnotherSalesman)));
        itineraryRepository.save(itinerary);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            routeService.createRoute(itinerary.getId(), salesman);
        });

        assertEquals(String.format(ItineraryService.ITINERARY_WITH_ID_NOT_BELONGS_TO_SALESMAN, itinerary.getId()), exception.getMessage());
    }

    @Test
    public void testGetRouteById() {
        Route route = IntegrationTestUtils.createRoute(salesman, clients.stream().map(ClientDTO::new).toList());
        routeRepository.save(route);

        Route routeRetrieved = routeService.findByIdAndConnectedSalesman(route.getId(), salesman);

        assertEquals(route, routeRetrieved);
    }

    @Test
    public void testGetAllRoutesFromSalesman() {
        Salesman anotherSalesman = salesmanRepository.save(IntegrationTestUtils.createSalesman());
        Route route1 = IntegrationTestUtils.createRoute(salesman, clients.stream().map(ClientDTO::new).toList());
        Route route2 = IntegrationTestUtils.createRoute(salesman, clients.stream().map(ClientDTO::new).toList());
        Route route3 = IntegrationTestUtils.createRoute(anotherSalesman, clients.stream().map(ClientDTO::new).toList());
        routeRepository.saveAll(List.of(route1, route2, route3));

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Route> routesRetrieved = routeService.getAllRoutesFromSalesman(salesman, pageRequest).getContent();

        assertTrue(routesRetrieved.contains(route1));
        assertTrue(routesRetrieved.contains(route2));
        assertEquals(2, routesRetrieved.size());
    }

    @Test
    public void testDeleteRoute() {
        Route route = IntegrationTestUtils.createRoute(salesman, clients.stream().map(ClientDTO::new).toList());
        routeRepository.save(route);

        routeService.deleteByIdAndConnectedSalesman(route.getId(), salesman);

        assertFalse(routeRepository.findById(route.getId()).isPresent());
    }

    @Test
    public void testDeleteNonExistingRoute() {
        Route route = IntegrationTestUtils.createRoute(salesman, clients.stream().map(ClientDTO::new).toList());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            routeService.deleteByIdAndConnectedSalesman(route.getId(), salesman);
        });

        assertEquals("Route not found with ID: " + route.getId(), exception.getMessage());
    }

    @Test
    public void testSetClientVisited() {
        // given a route, a client, and a state
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        ClientDTO client = new ClientDTO();
        client.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, List.of(client));
        routeRepository.save(route);

        // when setting the client as visited
        routeService.setClientVisited(client.getId(), route.getId(), salesman);

        // then the client state should be VISITED
        Route updatedRoute = routeService.findByIdAndConnectedSalesman(route.getId(), salesman);
        assertEquals(ClientState.VISITED, updatedRoute.getClients().getFirst().getState());
    }

    @Test
    public void testSetClientSkipped() {
        // given a route, a client, and a state
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        ClientDTO client = new ClientDTO();
        client.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, List.of(client));
        routeRepository.save(route);

        // when setting the client as skipped
        routeService.setClientSkipped(client.getId(), route.getId(), salesman);

        // then the client state should be SKIPPED
        Route updatedRoute = routeService.findByIdAndConnectedSalesman(route.getId(), salesman);
        assertEquals(ClientState.SKIPPED, updatedRoute.getClients().getFirst().getState());
    }

    @Test
    public void testSetClientVisitedWithInvalidClient() {
        // given a route and a salesman
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        ClientDTO client = new ClientDTO();
        client.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, List.of(client));
        routeRepository.save(route);

        // when setting a non-existing client as visited
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            routeService.setClientVisited(999, route.getId(), salesman);
        });

        // then an exception should be thrown
        assertEquals("Client with ID: 999 is not in the route with ID: " + route.getId(), exception.getMessage());
    }

    @Test
    public void testSetClientSkippedWithInvalidClient() {
        // given a route and a salesman
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        ClientDTO client = new ClientDTO();
        client.setId(1);
        Route route = IntegrationTestUtils.createRoute(salesman, List.of(client));
        routeRepository.save(route);

        // when setting a non-existing client as skipped
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            routeService.setClientSkipped(999, route.getId(), salesman);
        });

        // then an exception should be thrown
        assertEquals("Client with ID: 999 is not in the route with ID: " + route.getId(), exception.getMessage());
    }

    @Test
    public void testSetClientVisitedWithInvalidRoute() {
        // given a salesman and a client
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        ClientDTO client = new ClientDTO();
        client.setId(1);

        // when setting the client as visited in a non-existing route
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            routeService.setClientVisited(client.getId(), "invalidRouteId", salesman);
        });

        // then an exception should be thrown
        assertEquals("Route not found with ID: invalidRouteId", exception.getMessage());
    }

    @Test
    public void testSetClientSkippedWithInvalidRoute() {
        // given a salesman and a client
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        ClientDTO client = new ClientDTO();
        client.setId(1);

        // when setting the client as skipped in a non-existing route
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            routeService.setClientSkipped(client.getId(), "invalidRouteId", salesman);
        });

        // then an exception should be thrown
        assertEquals("Route not found with ID: invalidRouteId", exception.getMessage());
    }

    @Test
    public void testStartRoute() {
        // given a route
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        Route route = IntegrationTestUtils.createRoute(salesman, clients.stream().map(ClientDTO::new).toList());
        route  = routeRepository.save(route);

        GeoCord geoCord = new GeoCord(48.8566, 2.3522);

        // when starting the route
        routeService.startRoute(route.getId(), geoCord, salesman);

        // then the route state should be IN_PROGRESS
        Route updatedRoute = routeService.findByIdAndConnectedSalesman(route.getId(), salesman);

        assertEquals(RouteState.IN_PROGRESS, updatedRoute.getState());
    }

    @Test
    public void testStartRouteWithInvalidRoute() {
        // given a salesman
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);

        String invalidRouteId = "invalidRouteId";
        GeoCord geoCord = new GeoCord(48.8566, 2.3522);

        // when starting a non-existing route
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            routeService.startRoute(invalidRouteId, geoCord, salesman);
        });

        // then an exception should be thrown
        assertEquals("Route not found with ID: invalidRouteId", exception.getMessage());
    }

    @Test
    public void testStopRoute() {
        // Given a route
        Route route = IntegrationTestUtils.createRoute(salesman, clients.stream().map(ClientDTO::new).toList());
        routeRepository.save(route);

        // When stopping the route
        routeService.stopRoute(route.getId(), salesman);

        // Then the route state should be STOPPED
        Route updatedRoute = routeService.findByIdAndConnectedSalesman(route.getId(), salesman);
        assertEquals(RouteState.STOPPED, updatedRoute.getState());
    }

    @Test
    public void testStopRouteWithInvalidRoute() {
        // When stopping a non-existing route
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            routeService.stopRoute("invalidRouteId", salesman);
        });

        // Then an exception should be thrown
        assertEquals("Route not found with ID: invalidRouteId", exception.getMessage());
    }

    @Test
    public void testStopRouteButRouteDoesNotBelongToSalesman() {
        // Given two salesmen and a route that belongs to the first salesman
        Salesman anotherSalesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(anotherSalesman);
        Route route = IntegrationTestUtils.createRoute(anotherSalesman, clients.stream().map(ClientDTO::new).toList());
        routeRepository.save(route);

        // When stopping the route
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            routeService.stopRoute(route.getId(), salesman);
        });

        // Then an exception should be thrown
        assertEquals(String.format(RouteService.ROUTE_NOT_BELONGS_TO_SALESMAN, route.getId()), exception.getMessage());
    }

    @Test
    public void testPauseRouteButRouteDoesNotBelongToSalesman() {
        // Given two salesmen and a route that belongs to the first salesman
        Salesman anotherSalesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(anotherSalesman);
        Route route = IntegrationTestUtils.createRoute(anotherSalesman, clients.stream().map(ClientDTO::new).toList());
        routeRepository.save(route);

        // When pausing the route
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            routeService.pauseRoute(route.getId(), salesman);
        });

        // Then an exception should be thrown
        assertEquals(String.format(RouteService.ROUTE_NOT_BELONGS_TO_SALESMAN, route.getId()), exception.getMessage());
    }

    @Test
    public void testResumeRoute() {
        // given a route
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        Route route = IntegrationTestUtils.createRoute(salesman, clients.stream().map(ClientDTO::new).toList());
        route  = routeRepository.save(route);

        GeoCord geoCord = new GeoCord(48.8566, 2.3522);

        // when resuming the route
        routeService.resumeRoute(route.getId(), geoCord, salesman);

        // then the route state should be IN_PROGRESS
        Route updatedRoute = routeService.findByIdAndConnectedSalesman(route.getId(), salesman);

        assertEquals(RouteState.IN_PROGRESS, updatedRoute.getState());
    }

    @Test
    public void testResumeRouteWithInvalidRoute() {
        // given a salesman
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);

        String invalidRouteId = "invalidRouteId";
        GeoCord geoCord = new GeoCord(48.8566, 2.3522);

        // when resuming a non-existing route
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            routeService.resumeRoute(invalidRouteId, geoCord, salesman);
        });

        // then an exception should be thrown
        assertEquals("Route not found with ID: invalidRouteId", exception.getMessage());
    }

    @Test
    void testUpdateSalesmanPosition() {
        // Given a salesman, a route, and a new position
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        GeoCord newPosition = new GeoCord(44.0, 2.0);
        Route route = IntegrationTestUtils.createRoute(salesman, List.of());
        routeRepository.save(route);

        // When updating the salesman's position
        routeService.updateSalesmanPosition(route.getId(), salesman, newPosition);

        // Then the route should be updated with the new position and nearby clients should be returned
        Route updatedRoute = routeRepository.findById(route.getId()).orElseThrow();
        assertEquals(2, updatedRoute.getSalesmanPositions().getCoordinates().size());
        assertEquals(new GeoJsonPoint(2.0, 44.0), updatedRoute.getSalesmanPositions().getCoordinates().get(1));
    }

    @Test
    void testUpdateSalesmanPositionRouteNotFound() {
        // Given a salesman and a route ID that does not exist
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        GeoCord newPosition = new GeoCord(2.0, 44.0);
        String routeId = "invalidRouteId";

        // When updating the salesman's position
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            routeService.updateSalesmanPosition(routeId, salesman, newPosition);
        });

        // Then an exception should be thrown with the message "Route not found with ID: invalidRouteId"
        assertEquals("Route not found with ID: " + routeId, exception.getMessage());
    }
    @Test
    void testFindNearbyClients() {
        // Given a route, a salesman, a point, and a distance
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        Route route = IntegrationTestUtils.createRoute(salesman, clients.stream().map(ClientDTO::new).toList());
        routeRepository.save(route);
        GeoJsonPoint point = new GeoJsonPoint(2.0, 44.0);
        GeoJsonPoint nearbyPoint = new GeoJsonPoint(2.01, 44.01);
        double distanceInKm = 1.0;
        MongoClient client = new MongoClient();
        client.setId(1);
        client.setCategory(new ClientCategory("PROSPECT"));
        client.setLocation(nearbyPoint);
        mongoTemplate.save(client);

        // When finding nearby clients
        Page<MongoClient> nearbyClients = routeService.findNearbyClients(route.getId(), salesman, point, List.of(), new Distance(distanceInKm));

        // Then the result should contain the expected clients
        assertNotNull(nearbyClients);
        assertEquals(1, nearbyClients.getTotalElements());
        assertEquals(client, nearbyClients.getContent().get(0));
    }

    @Test
    void testFindNearbyClientsWithMultipleClients() {
        // Given a route, a salesman, a point, and a distance
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        Route route = IntegrationTestUtils.createRoute(salesman, clients.stream().map(ClientDTO::new).toList());
        routeRepository.save(route);
        GeoJsonPoint point = new GeoJsonPoint(2.0, 44.0);
        double distanceInKm = 1.0;
        MongoClient client1 = new MongoClient();
        client1.setId(1);
        client1.setCategory(new ClientCategory("PROSPECT"));
        client1.setLocation(new GeoJsonPoint(2.01, 44.01));
        MongoClient client2 = new MongoClient();
        client2.setId(2);
        client2.setCategory(new ClientCategory("PROSPECT"));
        client2.setLocation(new GeoJsonPoint(3, 44.0));
        mongoTemplate.save(client1);
        mongoTemplate.save(client2);

        // When finding nearby clients
        Page<MongoClient> nearbyClients = routeService.findNearbyClients(route.getId(), salesman, point, List.of(client2), new Distance(distanceInKm));

        // Then the result should contain the expected clients
        assertNotNull(nearbyClients);
        assertEquals(1, nearbyClients.getTotalElements());
        assertEquals(client1, nearbyClients.getContent().get(0));
    }

    @Test
    void testFindNearbyClientsNoResults() {
        // Given a route, a salesman, a point, and a distance
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        Route route = IntegrationTestUtils.createRoute(salesman, clients.stream().map(ClientDTO::new).toList());
        routeRepository.save(route);
        GeoJsonPoint point = new GeoJsonPoint(2.0, 44.0);
        double distanceInKm = 1.0;

        // When finding nearby clients
        Page<MongoClient> nearbyClients = routeService.findNearbyClients(route.getId(), salesman, point, List.of(), new Distance(distanceInKm));

        // Then the result should be an empty list
        assertNotNull(nearbyClients);
        assertTrue(nearbyClients.isEmpty());
    }

    @Test
    void testFindNearbyClientWithNoClientCloseEnough() {
        // Given a route, a salesman, a point, and a distance
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        Route route = IntegrationTestUtils.createRoute(salesman, clients.stream().map(ClientDTO::new).toList());
        routeRepository.save(route);
        GeoJsonPoint point = new GeoJsonPoint(2.0, 44.0);
        double distanceInKm = 1.0;
        MongoClient client = new MongoClient();
        client.setCategory(new ClientCategory("PROSPECT"));
        client.setId(1);
        client.setLocation(new GeoJsonPoint(3.0, 45.0));
        mongoTemplate.save(client);

        // When finding nearby clients
        Page<MongoClient> nearbyClients = routeService.findNearbyClients(route.getId(), salesman, point, List.of(client), new Distance(distanceInKm));

        // Then the result should be an empty list
        assertNotNull(nearbyClients);
        assertTrue(nearbyClients.isEmpty());
    }

    @Test
    void testFindNearbyClientWithWrongCategory() {
        // Given a route, a salesman, a point, and a distance
        Salesman salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        Route route = IntegrationTestUtils.createRoute(salesman, clients.stream().map(ClientDTO::new).toList());
        routeRepository.save(route);
        GeoJsonPoint point = new GeoJsonPoint(2.0, 44.0);
        double distanceInKm = 1.0;
        MongoClient client = new MongoClient();
        client.setCategory(new ClientCategory("WRONG"));
        client.setId(1);
        client.setLocation(new GeoJsonPoint(2.01, 44.01));
        mongoTemplate.save(client);

        // When finding nearby clients
        Page<MongoClient> nearbyClients = routeService.findNearbyClients(route.getId(), salesman, point, List.of(), new Distance(distanceInKm));

        // Then the result should be an empty list
        assertNotNull(nearbyClients);
        assertTrue(nearbyClients.isEmpty());
    }



    @AfterEach
    void tearDown() {
        itineraryRepository.deleteAll();
        clientRepository.deleteAll();
        salesmanRepository.deleteAll();
        routeRepository.deleteAll();
        mongoClientRepository.deleteAll();
    }
}