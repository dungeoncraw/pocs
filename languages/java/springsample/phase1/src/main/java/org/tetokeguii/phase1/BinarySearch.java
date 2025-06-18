package org.tetokeguii.phase1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BinarySearch {
    @Autowired
    private final SortAlgorithm sortAlgorithm;
    public BinarySearch(SortAlgorithm sortAlgorithm) {
        super();
        this.sortAlgorithm = sortAlgorithm;
    }
    public int binarySearch(int[] array, int target) {
        int[] sortedArray = sortAlgorithm.sort(array);
        return 3;
    }
}
