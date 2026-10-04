package com.resumex.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.resumex.models.Candidate;
import com.resumex.models.JobDescription;
import com.resumex.models.Resume;
import com.resumex.models.ScreeningReport;

public class ResumeScreeningService {

    private final ScreeningEngine screeningEngine;
    private final RankingEngine rankingEngine;
    private final DuplicateDetector duplicateDetector;

    public ResumeScreeningService() {
        screeningEngine = new ScreeningEngine();
        rankingEngine = new RankingEngine();
        duplicateDetector = new DuplicateDetector();
    }

    public ScreeningReport screenResumes(List<Resume> resumes, JobDescription job) {
        List<Candidate> candidates = new ArrayList<>();

        if (resumes == null || job == null) {
            return new ScreeningReport(job, candidates);
        }

        // Map duplicates via SHA-256 Hashing
        Map<String, Resume> duplicateMap = duplicateDetector.findDuplicateMap(resumes);

        for (Resume resume : resumes) {
            Candidate candidate = screeningEngine.screenCandidate(resume, job);

            if (duplicateMap.containsKey(resume.getCandidateName())) {
                Resume original = duplicateMap.get(resume.getCandidateName());
                candidate.setDuplicate(true);
                candidate.setDuplicateReason("Exact SHA-256 duplicate of " + original.getCandidateName() + " (" + original.getFileName() + ")");
            }

            candidates.add(candidate);
        }

        List<Candidate> ranked = rankingEngine.rank(candidates);
        return new ScreeningReport(job, ranked);
    }

    public ScreeningEngine getScreeningEngine() {
        return screeningEngine;
    }

    public RankingEngine getRankingEngine() {
        return rankingEngine;
    }
}