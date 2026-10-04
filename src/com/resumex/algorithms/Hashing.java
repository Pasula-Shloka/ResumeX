package com.resumex.algorithms;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Hashing {

    private static final long PRIME_BASE = 31;
    private static final long PRIME_MOD = 1_000_000_007L;

    /*
     * Creates a SHA-256 cryptographic hash for a resume.
     * Used for exact duplicate detection.
     * Time Complexity: O(n) where n is text length.
     */
    public String generateHash(String text) {
        if (text == null) {
            return "";
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();

            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }

            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /*
     * Checks whether two texts are exactly identical using their hashes.
     */
    public boolean areDuplicates(String first, String second) {
        String hash1 = generateHash(normalize(first));
        String hash2 = generateHash(normalize(second));
        return hash1.equals(hash2);
    }

    /*
     * Normalizes text before hashing.
     */
    public String normalize(String text) {
        if (text == null) {
            return "";
        }
        return text
                .toLowerCase()
                .replaceAll("\\s+", " ")
                .trim();
    }

    /*
     * Polynomial Rolling Hash (Rabin-Karp style).
     * Useful for rolling window comparison and fast checksumming.
     * Formula: H = sum(c_i * base^(n-1-i)) mod M
     */
    public long polynomialRollingHash(String text) {
        if (text == null || text.isEmpty()) {
            return 0L;
        }
        long hash = 0;
        for (int i = 0; i < text.length(); i++) {
            hash = (hash * PRIME_BASE + text.charAt(i)) % PRIME_MOD;
        }
        return hash;
    }

    /*
     * Frequency and count operations using Hash Table (O(1) average lookup/update).
     */
    public Map<String, Integer> computeKeywordFrequencies(String text) {
        Map<String, Integer> freqMap = new HashMap<>();
        if (text == null || text.trim().isEmpty()) {
            return freqMap;
        }

        String[] tokens = text.toLowerCase().split("[^a-zA-Z0-9+#]+");
        for (String token : tokens) {
            if (token.length() > 2) {
                freqMap.put(token, freqMap.getOrDefault(token, 0) + 1);
            }
        }
        return freqMap;
    }

    /*
     * Hash Set for O(1) keyword and skill lookup.
     */
    public Set<String> createKeywordSet(List<String> keywords) {
        Set<String> set = new HashSet<>();
        if (keywords != null) {
            for (String kw : keywords) {
                if (kw != null && !kw.trim().isEmpty()) {
                    set.add(kw.trim().toLowerCase());
                }
            }
        }
        return set;
    }
}