package fr.iut.pathpilotapi.itineraries.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

import static fr.iut.pathpilotapi.Constants.MAX_CLIENTS;

@Setter
@Getter
@Schema(description = "The matrix of locations represent the clients and the salesman locations")
public class MatrixLocationsRequest {

    @NotNull
    @NotEmpty
    @Size(max = MAX_CLIENTS + 1) // There are the clients more the salesman
    private List<List<Double>> locations;

    @NotNull
    @NotEmpty
    @Schema(description = "Specifies the matrix type (e.g., distance or duration)")
    private List<String> metrics;

    public MatrixLocationsRequest(List<List<Double>> locations, List<String> metrics) {
        this.locations = locations;
        this.metrics = metrics;
    }
}
