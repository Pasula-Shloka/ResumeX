package com.resumex.algorithms;

public class SequenceAlignment {

    private static final int MATCH_SCORE = 2;
    private static final int MISMATCH_SCORE = -1;
    private static final int GAP_PENALTY = -2;

    /*
     * Calculates the Global Sequence Alignment score
     * using the Needleman-Wunsch dynamic programming algorithm.
     */
    public int calculateScore(String first, String second) {

        if (first == null || second == null) {
            return 0;
        }

        first = first.toLowerCase();
        second = second.toLowerCase();

        int m = first.length();
        int n = second.length();

        int[][] dp = new int[m + 1][n + 1];

        // First column
        for (int i = 1; i <= m; i++) {
            dp[i][0] =
                    dp[i - 1][0] + GAP_PENALTY;
        }

        // First row
        for (int j = 1; j <= n; j++) {
            dp[0][j] =
                    dp[0][j - 1] + GAP_PENALTY;
        }

        // Fill DP table
        for (int i = 1; i <= m; i++) {

            for (int j = 1; j <= n; j++) {

                int diagonal;

                if (first.charAt(i - 1)
                        == second.charAt(j - 1)) {

                    diagonal =
                            dp[i - 1][j - 1]
                            + MATCH_SCORE;

                } else {

                    diagonal =
                            dp[i - 1][j - 1]
                            + MISMATCH_SCORE;
                }

                int up =
                        dp[i - 1][j]
                        + GAP_PENALTY;

                int left =
                        dp[i][j - 1]
                        + GAP_PENALTY;

                dp[i][j] =
                        Math.max(
                                diagonal,
                                Math.max(up, left)
                        );
            }
        }

        return dp[m][n];
    }

    /*
     * Converts the alignment score into a percentage.
     */
    public double similarity(
            String first,
            String second) {

        if (first == null || second == null) {
            return 0;
        }

        if (first.isEmpty() && second.isEmpty()) {
            return 100;
        }

        first = first.toLowerCase();
        second = second.toLowerCase();

        int score =
                calculateScore(first, second);

        /*
         * Calculate the maximum possible score
         * for the two sequences.
         */
        int maxLength =
                Math.max(
                        first.length(),
                        second.length()
                );

        int maximumScore =
                maxLength * MATCH_SCORE;

        if (maximumScore == 0) {
            return 0;
        }

        /*
         * Convert the alignment score into a
         * 0-100 similarity value.
         */
        double similarity =
                ((double) score /
                        maximumScore) * 100;

        /*
         * Keep the result between 0 and 100.
         */
        return Math.max(
                0,
                Math.min(
                        100,
                        similarity
                )
        );
    }

    /*
     * Prints the complete DP table.
     * Useful for demonstration during viva.
     */
    public void printDPTable(
            String first,
            String second) {

        first = first.toLowerCase();
        second = second.toLowerCase();

        int m = first.length();
        int n = second.length();

        int[][] dp =
                new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            dp[i][0] =
                    dp[i - 1][0] + GAP_PENALTY;
        }

        for (int j = 1; j <= n; j++) {
            dp[0][j] =
                    dp[0][j - 1] + GAP_PENALTY;
        }

        for (int i = 1; i <= m; i++) {

            for (int j = 1; j <= n; j++) {

                int diagonal;

                if (first.charAt(i - 1)
                        == second.charAt(j - 1)) {

                    diagonal =
                            dp[i - 1][j - 1]
                            + MATCH_SCORE;

                } else {

                    diagonal =
                            dp[i - 1][j - 1]
                            + MISMATCH_SCORE;
                }

                int up =
                        dp[i - 1][j]
                        + GAP_PENALTY;

                int left =
                        dp[i][j - 1]
                        + GAP_PENALTY;

                dp[i][j] =
                        Math.max(
                                diagonal,
                                Math.max(up, left)
                        );
            }
        }

        System.out.println();
        System.out.println(
                "Sequence Alignment DP Table"
        );

        System.out.print("    ");

        for (int j = 0; j < n; j++) {
            System.out.printf(
                    "%4c",
                    second.charAt(j)
            );
        }

        System.out.println();

        for (int i = 0; i <= m; i++) {

            if (i == 0) {
                System.out.print("  ");
            } else {
                System.out.printf(
                        "%2c",
                        first.charAt(i - 1)
                );
            }

            for (int j = 0; j <= n; j++) {

                System.out.printf(
                        "%4d",
                        dp[i][j]
                );
            }

            System.out.println();
        }

        System.out.println(
                "Final Alignment Score: "
                + dp[m][n]
        );
    }
}