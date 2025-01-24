package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.routes.dto.ClientDTO;
import fr.iut.pathpilotapi.routes.dto.PositionDTO;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the RouteService
 */
class RouteTest {

    @Test
    void testEquals() {
        Route route1 = new Route();

        // Test if a route is equals to herself
        assertEquals(route1, route1);

        route1.setId("1");
        route1.setSalesmanHome(new GeoJsonPoint(0.0, 0.0));
        route1.setSalesman(1);
        route1.setClients_schedule(new ArrayList<>());
        route1.setSalesManCurrentPosition(new GeoJsonPoint(0.0, 0.0));
        route1.setClients_visited(new ArrayList<>());
        route1.setStartDate(new Date());

        // Test if a route is equals to herself but with basic data
        assertEquals(route1, route1);

        Route route2 = new Route();

        // Test if a route isn't equals to an empty route
        assertNotEquals(route1, route2);

        route2.setId(route1.getId());
        route2.setSalesmanHome(route1.getSalesmanHome());
        route2.setSalesman(route1.getSalesman());
        route2.setClients_schedule(route1.getClients_schedule());
        route2.setSalesManCurrentPosition(route1.getSalesManCurrentPosition());
        route2.setClients_visited(route1.getClients_visited());
        route2.setStartDate(route1.getStartDate());

        // Test if two route with the same data are equal but isn't the same instance
        assertEquals(route1, route2);
        assertNotSame(route1, route2);

        route1.getClients_schedule().add(new ClientDTO());
        route1.getClients_schedule().add(new ClientDTO());
        route2.getClients_schedule().add(new ClientDTO());
        route2.getClients_schedule().add(new ClientDTO());

        assertEquals(route1, route2);
        assertNotSame(route1, route2);


    }
}