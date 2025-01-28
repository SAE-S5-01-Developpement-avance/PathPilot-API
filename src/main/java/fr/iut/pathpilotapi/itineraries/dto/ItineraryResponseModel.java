/*
 * ItineraryResponseModel.java                                  06 dec. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.itineraries.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

import static fr.iut.pathpilotapi.Constants.MAX_CLIENTS;

/**
 * Class representing an itinerary
 * <h3>Mandatory fields</h3>
 * <ul>
 *     <li>Salesman ID</li>
 *     <li>Home position of the salesman</li>
 *     <li>Clients schedule</li>
 * </ul>
 */
@Getter
@Setter
@Schema(description = "Itinerary entity representing a itinerary to follow for a salesman")
public class ItineraryResponseModel extends RepresentationModel<ItineraryResponseModel> {

    @Schema(description = "Unique identifier of the route", example = "1")
    private String id;

    @NotNull
    @Schema(description = "Id of the salesman who owns the route")
    private Integer salesman_id;

    @NotNull
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    @Schema(description = "Home position of the salesman", example = "{type: 'Point', coordinates: [48.8566, 2.3522]}")
    private GeoJsonPoint salesman_home;

    @NotEmpty
    @NotNull
    @Size(max = MAX_CLIENTS)
    @Schema(description = "List of the clients to visit in the route")
    private List<@NotNull ClientDTO> clients_schedule;
}
