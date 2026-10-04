package com.resumex.models;

import java.util.ArrayList;
import java.util.List;

public class Candidate {

    private Resume resume;
    private MatchResult matchResult;
    private int rank;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private double skillMatchPercentage;
    private String recommendation;
    private List<String> commonPhrases;
    private boolean duplicate;
    private String duplicateReason;

    public Candidate(Resume resume, MatchResult matchResult) {
        this.resume = resume;
        this.matchResult = matchResult;
        this.matchedSkills = new ArrayList<>();
        this.missingSkills = new ArrayList<>();
        this.commonPhrases = new ArrayList<>();
        this.recommendation = determineRecommendation(matchResult != null ? matchResult.getOverallScore() : 0);
    }

    public Candidate(Resume resume,
                     MatchResult matchResult,
                     int rank,
                     List<String> matchedSkills,
                     List<String> missingSkills,
                     double skillMatchPercentage,
                     String recommendation,
                     List<String> commonPhrases) {
        this.resume = resume;
        this.matchResult = matchResult;
        this.rank = rank;
        this.matchedSkills = matchedSkills != null ? matchedSkills : new ArrayList<>();
        this.missingSkills = missingSkills != null ? missingSkills : new ArrayList<>();
        this.skillMatchPercentage = skillMatchPercentage;
        this.recommendation = recommendation != null ? recommendation : determineRecommendation(matchResult != null ? matchResult.getOverallScore() : 0);
        this.commonPhrases = commonPhrases != null ? commonPhrases : new ArrayList<>();
    }

    public static String determineRecommendation(double score) {
        if (score >= 80.0) {
            return "STRONG MATCH - Highly recommended. The candidate matches the majority of required skills and displays high textual and structural alignment with the job description.";
        } else if (score >= 65.0) {
            return "GOOD MATCH - Recommended for review. The candidate possesses strong foundational skills with a few minor gaps in preferred technologies.";
        } else if (score >= 50.0) {
            return "MODERATE MATCH - Partial alignment. Recommended for preliminary technical screening or alternative matching roles.";
        } else {
            return "LOW MATCH - Not recommended. Significant skill and experience gaps compared against the job requirements.";
        }
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public MatchResult getMatchResult() {
        return matchResult;
    }

    public void setMatchResult(MatchResult matchResult) {
        this.matchResult = matchResult;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public double getSkillMatchPercentage() {
        return skillMatchPercentage;
    }

    public void setSkillMatchPercentage(double skillMatchPercentage) {
        this.skillMatchPercentage = skillMatchPercentage;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public List<String> getCommonPhrases() {
        return commonPhrases;
    }

    public void setCommonPhrases(List<String> commonPhrases) {
        this.commonPhrases = commonPhrases;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public String getDuplicateReason() {
        return duplicateReason;
    }

    public void setDuplicateReason(String duplicateReason) {
        this.duplicateReason = duplicateReason;
    }
}