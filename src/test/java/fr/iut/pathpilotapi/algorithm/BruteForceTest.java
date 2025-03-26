package fr.iut.pathpilotapi.algorithm;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the BruteForce class' methods.
 * <p>
 * The purpose of this class isn't to test the BruteForce algorithm itself, but to test the methods of the BruteForce class.
 * Go to {@link AlgorithmTest} to test the BruteForce algorithm.
 * </p>
 *
 * @see AlgorithmTest
 */
class BruteForceTest {

    /**
     * Get the combinaisons of a list.
     * <p>
     * To simplfy the syntaxe of the test, this method is used to get the combinaisons of a list.
     * <br>
     * So instead of calling
     * <pre>
     * {@code
     * new BruteForce().getCombinaisons(list);
     * }
     * </pre>
     * We can call
     * <pre>
     * {@code
     * getCombinaisons(list);
     * }
     * </pre>
     * </p>
     *
     * @param list the list to get the combinaisons
     * @return the combinaisons of the list
     */
    private static Set<List<Integer>> getCombinaisons(List<Integer> list) {
        return new BruteForce().getCombinaisons(list);
    }

    /**
     * Test the getCombinaisons method.
     */
    @Test
    void testGetCombinaisons() {
        // Test with only one element, the result should be a list with only one element
        assertEquals(Set.of(List.of(1)), getCombinaisons(List.of(1)));
        assertEquals(Set.of(
                List.of(1, 2),
                List.of(2, 1)
        ), getCombinaisons(List.of(1, 2)));
        assertEquals(Set.of(
                List.of(1, 2, 3),
                List.of(1, 3, 2),
                List.of(2, 1, 3),
                List.of(2, 3, 1),
                List.of(3, 1, 2),
                List.of(3, 2, 1)
        ), getCombinaisons(List.of(1, 2, 3)));
        assertEquals(Set.of(
                List.of(1, 2, 2),
                List.of(2, 1, 2),
                List.of(2, 2, 1)
        ), getCombinaisons(List.of(1, 2, 2)));
        // Test with a list with the same element. The result list shouldn't have duplicates
        assertEquals(Set.of(List.of(3, 3, 3)), getCombinaisons(List.of(3, 3, 3)));

    }

    @Test
    void timeGetCombinaisons() {
        BruteForce bruteForce = new BruteForce();
        List<List<Integer>> small = List.of(
                List.of(1),
                List.of(1, 2),
                List.of(1, 2, 3),
                List.of(1, 2, 3, 4),
                List.of(1, 2, 3, 4, 5),
                List.of(1, 2, 3, 4, 5, 6),
                List.of(1, 2, 3, 4, 5, 6, 7),
                List.of(1, 2, 3, 4, 5, 6, 7, 8),
                List.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
        );

        List<List<Integer>> tooBig = List.of(
                List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)
        );

        small.forEach(list -> assertDoesNotThrow(() ->
                {
                    double timeToRunInMilliS = timeRun(() -> bruteForce.getCombinaisons(list)) / 1_000_000.0;
                    System.out.printf("Time to get combinaisons of %d elements: %.4fms%n", list.size(), timeToRunInMilliS);
                }
        ));
        tooBig.forEach(list -> assertThrows(OutOfMemoryError.class, () -> bruteForce.getCombinaisons(list)));
    }


    private long timeRun(Runnable runnable) {
        long start = System.nanoTime();
        runnable.run();
        return System.nanoTime() - start;
    }
}