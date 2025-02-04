package fr.iut.pathpilotapi.itineraries.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Schema(description = "All information based on the client and salesman locations")
public class MatrixDistancesResponse {
    @Schema(description = "The matrix of distances enter all the clients and the salesman")
    private List<List<Double>> distances;
}