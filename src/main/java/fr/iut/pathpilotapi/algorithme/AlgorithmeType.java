package fr.iut.pathpilotapi.algorithme;

import lombok.Getter;

/**
 * Enum to represent the different types of algorithms to find the best path to optimize the itinerary.
 */
public enum AlgorithmeType {
    BRUTE_FORCE(BruteForce.class),
    BRANCH_AND_BOUND(BranchAndBound.class);

    @Getter
    private final String name;
    private final Class<? extends Algorithme> algorithme;

    AlgorithmeType(Class<? extends Algorithme> algorithme) {
        this.name = algorithme.getSimpleName();
        this.algorithme = algorithme;
    }

    public Algorithme newInstanceAlgorithm() {
        try {
            return algorithme.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error while creating the instance of the algorithm", e);
        }
    }
}
