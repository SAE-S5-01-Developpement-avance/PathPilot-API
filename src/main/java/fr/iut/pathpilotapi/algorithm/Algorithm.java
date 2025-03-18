/*
 * Algorithm.java                                  18 janv. 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.algorithm;

import lombok.Getter;

import java.util.List;

/**
 * Abstract Class to represent an algorithm to find the best path to optimize the itinerary.
 */
public abstract class Algorithm {

    protected static final int SALESMAN_INDEX = 0;

    protected List<List<Double>> distances;
    /**
     * -- GETTER --
     * Get the best path to optimize the itinerary.
     */
    @Getter
    protected List<Integer> bestPath;
    protected double bestDistance;

    /**
     * Compute the best path to optimize the itinerary.
     */
    public abstract void computeBestPath();

    /**
     * Set the matrix of distances between the clients and the salesman.
     *
     * @param distances square matrix with the distances between the clients and the salesman
     */
    public void setMatrixLocationsRequest(List<List<Double>> distances) {
        this.distances = distances;
    }

    /**
     * @return the distance of the best path
     */
    public Double getDistanceBestPath() {
        if (getBestPath().isEmpty()) {
            return 0.0;
        }
        return getCompleteDistance(getBestPath());
    }


    /**
     * Get the distance of a path.
     * <p>
     * The path is a list of clients' index.
     * <br>
     * The salesman shouldn't be in the path, but we add it to the beginning and the end of the path.
     *
     * @param bestClientPath the path to calculate the distance
     * @return the distance of the path
     */
    public Double getCompleteDistance(List<Integer> bestClientPath) {
        Double distance = getDistance(SALESMAN_INDEX, bestClientPath.getFirst());
        distance += getDistance(bestClientPath);
        distance += getDistance(bestClientPath.getLast(), SALESMAN_INDEX);
        return distance;
    }

    /**
     * Get the distance of a path.
     * <p>
     * The path is a list of clients' index.
     * <br>
     * The salesman shouldn't be in the path.
     *
     * @param bestClientPath the path to calculate the distance
     * @return the distance of the path
     */
    public Double getDistance(List<Integer> bestClientPath) {
        Double distance = 0.0;
        for (int i = 0; i < bestClientPath.size() - 1; i++) {
            distance += getDistance(bestClientPath.get(i), bestClientPath.get(i + 1));
        }
        return distance;
    }

    /**
     * Get the distance between two clients.
     *
     * @param from the index of the first client we want to start from
     * @param to   the index of the second client we want to go to
     * @return the distance between the two clients
     */
    public Double getDistance(int from, int to) {
        return distances.get(from).get(to);
    }
}
