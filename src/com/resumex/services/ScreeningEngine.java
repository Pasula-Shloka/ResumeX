package com.resumex.services;

import java.util.ArrayList;
import java.util.List;

import com.resumex.algorithms.ApproximateMatcher;
import com.resumex.algorithms.EditDistance;
import com.resumex.algorithms.KMPMatcher;
import com.resumex.algorithms.LCPArray;
import com.resumex.algorithms.SequenceAlignment;
import com.resumex.algorithms.SuffixArray;
import com.resumex.models.Candidate;
import com.resumex.models.JobDescription;
import com.resumex.models.MatchResult;
import com.resumex.models.Resume;

public class ScreeningEngine {

    private final KMPMatcher kmpMatcher;
    private final EditDistance editDistance;
    private final SequenceAlignment sequenceAlignment;
    private final SuffixArray suffixArray;
    private final LCPArray lcpArray;
    private final ApproximateMatcher approximateMatcher;
    private final SkillExtractor skillExtractor;

    public ScreeningEngine() {
        kmpMatcher = new KMPMatcher();
        editDistance = new EditDistance();
        sequenceAlignment = new SequenceAlignment();
        suffixArray = new SuffixArray();
        lcpArray = new LCPArray();
        approximateMatcher = new ApproximateMatcher();
        skillExtractor = new SkillExtractor();
    }

    /*
     * ============================================================
     * MAIN SCREENING METHOD
     * ============================================================
     */
    public MatchResult screen(Resume resume, JobDescription job) {
        if (resume == null || job == null) {
            return new MatchResult(0, 0, 0, 0, 0, 0);
        }

        String resumeText = resume.getRawText() == null ? "" : resume.getRawText().toLowerCase();
        String jobText = job.getDescription() == null ? "" : job.getDescription().toLowerCase();

        /*
         * 1. Skill Matching (KMP)
         */
        double skillScore = calculateSkillScore(resume, job);

        /*
         * 2. String Matching (KMP on meaningful job description keywords)
         */
        double stringScore = calculateStringMatch(resumeText, jobText);

        /*
         * 3. Edit Distance (Levenshtein Distance on technical keywords)
         */
        double editScore = calculateEditScore(resumeText, jobText);

        /*
         * 4. Sequence Alignment (Needleman-Wunsch algorithm on skill sequences)
         */
        double alignmentScore = calculateAlignmentScore(resume, job);

        /*
         * 5. Suffix Array + Kasai's LCP Array
         */
        double suffixLcpScore = calculateSuffixLcpScore(resumeText, jobText);

        /*
         * 6. Approximate / Fuzzy Matching (Greedy Bipartite Approximation)
         */
        double approximateScore = calculateApproximateScore(resume, job);

        return new MatchResult(
                skillScore,
                stringScore,
                editScore,
                alignmentScore,
                suffixLcpScore,
                approximateScore
        );
    }

    /**
     * Creates a fully enriched Candidate object with matched skills, missing skills,
     * common phrases, recommendation, and score breakdown.
     */
    public Candidate screenCandidate(Resume resume, JobDescription job) {
        MatchResult result = screen(resume, job);
        Candidate candidate = new Candidate(resume, result);

        List<String> requiredSkills = job.getRequiredSkills() != null ? job.getRequiredSkills() : new ArrayList<>();
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        String resumeText = resume.getRawText() != null ? resume.getRawText() : "";
        List<String> resumeSkills = resume.getSkills() != null ? resume.getSkills() : new ArrayList<>();

        for (String req : requiredSkills) {
            boolean hasSkill = false;
            // Check via KMP in resume text or direct skill tag
            if (kmpMatcher.contains(resumeText, req) || skillExtractor.containsSkill(resumeText, req)) {
                hasSkill = true;
            } else {
                for (String rSkill : resumeSkills) {
                    if (rSkill.equalsIgnoreCase(req) || approximateMatcher.isSimilar(rSkill, req, 85.0)) {
                        hasSkill = true;
                        break;
                    }
                }
            }

            if (hasSkill) {
                matched.add(req);
            } else {
                missing.add(req);
            }
        }

        candidate.setMatchedSkills(matched);
        candidate.setMissingSkills(missing);
        double skillPercent = requiredSkills.isEmpty() ? 0 : ((double) matched.size() / requiredSkills.size()) * 100.0;
        candidate.setSkillMatchPercentage(Math.round(skillPercent * 100.0) / 100.0);
        candidate.setRecommendation(Candidate.determineRecommendation(result.getOverallScore()));

        // Extract common phrases using Generalized Suffix Array and LCP
        String jobText = job.getDescription() != null ? job.getDescription() : "";
        List<String> phrases = lcpArray.findCommonPhrases(resumeText, jobText, 4, 6);
        candidate.setCommonPhrases(phrases);

        return candidate;
    }

    /*
     * 1. KMP STRING MATCHING - SKILL MATCHING
     */
    private double calculateSkillScore(Resume resume, JobDescription job) {
        List<String> requiredSkills = job.getRequiredSkills();
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return 0;
        }

        String resumeText = resume.getRawText() == null ? "" : resume.getRawText();
        int matched = 0;

