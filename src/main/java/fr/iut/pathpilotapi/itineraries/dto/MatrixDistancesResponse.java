package fr.iut.pathpilotapi.itineraries.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MatrixDistancesResponse {

    private List<List<Double>> distances;

}