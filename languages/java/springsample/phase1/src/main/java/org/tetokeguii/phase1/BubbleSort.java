package org.tetokeguii.phase1;

import org.springframework.stereotype.Component;

import java.util.Arrays;
@Component
public class BubbleSort implements SortAlgorithm {
    public int[] sort(int[] numbers) {
        return Arrays.stream(numbers).sorted().toArray();
    }
}
