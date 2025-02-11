/*
 * BruteForce.java                                  11 févr. 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.algorithme;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author François de Saint Palais
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
        findBestPathForItinerary(distances, new ArrayList<>(), remainingClients, 0);
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
     * @param clientsDistances      square matrix with the distances between the clients and the salesman
     * @param currentClientsVisited the clients already visited during on one path
     * @param remainingClients      the clients which we have to visit
     * @param currentDistance       the distances already did on one path
     * @return the distance of the best path.
     */
    private double findBestPathForItinerary(
            List<List<Double>> clientsDistances,
            List<Integer> currentClientsVisited,
            List<Integer> remainingClients,
            double currentDistance
    ) {
        if (remainingClients.isEmpty()) {
            currentDistance += clientsDistances.get(currentClientsVisited.getLast()).getFirst();
            if (currentDistance < bestDistance) {
                bestDistance = currentDistance;
                bestPath.clear();
                bestPath.addAll(currentClientsVisited);
            }
            return bestDistance;
        }

        for (int i = 0; i < remainingClients.size(); i++) {
            int client = remainingClients.get(i);
            List<Integer> newPath = new ArrayList<>(currentClientsVisited);
            newPath.add(client);
            List<Integer> newRemaining = new ArrayList<>(remainingClients);
            newRemaining.remove(i);
            double newDistance = currentDistance;

            if (!currentClientsVisited.isEmpty()) {
                // We had visit clients so we take the last visited and the current to take the distance.
                newDistance += clientsDistances.get(currentClientsVisited.getLast()).get(client);
            } else {
                // No client already visited, so we take the first line dedicated to the salesman.
                newDistance += clientsDistances.getFirst().get(client);
            }
            bestDistance = findBestPathForItinerary(clientsDistances, newPath, newRemaining, newDistance);
        }
        return bestDistance;
    }
}
