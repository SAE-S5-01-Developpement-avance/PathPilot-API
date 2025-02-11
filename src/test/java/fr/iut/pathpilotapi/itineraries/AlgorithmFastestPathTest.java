package fr.iut.pathpilotapi.itineraries;


import fr.iut.pathpilotapi.algorithme.Algorithme;
import fr.iut.pathpilotapi.algorithme.BruteForce;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlgorithmFastestPathTest {

    private final Algorithme algorithm = new BruteForce();

    @Test
    void testFindBestPath2X2() {
        List<List<Double>> distance = Arrays.asList(
                Arrays.asList(0.0, 2.0), // The salesman
                Arrays.asList(1.0, 0.0)  // A client
        );
        // The optimized itinerary, they are ids ordered
        List<Integer> expectedList = List.of(1);
        algorithm.setMatrixLocationsRequest(distance);
        algorithm.computeBestPath();
        List<Integer> result = algorithm.getBestPath();

        assertEquals(result, expectedList);
    }

    @Test
    void testFindBestPathMatrix3X3() {
        List<List<Double>> distance = Arrays.asList(
                Arrays.asList(0.0, 2.0, 1.0), // The salesman
                Arrays.asList(1.0, 0.0, 1.0), // A client
                Arrays.asList(1.0, 1.0, 0.0)  // A client
        );
        // The optimized itinerary, they are ids ordered
        List<Integer> expectedList = Arrays.asList(2, 1);

        algorithm.setMatrixLocationsRequest(distance);
        algorithm.computeBestPath();
        List<Integer> result = algorithm.getBestPath();

        assertEquals(result, expectedList);
    }

    @Test
    void testFindBestPathMatrix5X5() {
        List<List<Double>> distance = Arrays.asList(
                Arrays.asList(0.0, 3.0, 2.0, 2.0, 2.0), // The salesman
                Arrays.asList(1.0, 0.0, 2.0, 2.0, 2.0), // A client
                Arrays.asList(3.0, 4.0, 0.0, 2.0, 2.0), // A client
                Arrays.asList(2.0, 3.0, 4.0, 0.0, 2.0), // A client
                Arrays.asList(1.0, 2.0, 3.0, 4.0, 0.0) // A client
        );
        // The optimized itinerary, they are ids ordered
        List<Integer> expectedList = Arrays.asList(2, 3, 4, 1);
        algorithm.setMatrixLocationsRequest(distance);
        algorithm.computeBestPath();
        List<Integer> result = algorithm.getBestPath();

        assertEquals(result, expectedList);
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
        // The optimized itinerary, they are ids ordered
        List<Integer> expectedList = Arrays.asList(2, 3, 4, 5, 7, 8, 6, 1);

        algorithm.setMatrixLocationsRequest(distance);
        algorithm.computeBestPath();
        List<Integer> result = algorithm.getBestPath();

        assertEquals(result, expectedList);


        distance = Arrays.asList(
                Arrays.asList(0.0, 5131.0, 53546.215, 654.165, 6584.2, 6516.5, 6513.12, 5465.1, 1254.12), // The salesman
                Arrays.asList(6547.126, 0.0, 6548.66, 7598.65, 4789.23, 4987.54, 7984.56, 1654.145, 9874.127), // A client
                Arrays.asList(6487.265, 4646.21, 0.0, 6481.56, 6548.78, 7665.15, 6541.12, 1256.2, 4568.15), // A client
                Arrays.asList(6541.12, 1256.2, 4568.15, 0.0, 654.165, 6584.2, 6516.5, 4865.124, 5423.14), // A client
                Arrays.asList(6548.66, 7598.65, 4789.23, 4987.54, 0.0, 6548.66, 7598.65, 4789.23, 4987.54), // A client
                Arrays.asList(6481.56, 6548.78, 7665.15, 6541.12, 1256.2, 0.0, 6541.12, 1256.2, 4568.1), // A client
                Arrays.asList(6541.12, 1256.2, 4568.41, 6541.12, 1256.2, 4568.1, 0.0, 6481.56, 6548.78), // A client
                Arrays.asList(4789.23, 4987.54, 6481.56, 6548.78, 7665.15, 6541.12, 1256.2, 0.0, 4157.15), // A client
                Arrays.asList(7598.65, 4789.23, 4987.54, 7984.56, 1654.145, 9874.127, 4789.23, 4987.54, 0.0)  // A client
        );
        // The optimized itinerary, they are ids ordered
        expectedList = Arrays.asList(3, 4, 8, 2, 7, 6, 1, 5);

        algorithm.setMatrixLocationsRequest(distance);
        algorithm.computeBestPath();
        result = algorithm.getBestPath();

        assertEquals(result, expectedList);
    }
}
