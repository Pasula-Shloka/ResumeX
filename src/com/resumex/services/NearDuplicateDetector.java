package com.resumex.services;

import java.util.ArrayList;
import java.util.List;

import com.resumex.algorithms.EditDistance;
import com.resumex.models.Resume;

public class NearDuplicateDetector {

    public static class DuplicatePair {
        private final String candidateA;
        private final String fileA;
        private final String candidateB;
        private final String fileB;
        private final double similarity;
        private final String method;

        public DuplicatePair(String candidateA, String fileA, String candidateB, String fileB, double similarity, String method) {
            this.candidateA = candidateA;
            this.fileA = fileA;
            this.candidateB = candidateB;
            this.fileB = fileB;
            this.similarity = similarity;
            this.method = method;
        }

        public String getCandidateA() {
            return candidateA;
        }

        public String getFileA() {
            return fileA;
        }

        public String getCandidateB() {
            return candidateB;
        }

        public String getFileB() {
            return fileB;
        }

        public double getSimilarity() {
            return similarity;
        }

        public String getMethod() {
            return method;
        }
    }

    private final EditDistance editDistance;

    public NearDuplicateDetector() {
        editDistance = new EditDistance();
    }

    public double calculateSimilarity(Resume first, Resume second) {
        if (first == null || second == null) {
            return 0.0;
        }

        String t1 = getNormalizedSample(first.getRawText());
        String t2 = getNormalizedSample(second.getRawText());

        return editDistance.similarity(t1, t2);
    }

    public boolean areNearDuplicates(Resume first, Resume second, double threshold) {
        return calculateSimilarity(first, second) >= threshold;
    }

    public List<DuplicatePair> findDuplicatePairs(List<Resume> resumes, double threshold) {
        List<DuplicatePair> pairs = new ArrayList<>();
        if (resumes == null || resumes.size() < 2) {
            return pairs;
        }

        for (int i = 0; i < resumes.size(); i++) {
            for (int j = i + 1; j < resumes.size(); j++) {
                Resume r1 = resumes.get(i);
                Resume r2 = resumes.get(j);

                double sim = calculateSimilarity(r1, r2);
                if (sim >= threshold) {
                    pairs.add(new DuplicatePair(
                            r1.getCandidateName(),
                            r1.getFileName(),
                            r2.getCandidateName(),
                            r2.getFileName(),
                            Math.round(sim * 100.0) / 100.0,
                            "Normalized Edit Distance"
                    ));
                }
            }
        }

        return pairs;
    }

    public List<String> findNearDuplicates(List<Resume> resumes, double threshold) {
        List<String> results = new ArrayList<>();
        List<DuplicatePair> pairs = findDuplicatePairs(resumes, threshold);

        for (DuplicatePair pair : pairs) {
            results.add(
                    pair.getCandidateA() + " (" + pair.getFileA() + ") <-> "
                    + pair.getCandidateB() + " (" + pair.getFileB() + ") : "
                    + String.format("%.2f%%", pair.getSimilarity())
            );
        }

        return results;
    }

    private String getNormalizedSample(String text) {
        if (text == null) {
            return "";
        }
        String normalized = text.toLowerCase().replaceAll("\\s+", " ").trim();
        if (normalized.length() <= 400) {
            return normalized;
        }
        return normalized.substring(0, 400);
    }

    public void printNearDuplicates(List<Resume> resumes, double threshold) {
        System.out.println("\n==============================================");
        System.out.println("         NEAR-DUPLICATE DETECTION");
        System.out.println("==============================================");

        List<String> results = findNearDuplicates(resumes, threshold);
        if (results.isEmpty()) {
            System.out.println("No near-duplicate resumes found.");
        } else {
            for (String result : results) {
                System.out.println(result);
            }
        }
        System.out.println("==============================================");
    }
}