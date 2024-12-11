/*
 * PositionDTO.java                                 11 Dec 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Class representing a position with coordinates
 */
@Getter
@Setter
@RequiredArgsConstructor
public class PositionDTO {

    @NotNull
    @Schema(description = "latitude value of the position")
    private double latitude;

    @NotNull
    @Schema(description = "position value of the position")
    private double longitude;
}
