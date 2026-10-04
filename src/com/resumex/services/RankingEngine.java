package com.resumex.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.resumex.models.Candidate;

public class RankingEngine {

    public List<Candidate> rank(
            List<Candidate> candidates) {

        if (candidates == null ||
                candidates.isEmpty()) {

            return new ArrayList<Candidate>();
        }

        List<Candidate> rankedCandidates =
                new ArrayList<Candidate>(candidates);

        rankedCandidates.sort(
                Comparator.comparingDouble(
                        (Candidate candidate) ->
                                candidate
                                        .getMatchResult()
                                        .getOverallScore()
                ).reversed()
        );

        for (int i = 0;
                i < rankedCandidates.size();
                i++) {

            rankedCandidates
                    .get(i)
                    .setRank(i + 1);
        }

        return rankedCandidates;
    }

    public void printRanking(
            List<Candidate> candidates) {

        System.out.println();

        System.out.println(
                "=============================================="
        );

        System.out.println(
                "              RESUMEX RANKING"
        );

        System.out.println(
                "=============================================="
        );

        System.out.printf(
                "%-6s %-25s %-12s%n",
                "Rank",
                "Candidate",
                "Score"
        );

        System.out.println(
                "----------------------------------------------"
        );

        for (Candidate candidate : candidates) {

            System.out.printf(
                    "%-6d %-25s %.2f%%%n",
                    candidate.getRank(),
                    candidate
                            .getResume()
                            .getCandidateName(),
                    candidate
                            .getMatchResult()
                            .getOverallScore()
            );
        }

        System.out.println(
                "=============================================="
        );
    }
}