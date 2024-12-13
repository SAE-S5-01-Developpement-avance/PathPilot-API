/*
 * PositionDTO.java                                 11 Dec 2024
 * IUT de Rodez, no author rights
 */

package fr.iut.pathpilotapi.routes.dto;

import fr.iut.pathpilotapi.salesman.Salesman;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

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

    /**
     * constructor
     * needed for integration tests
     * @param longitude
     * @param latitude
     */
    public PositionDTO(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public static PositionDTO createFromSalesman(Salesman salesman) {
        return new PositionDTO(salesman.getLongHomeAddress(), salesman.getLatHomeAddress());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PositionDTO that = (PositionDTO) o;

        return Double.compare(latitude, that.latitude) == 0
                && Double.compare(longitude, that.longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
