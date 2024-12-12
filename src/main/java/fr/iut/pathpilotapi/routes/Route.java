/*
 * Route.java                                  06 dec. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.routes.dto.ClientDTO;
import fr.iut.pathpilotapi.routes.dto.PositionDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

/**
 * Class representing a route
 * <h3>Mandatory fields</h3>
 * <ul>
 *     <li>Salesman ID</li>
 *     <li>Home position of the salesman</li>
 *     <li>Clients schedule</li>
 * </ul>
 * <h3>Optional fields</h3>
 * <ul>
 *     <li>Start date</li>
 *     <li>Clients visited</li>
 *     <li>Current position of the salesman</li>
 * </ul>
 */
@Getter
@Setter
@RequiredArgsConstructor
@Document(collection = "routes")
@Schema(description = "Route entity representing a route to follow for a salesman")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the route", example = "1")
    private int _id;

    @NotNull
    @Schema(description = "Id of the salesman who owns the route")
    private int salesman;

    @NotNull
    @Schema(description = "Home position of the salesman", example = "{type: 'Point', coordinates: [48.8566, 2.3522]}")
    private PositionDTO salesmanHome;

    @NotEmpty
    @NotNull
    @Size(max = 8)
    @Schema(description = "List of the clients to visit in the route")
    private List<@NotNull ClientDTO> clients_schedule;

    @Schema(description = "Start date of the route", example = "2024-12-06T00:00:00.000Z")
    private Date startDate;

    @Size(max = 8)
    @Schema(description = "List of the clients already visited")
    private List<@NotNull ClientDTO> clients_visited;

    @Schema(description = "Curent position of the salesman", example = "{type: 'Point', coordinates: [48.8566, 2.3522]}")
    private PositionDTO salesManCurrentPosition;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;

        return     _id == route._id
                && salesman == route.salesman
                && Objects.equals(salesmanHome, route.salesmanHome)
                && ( (getClients_schedule() == null && getClients_schedule() == route.getClients_schedule()) || getClients_schedule().containsAll(route.clients_schedule))
                && Objects.equals(startDate, route.startDate)
                && Objects.equals(clients_visited, route.clients_visited)
                && Objects.equals(salesManCurrentPosition, route.salesManCurrentPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                _id,
                salesman,
                salesmanHome,
                clients_schedule,
                startDate,
                clients_visited,
                salesManCurrentPosition
        );
    }

    @Override
    public String toString() {
        return "Route{" +
                "_id=" + _id +
                ", salesman=" + salesman +
                ", salesman_home=" + salesmanHome +
                ", clients_schedule=" + clients_schedule +
                ", startDate=" + startDate +
                ", clients_visited=" + clients_visited +
                ", salesManCurrentPosition=" + salesManCurrentPosition +
                '}';
    }
}
