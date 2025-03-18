/*
 * BruteForce.java                                  18 janv. 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.algorithm;

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
public class BruteForce extends Algorithm {

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

    @Override
    public void computeBestPath() {
        bestPath = new ArrayList<>();
        bestDistance = Double.MAX_VALUE;
        // List of every number between 1 and the number of clients (distances.size() - 1)
        List<Integer> remainingClientsIndex = IntStream.range(1, distances.size()).boxed().toList();
        findBestPathForItinerary(remainingClientsIndex);
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
     * Generates all possible combinations of the given list of integers.
     *
     * @param list the list of integers to generate combinations from
     * @return a set of lists, where each list is a unique combination of the input list
     */
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
}
