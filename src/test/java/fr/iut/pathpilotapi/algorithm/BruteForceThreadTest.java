package fr.iut.pathpilotapi.algorithm;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BruteForceThreadTest {

    @Test
    void testGetSubLists() {
        List<Integer> list = List.of(1, 2, 3, 4);
        List<List<Integer>> subLists = BruteForceThread.getSubLists(list, 2);
        assertEquals(2, subLists.size());
        assertEquals(List.of(1, 2), subLists.get(0));
        assertEquals(List.of(3, 4), subLists.get(1));

        subLists = BruteForceThread.getSubLists(list, 3);
        assertEquals(2, subLists.size());
        assertEquals(List.of(1, 2, 3), subLists.get(0));
        assertEquals(List.of(4), subLists.get(1));

        for (int nbElementSubList = 4; nbElementSubList <= 20; nbElementSubList++) {
            subLists = BruteForceThread.getSubLists(list, nbElementSubList);
            assertEquals(1, subLists.size());
            assertEquals(list, subLists.getFirst());
        }

        list = List.of(1, 2, 3, 4, 5);
        subLists = BruteForceThread.getSubLists(list, 2);
        assertEquals(3, subLists.size());
        assertEquals(List.of(1, 2), subLists.get(0));
        assertEquals(List.of(3, 4), subLists.get(1));
        assertEquals(List.of(5), subLists.get(2));

        subLists = BruteForceThread.getSubLists(list, 3);
        assertEquals(2, subLists.size());
        assertEquals(List.of(1, 2, 3), subLists.get(0));
        assertEquals(List.of(4, 5), subLists.get(1));

        subLists = BruteForceThread.getSubLists(list, 4);
        assertEquals(2, subLists.size());
        assertEquals(List.of(1, 2, 3, 4), subLists.get(0));
        assertEquals(List.of(5), subLists.get(1));

        for (int nbElementSubList = 5; nbElementSubList <= 20; nbElementSubList++) {
            subLists = BruteForceThread.getSubLists(list, nbElementSubList);
            assertEquals(1, subLists.size());
            assertEquals(list, subLists.getFirst());
        }
    }
}