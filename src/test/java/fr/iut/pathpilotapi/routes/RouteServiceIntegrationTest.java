package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.client.Client;
import fr.iut.pathpilotapi.client.repository.ClientRepository;
import fr.iut.pathpilotapi.routes.dto.ClientDTO;
import fr.iut.pathpilotapi.routes.dto.CreateRouteDTO;
import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        clients = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            clients.add(IntegrationTestUtils.createClient(salesman));
        }

        routeRepository.deleteAll();
        salesmanRepository.save(salesman);
        clientRepository.saveAll(clients);
    }

    @Test
    public void testCreateRoute() {
        // Given a valid route
        Route route = IntegrationTestUtils.createRoute(salesman, clients);

        // When we create the route
        Route routeCreated = routeService.addRoute(route, salesman);

        // Then the route is in the BD
        assertEquals(route, routeCreated);
        assertEquals(route, routeRepository.findById(route.get_id()).orElseThrow());
    }

    @Test
    public void testCreateRouteWithDTO() {
        Route route = IntegrationTestUtils.createRoute(salesman, clients);
        // Given a valid route
        CreateRouteDTO createRouteDTO = new CreateRouteDTO();
        createRouteDTO.setClients_schedule(route.getClients_schedule().stream().map(ClientDTO::getClient).toList());

        // When we create the route
        Route routeCreated = routeService.addRoute(createRouteDTO, salesman);

        // Then the route is in the BD
        assertEquals(route.getSalesman(), routeCreated.getSalesman());
        assertEquals(route.getSalesmanHome(), routeCreated.getSalesmanHome());
        assertEquals(route.getClients_schedule(), routeCreated.getClients_schedule());

        Route routeFromDB = routeRepository.findById(routeCreated.get_id()).orElseThrow();
        assertEquals(route.getSalesman(), routeFromDB.getSalesman());
        assertEquals(route.getSalesmanHome(), routeFromDB.getSalesmanHome());
        assertEquals(route.getClients_schedule(), routeFromDB.getClients_schedule());
    }

    @Test
    public void testGetRoute() {
        // Given a route in the db
        Route route = IntegrationTestUtils.createRoute(salesman, clients);
        routeRepository.save(route);

        // When we retrieve the route of a Salesman
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Route> routeRetrieved = routeService.getAllRoutesFromSalesman(pageRequest, salesman).getContent();

        // Then the route we created should be in the retrieved routes
        assertTrue(routeRetrieved.contains(route));
        assertEquals(1, routeRetrieved.size());
        assertEquals(route, routeRetrieved.getFirst());
    }

    @Test
    public void testCreateRouteWithInvalidClients() {
        // Given a route with clients that do not belong to the salesman
        Salesman anotherSalesman = IntegrationTestUtils.createSalesman();
        salesmanRepository.save(anotherSalesman);
        Client clientWhoBelongToAnotherSalesman = clientRepository.save(IntegrationTestUtils.createClient(anotherSalesman));

        Route route = IntegrationTestUtils.createRoute(salesman, List.of(clientWhoBelongToAnotherSalesman));

        // When we try to create the route
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            routeService.addRoute(route, salesman);
        });

        // Then an exception should be thrown
        assertEquals(RouteService.CLIENT_NOT_BELONGS_TO_SALESMAN, exception.getMessage());
    }

    @Test
    public void testGetRouteById() {
        // Given a route in the db
        Route route = IntegrationTestUtils.createRoute(salesman, clients);
        routeRepository.save(route);

        // When we retrieve the route by ID
        Route routeRetrieved = routeService.getRouteById(route.get_id());

        // Then the retrieved route should match the created route
        assertEquals(route, routeRetrieved);
    }

    @Test
    public void testGetAllRoutesFromSalesman() {
        Salesman anotherSalesman = salesmanRepository.save(IntegrationTestUtils.createSalesman());
        // Given multiple routes in the db
        Route route1 = IntegrationTestUtils.createRoute(salesman, clients);
        Route route2 = IntegrationTestUtils.createRoute(salesman, clients);
        Route route3 = IntegrationTestUtils.createRoute(anotherSalesman, clients);
        routeRepository.saveAll(List.of(route1, route2, route3));

        // When we retrieve all routes for the salesman
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Route> routesRetrieved = routeService.getAllRoutesFromSalesman(pageRequest, salesman).getContent();

        // Then the retrieved routes should match the created routes
        assertTrue(routesRetrieved.contains(route1));
        assertTrue(routesRetrieved.contains(route2));
        assertEquals(2, routesRetrieved.size());
    }

    @Test
    public void testDeleteRoute() {
        // Given a route in the db
        Route route = IntegrationTestUtils.createRoute(salesman, clients);
        routeRepository.save(route);

        // When we delete the route
        boolean isDeleted = routeService.delete(route, salesman);

        // Then the route should be removed from the db
        assertTrue(isDeleted);
        assertFalse(routeRepository.findById(route.get_id()).isPresent());
    }

    @Test
    public void testDeleteNonExistingRoute() {
        // Given a route that is not in the db
        Route route = IntegrationTestUtils.createRoute(salesman, clients);

        // When we try to delete the non-existing route
        boolean isDeleted = routeService.delete(route, salesman);

        // Then the deletion should return false
        assertTrue(isDeleted);
    }
}