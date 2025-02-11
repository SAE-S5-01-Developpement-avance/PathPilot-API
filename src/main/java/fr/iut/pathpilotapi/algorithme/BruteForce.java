/*
 * BruteForce.java                                  11 févr. 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.algorithme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        List<Integer> remainingClients = IntStream.range(1, distances.size()).boxed().toList();
        findBestPathForItinerary(Collections.emptyList(), remainingClients, 0);
    }

    @Override
    public List<Integer> getBestPath() {
        return bestPath;
    }

    /*
     * First step before to launch the algorithm to find the best path for an itinerary.
     *
     * @param clientsDistances square matrix with the distances between the clients and the salesman
     * @return the best path to optimize the itinerary.
     */

    /**
     * Recursive function to find the best path and her distance.
     *
     * @param currentClientsVisited the clients already visited during on one path
     * @param remainingClients      the clients which we have to visit
     * @param currentDistance       the distances already did on one path
     * @return the distance of the best path.
     */
    private double findBestPathForItinerary(
            List<Integer> currentClientsVisited,
            List<Integer> remainingClients,
            double currentDistance
    ) {
        if (remainingClients.isEmpty()) {
            currentDistance += getDistance(currentClientsVisited.getLast(), 0);
            if (currentDistance < bestDistance) {
                bestDistance = currentDistance;
                bestPath = new ArrayList<>(currentClientsVisited);
            }
            return bestDistance;
        }

        for (int i = 0; i < remainingClients.size(); i++) {
            int clientIndex = remainingClients.get(i);
            List<Integer> newPath = new ArrayList<>(currentClientsVisited);
            newPath.add(clientIndex);
            List<Integer> newRemaining = new ArrayList<>(remainingClients);
            newRemaining.remove(i);
            double newDistance = currentDistance;

            if (!currentClientsVisited.isEmpty()) {
                // We had visit clients so we take the last visited and the current to take the distance.
                newDistance += getDistance(currentClientsVisited.getLast(), clientIndex);
            } else {
                // No clientIndex already visited, so we take the first line dedicated to the salesman.
                newDistance += getDistance(0, clientIndex);
            }
            bestDistance = findBestPathForItinerary(newPath, newRemaining, newDistance);
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
}
