package com.resumex.algorithms;

import java.util.Arrays;

public class SuffixArray {

    /*
     * Builds a suffix array.
     *
     * A suffix is a substring starting from
     * a particular position until the end.
     * Suffix array stores starting indices of suffixes in lexicographical order.
     * Time Complexity: O(n^2 log n) naive sort or O(n log^2 n).
     */
    public int[] build(String text) {
        if (text == null || text.isEmpty()) {
            return new int[0];
        }

        final String input = text.toLowerCase();
        int n = input.length();
        Integer[] suffixes = new Integer[n];

        for (int i = 0; i < n; i++) {
            suffixes[i] = i;
        }

        Arrays.sort(suffixes, (a, b) -> {
            String suffixA = input.substring(a);
            String suffixB = input.substring(b);
            return suffixA.compareTo(suffixB);
        });

        int[] suffixArray = new int[n];
        for (int i = 0; i < n; i++) {
            suffixArray[i] = suffixes[i];
        }

        return suffixArray;
    }

    /*
     * Efficient pattern search using binary search on Suffix Array.
     * Time Complexity: O(m * log n) where m = pattern length, n = text length.
     */
    public int search(String text, int[] suffixArray, String pattern) {
        if (text == null || suffixArray == null || pattern == null || pattern.isEmpty() || suffixArray.length == 0) {
            return -1;
        }

        String lowerText = text.toLowerCase();
        String lowerPattern = pattern.toLowerCase();
        int low = 0;
        int high = suffixArray.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int suffixStart = suffixArray[mid];

            int cmp = comparePrefix(lowerText, suffixStart, lowerPattern);

            if (cmp == 0) {
                return suffixStart;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1;
    }

    public boolean contains(String text, int[] suffixArray, String pattern) {
        return search(text, suffixArray, pattern) != -1;
    }

    private int comparePrefix(String text, int suffixStart, String pattern) {
        int textRemaining = text.length() - suffixStart;
        int compareLength = Math.min(textRemaining, pattern.length());

        for (int i = 0; i < compareLength; i++) {
            char tc = text.charAt(suffixStart + i);
            char pc = pattern.charAt(i);
            if (tc != pc) {
                return tc - pc;
            }
        }

        if (textRemaining < pattern.length()) {
            return -1;
        }

        return 0; // Pattern matches prefix of this suffix
    }

    /*
     * Prints the suffix array.
     */
    public void printSuffixArray(String text) {
        int[] suffixArray = build(text);

        System.out.println("\nSuffix Array:");
        System.out.println("Index\tSuffix");

        for (int index : suffixArray) {
            System.out.println(index + "\t" + text.substring(index));
        }
    }
}