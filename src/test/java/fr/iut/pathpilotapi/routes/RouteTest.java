package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import fr.iut.pathpilotapi.routes.dto.ClientState;
import fr.iut.pathpilotapi.routes.dto.RouteClient;
import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

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
        route1.setSalesmanId(1);
        route1.setClients(new LinkedList<>());
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0));
        route1.setSalesman_positions(new GeoJsonLineString(points));
        route1.setStartDate(new Date());

        // Test if a route is equals to herself but with basic data
        assertEquals(route1, route1);

        Route route2 = new Route();

        // Test if a route isn't equals to an empty route
        assertNotEquals(route1, route2);

        route2.setId(route1.getId());
        route2.setSalesman_home(route1.getSalesman_home());
        route2.setSalesmanId(route1.getSalesmanId());
        route2.setClients(route1.getClients());
        route2.setSalesman_positions(route1.getSalesman_positions());
        route2.setStartDate(route1.getStartDate());

        // Test if two route with the same data are equal but isn't the same instance
        assertEquals(route1, route2);
        assertNotSame(route1, route2);

        route1.getClients().add(new RouteClient(new ClientDTO(), ClientState.EXPECTED));
        route1.getClients().add(new RouteClient(new ClientDTO(), ClientState.EXPECTED));
        route2.getClients().add(new RouteClient(new ClientDTO(), ClientState.EXPECTED));
        route2.getClients().add(new RouteClient(new ClientDTO(), ClientState.EXPECTED));

        assertEquals(route1, route2);
        assertNotSame(route1, route2);
    }
}