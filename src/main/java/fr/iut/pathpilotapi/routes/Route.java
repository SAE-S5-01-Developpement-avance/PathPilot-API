package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.routes.dto.RouteClient;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
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

    /**
     * Salesman home position
     */
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint salesman_home;

    /**
     * Route start date
     */
    private Date startDate;

    /**
     * List of the clients to visit
     */
    private LinkedList<RouteClient> clients;

    /**
     * Salesman positions as a LineString
     */
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonLineString salesman_positions;

    /**
     * Route state
     */
    private RouteState state;

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
                Objects.equals(salesmanPositions, route.salesmanPositions);
                Objects.equals(state, route.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, salesmanId, salesman_home, clients, startDate, salesmanPosition, state);
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