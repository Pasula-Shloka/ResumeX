package com.resumex.algorithms;

public class KMPMatcher {

    // Creates the LPS (Longest Prefix Suffix) array
    public int[] buildLPS(String pattern) {

        int[] lps = new int[pattern.length()];

        int length = 0;
        int i = 1;

        while (i < pattern.length()) {

            if (pattern.charAt(i) == pattern.charAt(length)) {

                length++;
                lps[i] = length;
                i++;

            } else {

                if (length != 0) {

                    length = lps[length - 1];

                } else {

                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }

    // Searches for a pattern inside a text
    public int search(String text, String pattern) {

        if (text == null || pattern == null) {
            return -1;
        }

        if (pattern.length() == 0) {
            return 0;
        }

        int[] lps = buildLPS(pattern);

        int i = 0;
        int j = 0;

        while (i < text.length()) {

            if (text.charAt(i) == pattern.charAt(j)) {

                i++;
                j++;

                if (j == pattern.length()) {
                    return i - j;
                }

            } else {

                if (j != 0) {

                    j = lps[j - 1];

                } else {

                    i++;
                }
            }
        }

        return -1;
    }

    // Checks whether a skill exists in the resume
    public boolean contains(String resumeText, String skill) {

        String text = resumeText.toLowerCase();
        String pattern = skill.toLowerCase();

        return search(text, pattern) != -1;
    }
}