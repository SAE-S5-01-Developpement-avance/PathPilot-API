package fr.iut.pathpilotapi.algorithme;

import java.util.List;

/**
 * Interface to represent an algorithm to find the best path to optimize the itinerary.
 */
public interface Algorithme {

    /**
     * Set the matrix of distances between the clients and the salesman.
     *
     * @param distances square matrix with the distances between the clients and the salesman
     */
    public void setMatrixLocationsRequest(List<List<Double>> distances);

    /**
     * Compute the best path to optimize the itinerary.
     */
    public void computeBestPath();

    /**
     * Get the best path to optimize the itinerary.
     *
     * @return the best path
     */
    public List<Integer> getBestPath();

    /**
     * @return the distance of the best path
     */
    public Double getDistanceBestPath();
}
