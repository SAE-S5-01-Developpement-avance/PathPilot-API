package fr.iut.pathpilotapi.itineraries.routes;

import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
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
        route1.setSalesman_home(new GeoJsonPoint(0.0, 0.0));
        route1.setSalesman_id(1);
        route1.setExpected_clients(new ArrayList<>());
        route1.setSalesman_current_position(new GeoJsonPoint(0.0, 0.0));
        route1.setVisited_clients(new ArrayList<>());
        route1.setStartDate(new Date());

        // Test if a route is equals to herself but with basic data
        assertEquals(route1, route1);

        Route route2 = new Route();

        // Test if a route isn't equals to an empty route
        assertNotEquals(route1, route2);

        route2.setId(route1.getId());
        route2.setSalesman_home(route1.getSalesman_home());
        route2.setSalesman_id(route1.getSalesman_id());
        route2.setExpected_clients(route1.getExpected_clients());
        route2.setSalesman_current_position(route1.getSalesman_current_position());
        route2.setVisited_clients(route1.getVisited_clients());
        route2.setStartDate(route1.getStartDate());

        // Test if two route with the same data are equal but isn't the same instance
        assertEquals(route1, route2);
        assertNotSame(route1, route2);

        route1.getExpected_clients().add(new ClientDTO());
        route1.getExpected_clients().add(new ClientDTO());
        route2.getExpected_clients().add(new ClientDTO());
        route2.getExpected_clients().add(new ClientDTO());

        assertEquals(route1, route2);
        assertNotSame(route1, route2);
    }
}