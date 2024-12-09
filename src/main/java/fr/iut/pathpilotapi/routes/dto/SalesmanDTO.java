/*
 * SalesmanDTO.java                                 09 Dec 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes.dto;

import com.mongodb.client.model.geojson.Position;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Schema(description = "Class representing a salesman with its unique identifier and his home position")
public class SalesmanDTO{

    @NotEmpty
    @Schema(description = "Unique identifier of the route owner", example = "1")
    private int salesman_id;

    @NotEmpty
    @Schema(description = "Home position of the salesman")
    private Position salesman_home;

}
