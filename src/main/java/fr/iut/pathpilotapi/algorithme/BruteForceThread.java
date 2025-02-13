/*
 * BruteForceThread.java                                  13 févr. 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.algorithme;

import java.util.List;

/**
 * @author François de Saint Palais
 */
public class BruteForceThread implements Algorithme{

    public static final int SALESMAN_INDEX = 0;
    private List<List<Double>> distances;
    private List<Integer> bestPath;
    private double bestDistance;


    @Override
    public void setMatrixLocationsRequest(List<List<Double>> distances) {
        this.distances = distances;
    }

    @Override
    public void computeBestPath() {
        //TODO implement
    }

    @Override
    public List<Integer> getBestPath() {
        return bestPath;
    }

    @Override
    public Double getDistanceBestPath() {
        return getCompleteDistance(bestPath);
    }

        /**
     * Get the distance of a path.
     * <p>
     *     The path is a list of clients' index.
     *     <br>
     *     The salesman shouldn't be in the path, but we add it to the beginning and the end of the path.
     * @param bestClientPath the path to calculate the distance
     * @return the distance of the path
     */
    private Double getCompleteDistance(List<Integer> bestClientPath) {
        Double distance = getDistance(SALESMAN_INDEX, bestClientPath.getFirst());
        distance += getDistance(bestClientPath);
        distance += getDistance(bestClientPath.getLast(), SALESMAN_INDEX);
        return distance;
    }

    /**
     * Get the distance of a path.
     * <p>
     *     The path is a list of clients' index.
     *     <br>
     *     The salesman shouldn't be in the path.
     * @param bestClientPath the path to calculate the distance
     * @return the distance of the path
     */
    private Double getDistance(List<Integer> bestClientPath) {
        Double distance = 0.0;
        for (int i = 1; i < bestClientPath.size() - 1; i++) {
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
    private Double getDistance(int from, int to) {
        return distances.get(from).get(to);
    }
}
