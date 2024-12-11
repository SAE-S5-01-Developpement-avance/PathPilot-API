/*
 * ClientDTO.java                                 09 Dec 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes.dto;

import com.mongodb.client.model.geojson.Position;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ClientDTO {

    @NotNull
    @Schema(description = "client's Id")
    private Integer client;

    @NotNull
    @Schema(description = "position of the client's company")
    private PositionDTO company_location;

    @NotNull
    @NotEmpty
    @Schema(description = "name of the client's company")
    private String company_name;
}
