/*
 * Route.java                                  06 dec. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.itineraries.routes;

import fr.iut.pathpilotapi.itineraries.dto.ClientDTO;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Class representing a route
 * <h3>Mandatory fields</h3>
 * <ul>
 *     <li>Salesman ID</li>
 *     <li>Home position of the salesman</li>
 *     <li>Clients schedule</li>
 *     <li>Start date</li>
 *     <li>Visited clients</li>
 *     <li>Expected clients</li>
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

    private Integer salesman_id;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint salesman_home;

    private List<@NotNull ClientDTO> expected_clients;

    private Date startDate;

    private List<@NotNull ClientDTO> visited_clients;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint salesman_current_position;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(id, route.id) &&
                Objects.equals(salesman_id, route.salesman_id) &&
                Objects.equals(salesman_home, route.salesman_home) &&
                Objects.equals(expected_clients, route.expected_clients) &&
                Objects.equals(startDate, route.startDate) &&
                Objects.equals(visited_clients, route.visited_clients) &&
                Objects.equals(salesman_current_position, route.salesman_current_position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, salesman_id, salesman_home, expected_clients, startDate, visited_clients, salesman_current_position);
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", salesman=" + salesman_id +
                ", salesman_home=" + salesman_home +
                ", clients_schedule=" + expected_clients +
                ", startDate=" + startDate +
                ", clients_visited=" + visited_clients +
                ", salesManCurrentPosition=" + salesman_current_position +
                '}';
    }
}
