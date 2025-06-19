package org.tetokeguii.phase1;

import org.springframework.stereotype.Component;

import java.util.Arrays;
@Component
public class BubbleSort implements SortAlgorithm {
    public int[] sort(int[] numbers) {
        if (numbers == null || numbers.length <= 1) {
            return numbers;
        }

        int[] result = Arrays.copyOf(numbers, numbers.length);
        int n = result.length;

        for (int i = 0; i < n -1; i++){
            boolean swapped = false;
            for (int j = 0; j< n-i-1; j++) {
                if (result[j] > result[j+1]) {
                    int tempResult = result[j];
                    result[j] = result[j+1];
                    result[j+1] = tempResult;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        return result;
    }
}
