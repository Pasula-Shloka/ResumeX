package com.resumex.algorithms;

public class EditDistance {

    /*
     * Calculates the minimum number of operations required
     * to convert one string into another.
     *
     * Operations:
     * 1. Insert
     * 2. Delete
     * 3. Replace
     */
    public int calculate(String first, String second) {

        first = first.toLowerCase();
        second = second.toLowerCase();

        int m = first.length();
        int n = second.length();

        int[][] dp = new int[m + 1][n + 1];

        // Convert empty string to first string
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }

        // Convert empty string to second string
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {

            for (int j = 1; j <= n; j++) {

                if (first.charAt(i - 1) == second.charAt(j - 1)) {

                    dp[i][j] = dp[i - 1][j - 1];

                } else {

                    int insert = dp[i][j - 1];
                    int delete = dp[i - 1][j];
                    int replace = dp[i - 1][j - 1];

                    dp[i][j] = 1 + Math.min(
                            insert,
                            Math.min(delete, replace)
                    );
                }
            }
        }

        return dp[m][n];
    }

    /*
     * Converts edit distance into a similarity percentage.
     */
    public double similarity(String first, String second) {

        if (first == null || second == null) {
            return 0;
        }

        if (first.length() == 0 && second.length() == 0) {
            return 100;
        }

        int distance = calculate(first, second);

        int maxLength = Math.max(first.length(), second.length());

        double similarity =
                (1.0 - ((double) distance / maxLength)) * 100;

        return Math.max(0, similarity);
    }

    /*
     * Displays the DP matrix.
     * Useful for the Algorithm Lab / faculty demonstration.
     */
    public void printDPTable(String first, String second) {

        first = first.toLowerCase();
        second = second.toLowerCase();

        int m = first.length();
        int n = second.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {

            for (int j = 1; j <= n; j++) {

                if (first.charAt(i - 1) == second.charAt(j - 1)) {

                    dp[i][j] = dp[i - 1][j - 1];

                } else {

                    dp[i][j] = 1 + Math.min(
                            dp[i][j - 1],
                            Math.min(
                                    dp[i - 1][j],
                                    dp[i - 1][j - 1]
                            )
                    );
                }
            }
        }

        System.out.println("\nEdit Distance DP Table:");

        for (int i = 0; i <= m; i++) {

            for (int j = 0; j <= n; j++) {

                System.out.print(dp[i][j] + "\t");
            }

            System.out.println();
        }
    }
}