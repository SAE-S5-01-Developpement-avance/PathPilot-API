/*
 * Route.java                                  06 dec. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.routes.dto.RouteClient;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Class representing a route
 * <h3>Mandatory fields</h3>
 * <ul>
 *     <li>Salesman ID</li>
 *     <li>Home position of the salesman</li>
 *     <li>Start date</li>
 *     <li>clients</li>
 *     <li>Current position of the salesman</li>
 *     <li>Is the route finished</li>
 * </ul>
 */
@Getter
@Setter
@RequiredArgsConstructor
@Document(collection = "routes")
public class Route {

    @Id
    private String id;

    /**
     * The salesman ID (keep it in camelCase, else SpringFramework will not be able to map the field correctly)
     */
    private Integer salesmanId;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint salesman_home;

    private Date startDate;

    private LinkedList<RouteClient> clients;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint salesman_current_position;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(id, route.id) &&
                Objects.equals(salesmanId, route.salesmanId) &&
                Objects.equals(salesman_home, route.salesman_home) &&
                Objects.equals(startDate, route.startDate) &&
                Objects.equals(clients, route.clients) &&
                Objects.equals(salesman_current_position, route.salesman_current_position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, salesmanId, salesman_home, clients, startDate, salesman_current_position);
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", salesman=" + salesmanId +
                ", salesman_home=" + salesman_home +
                ", startDate=" + startDate +
                ", clients=" + clients +
                ", salesManCurrentPosition=" + salesman_current_position +
                '}';
    }
}
