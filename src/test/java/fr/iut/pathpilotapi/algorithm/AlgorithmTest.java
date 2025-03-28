package fr.iut.pathpilotapi.algorithm;


import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Class to test the algorithms to find the best path to optimize the itinerary.
 */
public class AlgorithmTest {

    /**
     * Test the algorithm with a given matrix.
     *
     * @param testMatrix the matrix with the distances between the clients and the salesman and the expected best path
     * @param algoType   the type of algorithm used to find the best path
     * @see AlgorithmType
     */
    private void testAlgoWithGivenMatrice(TestMatrix testMatrix, AlgorithmType algoType) {
        Algorithm algorithm = algoType.newInstanceAlgorithm();

        algorithm.setMatrixLocationsRequest(testMatrix.distance);
        algorithm.computeBestPath();
        List<Integer> result = algorithm.getBestPath();

        assertEquals(testMatrix.expectedDistance, algorithm.getDistanceBestPath(), 0.01, String.format("Error with the algorithm %s. The distance is not the expected one.", algoType.getName()));
        assertEquals(testMatrix.expectedList, result, String.format("Error with the algorithm %s. The result is not the expected one.", algoType.getName()));
    }

    @Test
    void testFindBestPath2X2() {
        TestMatrix testMatrix = new TestMatrix(
                Arrays.asList(
                        Arrays.asList(0.0, 2.0), // The salesman
                        Arrays.asList(1.0, 0.0)  // A client
                ),
                List.of(1),
                3.0
        );
        testAlgoWithGivenMatrice(testMatrix, AlgorithmType.BRUTE_FORCE);
        testAlgoWithGivenMatrice(testMatrix, AlgorithmType.BRUTE_FORCE_THREAD);
        testAlgoWithGivenMatrice(testMatrix, AlgorithmType.BRANCH_AND_BOUND);
    }

    @Test
    void testFindBestPathMatrix3X3() {
        TestMatrix testMatrix = new TestMatrix(
                Arrays.asList(
                        Arrays.asList(0.0, 2.0, 1.0), // The salesman
                        Arrays.asList(1.0, 0.0, 1.0), // A client
                        Arrays.asList(1.0, 1.0, 0.0)  // A client
                ),
                Arrays.asList(2, 1),
                3.0
        );
        testAlgoWithGivenMatrice(testMatrix, AlgorithmType.BRUTE_FORCE);
        testAlgoWithGivenMatrice(testMatrix, AlgorithmType.BRUTE_FORCE_THREAD);
        testAlgoWithGivenMatrice(testMatrix, AlgorithmType.BRANCH_AND_BOUND);
    }

    @Test
    void testFindBestPathMatrix5X5() {
        TestMatrix testMatrix = new TestMatrix(
                Arrays.asList(
                        Arrays.asList(0.0, 3.0, 2.0, 2.0, 2.0), // The salesman
                        Arrays.asList(1.0, 0.0, 2.0, 2.0, 2.0), // A client
                        Arrays.asList(3.0, 4.0, 0.0, 2.0, 2.0), // A client
                        Arrays.asList(2.0, 3.0, 4.0, 0.0, 2.0), // A client
                        Arrays.asList(1.0, 2.0, 3.0, 4.0, 0.0) // A client
                ),
                Arrays.asList(2, 3, 4, 1),
                9.0
        );
        testAlgoWithGivenMatrice(testMatrix, AlgorithmType.BRUTE_FORCE);
        testAlgoWithGivenMatrice(testMatrix, AlgorithmType.BRUTE_FORCE_THREAD);
        testAlgoWithGivenMatrice(testMatrix, AlgorithmType.BRANCH_AND_BOUND);
    }

