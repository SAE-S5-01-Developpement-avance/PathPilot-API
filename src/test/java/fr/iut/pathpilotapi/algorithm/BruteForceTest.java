package fr.iut.pathpilotapi.algorithm;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}