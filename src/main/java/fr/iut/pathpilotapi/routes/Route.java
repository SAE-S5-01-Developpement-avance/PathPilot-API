/*
 * Route.java                                  06 dec. 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes;

import fr.iut.pathpilotapi.client.Client;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.geo.GeoJson;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

import static fr.iut.pathpilotapi.Constants.MAX_LENGTH;

@Getter
@Setter
@RequiredArgsConstructor
@Document(collection = "routes")
@Schema(description = "Route entity representing a route to follow for a salesman")
public class Route {

    @NotEmpty
    @Size(max = 8)
    @Schema(description = "List og the clients to visit in the route", example = "[CLIENT_ID1, CLIENT_ID2, CLIENT_ID3]")
    private List<String> idClientsToVisit;

    @Schema(description = "Start date of the route", example = "2024-12-06T00:00:00.000Z")
    private Date startDate;

    @Schema(description = "Curent position of the salesman", example = "{type: 'Point', coordinates: [48.8566, 2.3522]}")
    private GeoJson salesManCurrentPosition;

    @Size(max = 8)
    @Schema(description = "List of the clients already visited", example = "[CLIENT_ID1, CLIENT_ID2, CLIENT_ID3]")
    private List<String> idClientsVisited;
}
