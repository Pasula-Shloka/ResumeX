package com.resumex.services;

import java.util.ArrayList;
import java.util.List;

import com.resumex.models.Candidate;

public class CandidateSearchService {

    public List<Candidate> searchByName(
            List<Candidate> candidates,
            String keyword) {

        List<Candidate> results =
                new ArrayList<>();

        if (candidates == null ||
                keyword == null) {

            return results;
        }

        keyword =
                keyword.toLowerCase();

        for (Candidate candidate :
                candidates) {

            String name =
                    candidate
                            .getResume()
                            .getCandidateName()
                            .toLowerCase();

            if (name.contains(keyword)) {

                results.add(candidate);
            }
        }

        return results;
    }

    public List<Candidate> filterByScore(
            List<Candidate> candidates,
            double minimumScore) {

        List<Candidate> results =
                new ArrayList<>();

        if (candidates == null) {
            return results;
        }

        for (Candidate candidate :
                candidates) {

            if (candidate
                    .getMatchResult()
                    .getOverallScore()
                    >= minimumScore) {

                results.add(candidate);
            }
        }

        return results;
    }
}