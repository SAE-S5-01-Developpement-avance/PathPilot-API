package fr.iut.pathpilotapi.algorithme;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BruteForceTest {

    @Test
    void testGetCombinaisons() {
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
        assertEquals(Set.of(List.of(3, 3, 3)), getCombinaisons(List.of(3, 3, 3)));

    }

    private static Set<List<Integer>> getCombinaisons(List<Integer> list) {
        return new BruteForce().getCombinaisons(list);
    }
}