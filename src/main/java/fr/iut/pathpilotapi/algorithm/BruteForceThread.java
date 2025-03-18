/*
 * BruteForceThread.java                                  08 févr. 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

/**
 * This class extends the BruteForce class to implement a multithreaded approach
 * for computing the best path using brute force.
 */
public class BruteForceThread extends BruteForce {

    public static final int NB_ELEMENT_IN_SUBLIST = 100;

    public static <T> List<List<T>> getSubLists(List<T> allElements, int nbElementInSublist) {
        List<List<T>> subLists = new ArrayList<>();
        int startIndex = 0;
        while (startIndex < allElements.size()) {
            int end = Math.min(startIndex + nbElementInSublist, allElements.size());
            subLists.add(allElements.subList(startIndex, end));
            startIndex += nbElementInSublist;
        }
        return subLists;
    }

    @Override
    public void computeBestPath() {
        // List of every number between 1 and the number of clients (distances.size() - 1)
        List<Integer> clientsIndex = IntStream.range(1, distances.size()).boxed().toList();

        List<PossiblePath> allCombinaisons = getCombinaison(clientsIndex);
        List<List<PossiblePath>> subLists = getSubLists(allCombinaisons, NB_ELEMENT_IN_SUBLIST);

        // Creation of the Threads pool
        try (ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            // Submission of tasks
            List<Future<PossiblePath>> futures = subLists.stream()
                    .map(subList -> executorService.submit(() -> findBestPathInSublist(subList)))
                    .toList();

            // Search for the best path among all the results
            PossiblePath bestPath = findBestAmongFutures(futures);

            // Update of the best path found
            this.bestPath = bestPath.path();
            this.bestDistance = bestPath.distance();

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during the parallel calculation of the paths", e);
        }
    }

    /**
     * Find the best path in a sublist.
     *
     * @param subList the sublist to search in
     * @return the best path in the sublist
     */
    private PossiblePath findBestPathInSublist(List<PossiblePath> subList) {
        return subList.stream()
                .map(path -> new PossiblePath(path.path(), getCompleteDistance(path)))
                .min(Comparator.comparingDouble(PossiblePath::distance))
                .orElseThrow(() -> new IllegalStateException("Aucun chemin trouvé dans la sous-liste"));
    }

    /**
     * Find the best path among all the futures.
     *
     * @param futures the futures to search in
     * @return the best path among all the futures
     * @throws InterruptedException if the thread is interrupted
     * @throws ExecutionException   if an error occurs during the execution of the task
     */
    private PossiblePath findBestAmongFutures(List<Future<PossiblePath>> futures)
            throws InterruptedException, ExecutionException {
        PossiblePath bestPath = null;
        double bestDistance = Double.POSITIVE_INFINITY;

        for (Future<PossiblePath> future : futures) {
            // Wait for the result of the task
            PossiblePath path = future.get();

            // Update of the best path found
            if (path.distance() < bestDistance) {
                bestPath = path;
                bestDistance = path.distance();
            }
        }

        if (bestPath == null) {
            throw new IllegalStateException("No valid path was found");
        }

        return bestPath;
    }

    private List<PossiblePath> getCombinaison(List<Integer> list) {
        return super.getCombinaisons(list).stream()
                .map(PossiblePath::new)
                .toList();
    }


    public Double getCompleteDistance(PossiblePath bestClientPath) {
        return super.getCompleteDistance(bestClientPath.path());
    }

    public Double getDistance(PossiblePath bestClientPath) {
        return super.getDistance(bestClientPath.path());
    }

    record PossiblePath(List<Integer> path, Double distance) {
        public PossiblePath(List<Integer> path) {
            this(path, Double.NaN);
        }
    }
}
