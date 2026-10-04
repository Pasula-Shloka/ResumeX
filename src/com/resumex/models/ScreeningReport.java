package com.resumex.models;

import java.util.List;

public class ScreeningReport {

    private JobDescription job;
    private List<Candidate> candidates;

    public ScreeningReport(
            JobDescription job,
            List<Candidate> candidates) {

        this.job = job;
        this.candidates = candidates;
    }

    public JobDescription getJob() {
        return job;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public Candidate getTopCandidate() {

        if (candidates == null ||
                candidates.isEmpty()) {

            return null;
        }

        return candidates.get(0);
    }

    public int getCandidateCount() {

        if (candidates == null) {
            return 0;
        }

        return candidates.size();
    }
}