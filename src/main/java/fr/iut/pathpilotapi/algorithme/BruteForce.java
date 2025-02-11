/*
 * BruteForce.java                                  11 févr. 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.algorithme;

import java.util.*;
import java.util.stream.IntStream;

/**
 * BruteForce is an algorithm to find the best path to optimize the itinerary.
 * <p>
 * It's a naive algorithm that will try every possible path to find the best one.
 */
public class BruteForce implements Algorithme {

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
        findBestPathForItinerary(Collections.emptyList(), remainingClientsIndex, 0);
    }

    @Override
    public List<Integer> getBestPath() {
        return bestPath;
    }

    /**
     * Recursive function to find the best path and her distance.
     *
     * @param currentClientsVisited the clients already visited during on one path
     * @param remainingClientsIndex the clients which we have to visit
     * @param currentDistance       the distances already did on one path
     * @return the distance of the best path.
     */
    private double findBestPathForItinerary(
            List<Integer> currentClientsVisited,
            List<Integer> remainingClientsIndex,
            double currentDistance
    ) {
        if (remainingClientsIndex.isEmpty()) {
            currentDistance += getDistance(currentClientsVisited.getLast(), 0);
            if (currentDistance < bestDistance) {
                bestDistance = currentDistance;
                bestPath = new ArrayList<>(currentClientsVisited);
            }
            return bestDistance;
        }

        for (int i = 0; i < remainingClientsIndex.size(); i++) {
            int clientIndex = remainingClientsIndex.get(i);
            // Add the clientIndex to the path list
            List<Integer> newPath = new ArrayList<>(currentClientsVisited);
            newPath.add(clientIndex);
            // Remove the clientIndex from the remainingClientsIndex list.
            List<Integer> newRemainingClientsIndex = remainingClientsIndex.stream().filter(index -> index != clientIndex).toList();
            double newDistance = currentDistance;

            if (!currentClientsVisited.isEmpty()) {
                // We had visit clients so we take the last visited and the current to take the distance.
                newDistance += getDistance(currentClientsVisited.getLast(), clientIndex);
            } else {
                // No clientIndex already visited, so we take the first line dedicated to the salesman.
                newDistance += getDistance(0, clientIndex);
            }
            bestDistance = findBestPathForItinerary(newPath, newRemainingClientsIndex, newDistance);
        }
        return bestDistance;
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

        for (int j = 0, listSize = list.size(); j < listSize; j++) {
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
     *     If the number is greater than 12, the result will be {@link Integer#MAX_VALUE}, to avoid overflow.
     * @param n the number to calculate the factorial
     * @return the factorial of n or {@link Integer#MAX_VALUE} if n is greater than 12
     */
    private static int fact(int n) {
        if (n == 0) {
            return 1;
        } else if (12 < n) {
            return Integer.MAX_VALUE;
        } else {
            return n * fact(n - 1);
        }
    }
}
