package com.resumex.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LCPArray {

    /*
     * Kasai's Algorithm
     *
     * Builds the LCP array from a suffix array in linear O(n) time.
     *
     * LCP = Longest Common Prefix
     */
    public int[] build(String text, int[] suffixArray) {
        if (text == null || suffixArray == null || text.length() != suffixArray.length) {
            return new int[0];
        }

        int n = text.length();
        int[] rank = new int[n];

        for (int i = 0; i < n; i++) {
            rank[suffixArray[i]] = i;
        }

        int[] lcp = new int[n];
        int k = 0;

        for (int i = 0; i < n; i++) {
            int currentRank = rank[i];

            if (currentRank == 0) {
                continue;
            }

            int previousSuffix = suffixArray[currentRank - 1];

            while (
                    i + k < n &&
                    previousSuffix + k < n &&
                    text.charAt(i + k) == text.charAt(previousSuffix + k)
            ) {
                k++;
            }

            lcp[currentRank] = k;

            if (k > 0) {
                k--;
            }
        }

        return lcp;
    }

    /**
     * Finds common phrases between two texts using Generalized Suffix Array and LCP.
     * Suffixes from text1 and text2 are separated by a delimiter '#'.
     */
    public List<String> findCommonPhrases(String text1, String text2, int minLength, int maxCount) {
        List<String> commonPhrases = new ArrayList<>();
        if (text1 == null || text2 == null || text1.isEmpty() || text2.isEmpty()) {
            return commonPhrases;
        }

        String s1 = text1.toLowerCase();
        String s2 = text2.toLowerCase();
        int sepIndex = s1.length();
        String combined = s1 + "#" + s2;

        SuffixArray sa = new SuffixArray();
        int[] suffixArray = sa.build(combined);
        int[] lcp = build(combined, suffixArray);

        Set<String> seen = new HashSet<>();

        for (int i = 1; i < combined.length(); i++) {
            int len = lcp[i];
            if (len >= minLength) {
                int sa1 = suffixArray[i];
                int sa2 = suffixArray[i - 1];

                // One suffix must start in text1 and the other in text2
                boolean crossBoundary = (sa1 < sepIndex && sa2 > sepIndex) || (sa1 > sepIndex && sa2 < sepIndex);
                if (crossBoundary) {
                    String phrase = combined.substring(sa1, sa1 + len).trim();
                    // Clean up punctuation and whitespace
                    phrase = phrase.replaceAll("[^a-zA-Z0-9+# ]", "").trim();
                    if (phrase.length() >= minLength && !seen.contains(phrase)) {
                        seen.add(phrase);
                        commonPhrases.add(phrase);
                        if (commonPhrases.size() >= maxCount) {
                            break;
                        }
                    }
                }
            }
        }

        return commonPhrases;
    }

    /*
     * Prints suffix array and LCP together.
     */
    public void printLCP(String text, int[] suffixArray) {
        int[] lcp = build(text, suffixArray);

        System.out.println("\nSuffix Array + LCP:");
        System.out.println("Index\tSuffix\t\tLCP");

        for (int i = 0; i < suffixArray.length; i++) {
            int index = suffixArray[i];
            System.out.println(
                    index + "\t" +
                    text.substring(index) + "\t\t" +
                    lcp[i]
            );
        }
    }
}