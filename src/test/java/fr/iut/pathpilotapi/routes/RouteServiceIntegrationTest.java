package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.GeoCord;
import fr.iut.pathpilotapi.clients.Client;
import fr.iut.pathpilotapi.clients.repository.ClientRepository;
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
import org.springframework.data.domain.PageRequest;
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

    @AfterEach
    void tearDown() {
        itineraryRepository.deleteAll();
        clientRepository.deleteAll();
        salesmanRepository.deleteAll();
    }
}