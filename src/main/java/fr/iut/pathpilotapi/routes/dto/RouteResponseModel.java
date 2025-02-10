/*
 * RouteResponseModel.java                                 27 janv. 2025
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.LinkedList;

import static fr.iut.pathpilotapi.Constants.MAX_CLIENTS;

@Getter
@Setter
@Schema(description = "Route entity representing a route to follow for a salesman")
public class RouteResponseModel extends RepresentationModel<RouteResponseModel> {

    @Schema(description = "Unique identifier of the route", example = "1")
    private String id;

    @NotNull
    @Schema(description = "Home position of the salesman", example = "{type: 'Point', coordinates: [48.8566, 2.3522]}")
    private GeoJsonPoint salesman_home;

    @NotEmpty
    @NotNull
    @Size(max = MAX_CLIENTS)
    @Schema(description = "List of the clients in the route")
    private LinkedList<@NotNull RouteClient> clients;

    @Schema(description = "Start date of the route", example = "2024-12-06T00:00:00.000Z")
    private Date startDate;

    @Schema(description = "Current position of the salesman", example = "{type: 'Point', coordinates: [48.8566, 2.3522]}")
    private GeoJsonPoint salesman_current_position;
}
