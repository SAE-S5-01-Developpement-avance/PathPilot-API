package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.clients.Client;
import fr.iut.pathpilotapi.clients.repository.ClientRepository;
import fr.iut.pathpilotapi.itineraries.Itinerary;
import fr.iut.pathpilotapi.itineraries.ItineraryRepository;
import fr.iut.pathpilotapi.itineraries.ItineraryService;
import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
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

    @AfterEach
    void tearDown() {
        itineraryRepository.deleteAll();
        clientRepository.deleteAll();
        salesmanRepository.deleteAll();
    }
}