package fr.iut.pathpilotapi.algorithm;

import lombok.Getter;

/**
 * Enum to represent the different types of algorithms to find the best path to optimize the itinerary.
 */
public enum AlgorithmType {
    BRUTE_FORCE(BruteForce.class),
    BRUTE_FORCE_THREAD(BruteForceThread.class),
    BRANCH_AND_BOUND(BranchAndBound.class);

    @Getter
    private final String name;
    private final Class<? extends Algorithm> algorithm;

    AlgorithmType(Class<? extends Algorithm> algorithm) {
        this.name = algorithm.getSimpleName();
        this.algorithm = algorithm;
    }

    public Algorithm newInstanceAlgorithm() {
        try {
            return algorithm.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error while creating the instance of the algorithm", e);
        }
    }
}
