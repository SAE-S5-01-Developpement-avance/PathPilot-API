/*
 * Route.java                                  06 dec. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import com.mongodb.client.model.geojson.Position;
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

import java.util.Date;
import java.util.List;

/**
 * Class representing a route
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
    @Schema(description = "Id of the the salesman who owns the route")
    private int salesman;

    @NotNull
    @Schema(description = "Home position of the salesman", example = "{type: 'Point', coordinates: [48.8566, 2.3522]}")
    private PositionDTO salesman_home;

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
}
