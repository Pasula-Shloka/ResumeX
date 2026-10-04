package com.resumex.models;

public class MatchResult {

    private double skillMatchScore;
    private double stringMatchScore;
    private double editDistanceScore;
    private double sequenceAlignmentScore;
    private double suffixLcpScore;
    private double approximateScore;

    private double overallScore;

    public MatchResult(
            double skillMatchScore,
            double stringMatchScore,
            double editDistanceScore,
            double sequenceAlignmentScore,
            double suffixLcpScore,
            double approximateScore) {

        this.skillMatchScore = skillMatchScore;
        this.stringMatchScore = stringMatchScore;
        this.editDistanceScore = editDistanceScore;
        this.sequenceAlignmentScore = sequenceAlignmentScore;
        this.suffixLcpScore = suffixLcpScore;
        this.approximateScore = approximateScore;

        calculateOverallScore();
    }

    private void calculateOverallScore() {

        overallScore =
                skillMatchScore * 0.30 +
                stringMatchScore * 0.20 +
                editDistanceScore * 0.15 +
                sequenceAlignmentScore * 0.15 +
                suffixLcpScore * 0.10 +
                approximateScore * 0.10;

        overallScore =
                Math.max(0, Math.min(100, overallScore));
    }

    public double getSkillMatchScore() {
        return skillMatchScore;
    }

    public double getStringMatchScore() {
        return stringMatchScore;
    }

    public double getEditDistanceScore() {
        return editDistanceScore;
    }

    public double getSequenceAlignmentScore() {
        return sequenceAlignmentScore;
    }

    public double getSuffixLcpScore() {
        return suffixLcpScore;
    }

    public double getApproximateScore() {
        return approximateScore;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public void printBreakdown() {

        System.out.println("----------------------------------");

        System.out.printf(
                "Skill Match        : %.2f%%%n",
                skillMatchScore
        );

        System.out.printf(
                "String Matching    : %.2f%%%n",
                stringMatchScore
        );

        System.out.printf(
                "Edit Distance      : %.2f%%%n",
                editDistanceScore
        );

        System.out.printf(
                "Sequence Alignment : %.2f%%%n",
                sequenceAlignmentScore
        );

        System.out.printf(
                "Suffix + LCP       : %.2f%%%n",
                suffixLcpScore
        );

        System.out.printf(
                "Approximate Match  : %.2f%%%n",
                approximateScore
        );

        System.out.println("----------------------------------");

        System.out.printf(
                "OVERALL SCORE      : %.2f%%%n",
                overallScore
        );

        System.out.println("----------------------------------");
    }
}