/*
 * BruteForceThread.java                                  13 févr. 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package fr.iut.pathpilotapi.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author François de Saint Palais
 */
public class BruteForceThread extends BruteForce {

    public static final int NB_ELEMENT_IN_SUBLIST = 100;

    @Override
    public void computeBestPath() {
        // List of every number between 1 and the number of clients (distances.size() - 1)
        List<Integer> clientsIndex = IntStream.range(1, distances.size()).boxed().toList();

        List<PossiblePath> allCombinaisons = getCombinaison(clientsIndex);
        int subListCount = 1 + allCombinaisons.size() / NB_ELEMENT_IN_SUBLIST;
        List<List<PossiblePath>> subList = new ArrayList<>(subListCount);

        for (int i = 0; i < subListCount; i++) {
            int end = Math.min(i + NB_ELEMENT_IN_SUBLIST, allCombinaisons.size());
            subList.add(allCombinaisons.subList(i, end));
        }

        // TODO lancer un Thread pour chaque subList et récupérer le résultat
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

    record PossiblePath(List<Integer> path) {
    }
}