    @Test
    void testFindBestPathMatrix9X9() {
        List<List<Double>> distance = Arrays.asList(
                Arrays.asList(0.0, 3.0, 2.0, 2.0, 2.0, 2.0, 4.0, 2.0, 3.0), // The salesman
                Arrays.asList(1.0, 0.0, 2.0, 2.0, 2.0, 2.0, 4.0, 2.0, 3.0), // A client
                Arrays.asList(3.0, 4.0, 0.0, 2.0, 2.0, 2.0, 4.0, 2.0, 3.0), // A client
                Arrays.asList(2.0, 3.0, 4.0, 0.0, 2.0, 2.0, 4.0, 2.0, 4.0), // A client
                Arrays.asList(1.0, 2.0, 3.0, 4.0, 0.0, 2.0, 5.0, 2.0, 4.0), // A client
                Arrays.asList(2.0, 5.0, 3.0, 4.0, 3.0, 0.0, 2.0, 2.0, 4.0), // A client
                Arrays.asList(5.0, 2.0, 3.0, 4.0, 3.0, 2.0, 0.0, 2.0, 3.0), // A client
                Arrays.asList(3.0, 3.0, 3.0, 4.0, 3.0, 2.0, 2.0, 0.0, 3.0), // A client
                Arrays.asList(2.0, 7.0, 3.0, 2.0, 3.0, 2.0, 2.0, 2.0, 0.0)  // A client
        );
        Double expectedDistance = 18.0;

        // This matrix allows multiple solutions
        // So each algorithm selects a path differently,
        // But the distance should be the same
        testAlgoWithGivenMatrice(new TestMatrix(
                distance,
                Arrays.asList(2, 7, 5, 6, 8, 3, 4, 1),
                expectedDistance
        ), AlgorithmType.BRUTE_FORCE);
        testAlgoWithGivenMatrice(new TestMatrix(
                distance,
                Arrays.asList(2, 7, 5, 6, 8, 3, 4, 1),
                expectedDistance
        ), AlgorithmType.BRUTE_FORCE_THREAD);
        testAlgoWithGivenMatrice(new TestMatrix(
                distance,
                Arrays.asList(2, 3, 4, 5, 7, 8, 6, 1),
                expectedDistance
        ), AlgorithmType.BRANCH_AND_BOUND);

        TestMatrix testMatrix = new TestMatrix(
                Arrays.asList(
                        Arrays.asList(0.0, 5131.0, 53546.215, 654.165, 6584.2, 6516.5, 6513.12, 5465.1, 1254.12), // The salesman
                        Arrays.asList(6547.126, 0.0, 6548.66, 7598.65, 4789.23, 4987.54, 7984.56, 1654.145, 9874.127), // A client
                        Arrays.asList(6487.265, 4646.21, 0.0, 6481.56, 6548.78, 7665.15, 6541.12, 1256.2, 4568.15), // A client
                        Arrays.asList(6541.12, 1256.2, 4568.15, 0.0, 654.165, 6584.2, 6516.5, 4865.124, 5423.14), // A client
                        Arrays.asList(6548.66, 7598.65, 4789.23, 4987.54, 0.0, 6548.66, 7598.65, 4789.23, 4987.54), // A client
                        Arrays.asList(6481.56, 6548.78, 7665.15, 6541.12, 1256.2, 0.0, 6541.12, 1256.2, 4568.1), // A client
                        Arrays.asList(6541.12, 1256.2, 4568.41, 6541.12, 1256.2, 4568.1, 0.0, 6481.56, 6548.78), // A client
                        Arrays.asList(4789.23, 4987.54, 6481.56, 6548.78, 7665.15, 6541.12, 1256.2, 0.0, 4157.15), // A client
                        Arrays.asList(7598.65, 4789.23, 4987.54, 7984.56, 1654.145, 9874.127, 4789.23, 4987.54, 0.0)  // A client
                ),
                Arrays.asList(3, 4, 8, 2, 7, 6, 1, 5),
                26521.11
        );
        testAlgoWithGivenMatrice(testMatrix, AlgorithmType.BRUTE_FORCE);
        testAlgoWithGivenMatrice(testMatrix, AlgorithmType.BRUTE_FORCE_THREAD);
        testAlgoWithGivenMatrice(testMatrix, AlgorithmType.BRANCH_AND_BOUND);
    }

    /**
     * Record to represent a test matrix with the distances between the clients and the salesman and the expected best path.
     */
    private record TestMatrix(List<List<Double>> distance, List<Integer> expectedList, Double expectedDistance) {
    }
}
