package fr.iut.pathpilotapi.itineraries.routes;

import fr.iut.pathpilotapi.client.Client;
import fr.iut.pathpilotapi.client.repository.ClientRepository;
import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
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

    private static final Logger log = LoggerFactory.getLogger(RouteServiceIntegrationTest.class);

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

    @BeforeEach
    public void setUpSalesman() {
        salesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(salesman);
        clients = new ArrayList<>(clientRepository.saveAll(IntegrationTestUtils.createClients(salesman)));
    }

    @Test
    public void testAddRoute() {
        Route route = IntegrationTestUtils.createRoute(salesman, clients.stream().map(ClientDTO::new).toList());
        Route routeCreated = routeService.createRoute(route.getId(), salesman);

        assertEquals(route.getSalesman_id(), routeCreated.getSalesman_id());
        assertEquals(route.getSalesman_home(), routeCreated.getSalesman_home());
        assertEquals(route.getExpected_clients(), routeCreated.getExpected_clients());
        assertEquals(route, routeRepository.findById(routeCreated.getId()).orElseThrow());
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
    public void testAddRouteWithInvalidClients() {
        Salesman anotherSalesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(anotherSalesman);
        Client clientWhoBelongToAnotherSalesman = clientRepository.save(IntegrationTestUtils.createClient(anotherSalesman));

        Route route = IntegrationTestUtils.createRoute(salesman, List.of(new ClientDTO(clientWhoBelongToAnotherSalesman)));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            routeService.createRoute(route.getId(), salesman);
        });

        assertEquals(String.format(RouteService.ROUTE_NOT_BELONGS_TO_SALESMAN, clientWhoBelongToAnotherSalesman.getId()), exception.getMessage());
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
}