        for (String skill : requiredSkills) {
            if (skill == null || skill.trim().isEmpty()) {
                continue;
            }

            if (kmpMatcher.contains(resumeText, skill) || skillExtractor.containsSkill(resumeText, skill)) {
                matched++;
            }
        }

        return ((double) matched / requiredSkills.size()) * 100.0;
    }

    /*
     * 2. KMP STRING MATCHING - JOB DESCRIPTION TERMS
     */
    private double calculateStringMatch(String resumeText, String jobText) {
        if (resumeText == null || jobText == null || jobText.trim().isEmpty()) {
            return 0;
        }

        String[] words = jobText.split("\\s+");
        int totalMeaningfulWords = 0;
        int matchedWords = 0;

        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

            if (word.length() <= 2) {
                continue;
            }

            totalMeaningfulWords++;
            if (kmpMatcher.contains(resumeText, word)) {
                matchedWords++;
            }
        }

        if (totalMeaningfulWords == 0) {
            return 0;
        }

        return ((double) matchedWords / totalMeaningfulWords) * 100.0;
    }

    /*
     * 3. EDIT DISTANCE
     */
    private double calculateEditScore(String resumeText, String jobText) {
        String resumeSample = getTechnicalSample(resumeText);
        String jobSample = getTechnicalSample(jobText);

        return editDistance.similarity(resumeSample, jobSample);
    }

    /*
     * 4. SEQUENCE ALIGNMENT (Needleman-Wunsch)
     */
    private double calculateAlignmentScore(Resume resume, JobDescription job) {
        List<String> resumeSkills = resume.getSkills();
        List<String> requiredSkills = job.getRequiredSkills();

        if (resumeSkills == null || resumeSkills.isEmpty() || requiredSkills == null || requiredSkills.isEmpty()) {
            return 0;
        }

        StringBuilder resumeSequence = new StringBuilder();
        for (String skill : resumeSkills) {
            if (skill == null || skill.trim().isEmpty()) continue;
            if (resumeSequence.length() > 0) resumeSequence.append(" ");
            resumeSequence.append(skill.toLowerCase());
        }

        StringBuilder jobSequence = new StringBuilder();
        for (String skill : requiredSkills) {
            if (skill == null || skill.trim().isEmpty()) continue;
            if (jobSequence.length() > 0) jobSequence.append(" ");
            jobSequence.append(skill.toLowerCase());
        }

        if (resumeSequence.length() == 0 || jobSequence.length() == 0) {
            return 0;
        }

        int alignmentScore = sequenceAlignment.calculateScore(
                resumeSequence.toString(),
                jobSequence.toString()
        );

        int maxLength = Math.max(resumeSequence.length(), jobSequence.length());
        int maximumScore = maxLength * 2;
        int minimumScore = maxLength * -2;

        double normalized = ((double) (alignmentScore - minimumScore) / (maximumScore - minimumScore)) * 100.0;
        return Math.max(0, Math.min(100.0, normalized));
    }

    private String getTechnicalSample(String text) {
        if (text == null) return "";
        text = text.toLowerCase();
        if (text.length() <= 300) return text;
        return text.substring(0, 300);
    }

    /*
     * 5. SUFFIX ARRAY + LCP (Generalized cross-boundary analysis)
     */
    private double calculateSuffixLcpScore(String resumeText, String jobText) {
        if (resumeText == null || jobText == null || resumeText.isEmpty() || jobText.isEmpty()) {
            return 0;
        }

        String s1 = resumeText.toLowerCase();
        String s2 = jobText.toLowerCase();
        int maxLen = 1200;
        if (s1.length() > maxLen) s1 = s1.substring(0, maxLen);
        if (s2.length() > maxLen) s2 = s2.substring(0, maxLen);

        int sepIndex = s1.length();
        String combined = s1 + "#" + s2;

        int[] suffix = suffixArray.build(combined);
        int[] lcp = lcpArray.build(combined, suffix);

        int crossLcpSum = 0;
        int crossCount = 0;
        int maxLcp = 0;

        for (int i = 1; i < combined.length(); i++) {
            int len = lcp[i];
            if (len >= 3) {
                int sa1 = suffix[i];
                int sa2 = suffix[i - 1];
                boolean cross = (sa1 < sepIndex && sa2 > sepIndex) || (sa1 > sepIndex && sa2 < sepIndex);
                if (cross) {
                    crossLcpSum += len;
                    crossCount++;
                    if (len > maxLcp) maxLcp = len;
                }
            }
        }

        if (crossCount == 0) {
            return 0;
        }

        // Weighted metric based on max common substring and average common phrase length
        double score = (maxLcp * 2.5) + ((double) crossLcpSum / s2.length()) * 50.0;
        return Math.max(0, Math.min(100.0, score));
    }

    /*
     * 6. APPROXIMATE / FUZZY MATCHING (Greedy Bipartite Approximation)
     */
    private double calculateApproximateScore(Resume resume, JobDescription job) {
        List<String> resumeSkills = resume.getSkills();
        List<String> requiredSkills = job.getRequiredSkills();

        if (resumeSkills == null || resumeSkills.isEmpty() || requiredSkills == null || requiredSkills.isEmpty()) {
            return 0;
        }

        return approximateMatcher.calculateGreedyMatchScore(resumeSkills, requiredSkills);
    }
}
