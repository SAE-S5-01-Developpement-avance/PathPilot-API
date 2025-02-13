/*
 * BruteForce.java                                  11 févr. 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.algorithme;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * BruteForce is an algorithm to find the best path to optimize the itinerary.
 * <p>
 * It's a naive algorithm that will try every possible path to find the best one.
 */
public class BruteForce implements Algorithme {

    public static final int SALESMAN_INDEX = 0;
    private List<List<Double>> distances;
    private List<Integer> bestPath;
    private double bestDistance;

    public BruteForce() {
    }

    /**
     * Set the matrix of distances between the clients and the salesman.
     *
     * @param distances square matrix with the distances between the clients and the salesman
     */
    @Override
    public void setMatrixLocationsRequest(List<List<Double>> distances) {
        this.distances = distances;
    }

    @Override
    public void computeBestPath() {
        bestPath = new ArrayList<>();
        bestDistance = Double.MAX_VALUE;
        // List of every number between 1 and the number of clients (distances.size() - 1)
        List<Integer> remainingClientsIndex = IntStream.range(1, distances.size()).boxed().toList();
        findBestPathForItinerary(remainingClientsIndex);
    }

    @Override
    public List<Integer> getBestPath() {
        return bestPath;
    }

    @Override
    public Double getDistanceBestPath() {
        if (getBestPath().isEmpty()) {
            return 0.0;
        }
        return getCompleteDistance(bestPath);
    }

    /**
     * Recursive function to find the best path and his distance.
     *
     * @param remainingClientsIndex the clients which we have to visit
     */
    private void findBestPathForItinerary(List<Integer> remainingClientsIndex) {
        Set<List<Integer>> allPossiblePath = getCombinaisons(remainingClientsIndex);

        for (List<Integer> path : allPossiblePath) {
            Double pathDistance = getCompleteDistance(path);
            if (pathDistance < bestDistance) {
                bestDistance = pathDistance;
                bestPath = path;
            }
        }
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

    public Set<List<Integer>> getCombinaisons(List<Integer> list) {
        if (list.size() == 1) {
            return Set.of(list);
        }
        // Set to avoid duplicate combinaisons,
        // We set the size of the set to the factorial of the list size to avoid resizing the set.
        // The number of arrangements of a list of n elements is n!
        // (factorial of n)
        Set<List<Integer>> combinaisons = new HashSet<>(fact(list.size()));

        for (int j = SALESMAN_INDEX, listSize = list.size(); j < listSize; j++) {
            ArrayList<Integer> listWithoutElement = new ArrayList<>(list);
            listWithoutElement.remove(j);

            Set<List<Integer>> subCombinaisons = getCombinaisons(listWithoutElement);
            for (List<Integer> l : subCombinaisons) {
                ArrayList<Integer> newCombinaison = new ArrayList<>(l);
                newCombinaison.addFirst(list.get(j));
                combinaisons.add(newCombinaison);
            }
        }

        return combinaisons;
    }

    /**
     * Calculate the factorial of a number.
     * <p>
     * If the number is greater than 12, the result will be {@link Integer#MAX_VALUE}, to avoid overflow.
     *
     * @param n the number to calculate the factorial
     * @return the factorial of n or {@link Integer#MAX_VALUE} if n is greater than 12
     */
    private static int fact(int n) {
        if (n == SALESMAN_INDEX) {
            return 1;
        } else if (12 < n) {
            return Integer.MAX_VALUE;
        } else {
            return n * fact(n - 1);
        }
    }
}
