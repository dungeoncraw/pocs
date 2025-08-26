package org.tetokeguii;

import java.util.Arrays;

public class DungeonGame {
    
    public int calculateMinimumHP(int[][] dungeon) {
        if (dungeon == null || dungeon.length == 0 || dungeon[0].length == 0) {
            return 1;
        }
        
        int m = dungeon.length;
        int n = dungeon[0].length;
        
        int[][] dp = new int[m][n];
        
        dp[m-1][n-1] = Math.max(1, 1 - dungeon[m-1][n-1]);
        
        for (int j = n-2; j >= 0; j--) {
            dp[m-1][j] = Math.max(1, dp[m-1][j+1] - dungeon[m-1][j]);
        }
        
        for (int i = m-2; i >= 0; i--) {
            dp[i][n-1] = Math.max(1, dp[i+1][n-1] - dungeon[i][n-1]);
        }
        
        for (int i = m-2; i >= 0; i--) {
            for (int j = n-2; j >= 0; j--) {
                int minHealthOnExit = Math.min(dp[i+1][j], dp[i][j+1]);
                dp[i][j] = Math.max(1, minHealthOnExit - dungeon[i][j]);
            }
        }
        
        return dp[0][0];
    }
    
    public int calculateMinimumHPOptimized(int[][] dungeon) {
        if (dungeon == null || dungeon.length == 0 || dungeon[0].length == 0) {
            return 1;
        }
        
        var m = dungeon.length;
        var n = dungeon[0].length;
        
        var dp = new int[n];
        
        dp[n-1] = Math.max(1, 1 - dungeon[m-1][n-1]);
        
        for (int j = n-2; j >= 0; j--) {
            dp[j] = Math.max(1, dp[j+1] - dungeon[m-1][j]);
        }
        
        for (int i = m-2; i >= 0; i--) {
            dp[n-1] = Math.max(1, dp[n-1] - dungeon[i][n-1]);
            
            for (int j = n-2; j >= 0; j--) {
                var minHealthOnExit = Math.min(dp[j], dp[j+1]);
                dp[j] = Math.max(1, minHealthOnExit - dungeon[i][j]);
            }
        }
        
        return dp[0];
    }
    
    public int calculateMinimumHPFunctional(int[][] dungeon) {
        if (dungeon == null || dungeon.length == 0 || dungeon[0].length == 0) {
            return 1;
        }
        
        record Position(int row, int col) {}
        record HealthInfo(int minHealth, int currentHealth) {}
        
        var m = dungeon.length;
        var n = dungeon[0].length;
        var dp = new int[m][n];
        
        dp[m-1][n-1] = Math.max(1, 1 - dungeon[m-1][n-1]);
        
        for (int j = n-2; j >= 0; j--) {
            dp[m-1][j] = Math.max(1, dp[m-1][j+1] - dungeon[m-1][j]);
        }
        
        for (int i = m-2; i >= 0; i--) {
            dp[i][n-1] = Math.max(1, dp[i+1][n-1] - dungeon[i][n-1]);
        }
        
        for (int i = m-2; i >= 0; i--) {
            for (int j = n-2; j >= 0; j--) {
                var minHealthOnExit = Math.min(dp[i+1][j], dp[i][j+1]);
                dp[i][j] = Math.max(1, minHealthOnExit - dungeon[i][j]);
            }
        }
        
        return dp[0][0];
    }
    
    public static void main(String[] args) {
        var solution = new DungeonGame();
        
        var dungeon1 = new int[][]{
            {-3, 5},
            {1, -4}
        };
        
        var dungeon2 = new int[][]{
            {-200, -3, 4},
            {1, -3, -3},
            {4, -3, -200}
        };
        
        var dungeon3 = new int[][]{
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        
        System.out.println("=== Dungeon Game Solutions ===");
        
        var testCases = new int[][][]{dungeon1, dungeon2, dungeon3};
        var testNames = new String[]{"Basic Example", "Complex Dungeon", "All Positive"};
        
        for (int i = 0; i < testCases.length; i++) {
            System.out.printf("\nTest Case %d (%s):\n", i+1, testNames[i]);
            System.out.println("Dungeon:");
            printDungeon(testCases[i]);
            
            var result1 = solution.calculateMinimumHP(testCases[i]);
            var result2 = solution.calculateMinimumHPOptimized(testCases[i]);
            var result3 = solution.calculateMinimumHPFunctional(testCases[i]);
            
            System.out.printf("2D DP Solution: %d\n", result1);
            System.out.printf("Space Optimized: %d\n", result2);
            System.out.printf("Functional Style: %d\n", result3);
            
            assert result1 == result2 && result2 == result3 : "Solutions don't match!";
        }
    }
    
    private static void printDungeon(int[][] dungeon) {
        Arrays.stream(dungeon)
              .forEach(row -> {
                  Arrays.stream(row)
                        .forEach(cell -> System.out.printf("%4d ", cell));
                  System.out.println();
              });
    }
}