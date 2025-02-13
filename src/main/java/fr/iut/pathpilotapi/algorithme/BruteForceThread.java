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
        return bestDistance;
    }
}
