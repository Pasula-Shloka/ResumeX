package com.resumex.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Approximation Algorithm for Bipartite Skill Matching & Fuzzy Recognition.
 *
 * DSA Context:
 * Exact string matching fails for spelling variations (e.g. "Jvaa" vs "Java", "Springboot" vs "Spring Boot").
 * Finding the optimal one-to-one assignment between candidate skills and job requirements is a
 * Maximum Weight Bipartite Matching problem (solvable in O(V^2 E) via Hungarian algorithm).
 * We implement a Greedy 1/2-Approximation Algorithm:
 * 1. Compute pairwise Levenshtein similarity for all (candidateSkill, requiredSkill) pairs.
 * 2. Sort pairs by similarity descending.
 * 3. Greedily match pairs above threshold, ensuring each skill is matched at most once.
 * Time Complexity: O(|C|*|R| log(|C|*|R|)) where |C| = candidate skills, |R| = required skills.
 */
public class ApproximateMatcher {

    private final EditDistance editDistance;

    public static class SkillPairMatch {
        private final String candidateSkill;
        private final String requiredSkill;
        private final double similarity;

        public SkillPairMatch(String candidateSkill, String requiredSkill, double similarity) {
            this.candidateSkill = candidateSkill;
            this.requiredSkill = requiredSkill;
            this.similarity = similarity;
        }

        public String getCandidateSkill() {
            return candidateSkill;
        }

        public String getRequiredSkill() {
            return requiredSkill;
        }

        public double getSimilarity() {
            return similarity;
        }
    }

    public ApproximateMatcher() {
        editDistance = new EditDistance();
    }

    /*
     * Determines how similar two strings are via Levenshtein Edit Distance.
     */
    public double similarity(String first, String second) {
        return editDistance.similarity(first, second);
    }

    /*
     * Determines whether two strings are approximately equal within a threshold.
     */
    public boolean isSimilar(String first, String second, double threshold) {
        return similarity(first, second) >= threshold;
    }

    /*
     * Finds the closest skill from a list of skills.
     */
    public String findClosestSkill(String target, String[] availableSkills) {
        String bestMatch = null;
        double bestScore = 0;

        for (String skill : availableSkills) {
            double score = similarity(target, skill);
            if (score > bestScore) {
                bestScore = score;
                bestMatch = skill;
            }
        }

        return bestMatch;
    }

    /**
     * Greedy Bipartite Matching Approximation.
     * Greedily matches candidate skills to required skills to maximize cumulative similarity.
     */
    public List<SkillPairMatch> greedyBipartiteMatch(List<String> candidateSkills, List<String> requiredSkills, double threshold) {
        List<SkillPairMatch> matches = new ArrayList<>();
        if (candidateSkills == null || requiredSkills == null || candidateSkills.isEmpty() || requiredSkills.isEmpty()) {
            return matches;
        }

        // Generate all candidate-required pairs
        List<SkillPairMatch> allPairs = new ArrayList<>();
        for (String cSkill : candidateSkills) {
            for (String rSkill : requiredSkills) {
                double sim = similarity(cSkill, rSkill);
                if (sim >= threshold) {
                    allPairs.add(new SkillPairMatch(cSkill, rSkill, sim));
                }
            }
        }

        // Sort descending by similarity
        allPairs.sort((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()));

        // Greedily pick disjoint matches
        Set<String> matchedCandidate = new HashSet<>();
        Set<String> matchedRequired = new HashSet<>();

        for (SkillPairMatch pair : allPairs) {
            if (!matchedCandidate.contains(pair.getCandidateSkill().toLowerCase()) &&
                !matchedRequired.contains(pair.getRequiredSkill().toLowerCase())) {
                matches.add(pair);
                matchedCandidate.add(pair.getCandidateSkill().toLowerCase());
                matchedRequired.add(pair.getRequiredSkill().toLowerCase());
            }
        }

        return matches;
    }

    /**
     * Calculates the overall greedy approximation score (0 - 100).
     */
    public double calculateGreedyMatchScore(List<String> candidateSkills, List<String> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return 0.0;
        }
        if (candidateSkills == null || candidateSkills.isEmpty()) {
            return 0.0;
        }

        List<SkillPairMatch> matched = greedyBipartiteMatch(candidateSkills, requiredSkills, 70.0);
        double totalSim = 0.0;
        for (SkillPairMatch pair : matched) {
            totalSim += pair.getSimilarity();
        }

        return Math.min(100.0, totalSim / requiredSkills.size());
    }
}