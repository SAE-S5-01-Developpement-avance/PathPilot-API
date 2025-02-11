package fr.iut.pathpilotapi.algorithme;

import java.util.List;

public interface Algorithme {

    public void setMatrixLocationsRequest(List<List<Double>> distances);

    public void computeBestPath();

    public List<Integer> getBestPath();
}
