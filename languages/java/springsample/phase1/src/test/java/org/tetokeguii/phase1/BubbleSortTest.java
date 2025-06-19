package org.tetokeguii.phase1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class BubbleSortTest {

    private BubbleSort bubbleSort;

    @BeforeEach
    void setUp() {
        bubbleSort = new BubbleSort();
    }

    @Test
    @DisplayName("Should sort array with multiple elements correctly")
    void testSortMultipleElements() {
        int[] input = {64, 34, 25, 12, 22, 11, 90};
        int[] expected = {11, 12, 22, 25, 34, 64, 90};

        int[] result = bubbleSort.sort(input);

        assertArrayEquals(expected, result);
        assertArrayEquals(new int[]{64, 34, 25, 12, 22, 11, 90}, input);
    }

    @Test
    @DisplayName("Should handle already sorted array")
    void testSortAlreadySorted() {
        int[] input = {1, 2, 3, 4, 5};
        int[] expected = {1, 2, 3, 4, 5};

        int[] result = bubbleSort.sort(input);

        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle array with duplicates")
    void testSortWithDuplicates() {
        int[] input = {1, 5, 4, 4, 5};
        int[] expected = {1, 4, 4, 5, 5};

        int[] result = bubbleSort.sort(input);

        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle single element array")
    void testSortSingleElement() {
        int[] input = {42};
        int[] expected = {42};

        int[] result = bubbleSort.sort(input);

        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle empty array")
    void testSortEmptyArray() {
        int[] input = {};
        int[] expected = {};

        int[] result = bubbleSort.sort(input);

        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle null array")
    void testSortNullArray() {
        int[] result = bubbleSort.sort(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Should handle array with negative numbers")
    void testSortWithNegativeNumbers() {
        int[] input = {-3, -1, -5, 0, 2, -2};
        int[] expected = {-5, -3, -2, -1, 0, 2};

        int[] result = bubbleSort.sort(input);

        assertArrayEquals(expected, result);
    }

}