/*
 * BranchAndBound.java                                  03 févr. 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This class implements the Branch and Bound algorithm for finding the best path.
 * It extends the Algorithm class.
 */
public class BranchAndBound extends Algorithm {

    @Override
    public void computeBestPath() {
        bestPath = new ArrayList<>();
        bestDistance = Double.MAX_VALUE;
        // List of every number between 1 and the number of clients (distances.size() - 1)
        List<Integer> remainingClientsIndex = IntStream.range(1, distances.size()).boxed().toList();
        findBestPathForItinerary(Collections.emptyList(), remainingClientsIndex, 0);
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
            if (newDistance < bestDistance) {
                bestDistance = findBestPathForItinerary(newPath, newRemainingClientsIndex, newDistance);
            }
        }
        return bestDistance;
    }
}
