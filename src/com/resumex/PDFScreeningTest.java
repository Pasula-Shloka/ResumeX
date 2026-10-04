package com.resumex;

import java.util.Arrays;
import java.util.List;

import com.resumex.models.Candidate;
import com.resumex.models.JobDescription;
import com.resumex.models.Resume;
import com.resumex.models.ScreeningReport;
import com.resumex.services.ResumeProcessingService;
import com.resumex.services.ResumeScreeningService;

public class PDFScreeningTest {

    public static void main(String[] args) {

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "                 RESUMEX"
        );

        System.out.println(
                "          AI RESUME SCREENING TEST"
        );

        System.out.println(
                "=================================================="
        );

        /*
         * STEP 1:
         * Load all PDF resumes.
         *
         * IMPORTANT:
         * Use the SAME folder path that worked
         * in ResumeProcessingTest.java.
         */
        String folderPath =
 
                "/Users/pasulashlokareddy/eclipse-workspace/ResumeX/resumes";

        ResumeProcessingService processingService =
                new ResumeProcessingService();

        List<Resume> resumes =
                processingService.processFolder(
                        folderPath
                );

        /*
         * STEP 2:
         * Check whether resumes were loaded.
         */
        if (resumes.isEmpty()) {

            System.out.println(
                    "No resumes found."
            );

            return;
        }

        /*
         * STEP 3:
         * Create the Job Description.
         */
        JobDescription job =
                new JobDescription(
                        1,
                        "Java Backend Developer",

                        "We are looking for a Java Backend Developer "
                        + "with strong knowledge of Java, Spring Boot, "
                        + "REST API, SQL, MySQL, Git, Docker, "
                        + "Data Structures and Algorithms.",

                        Arrays.asList(
                                "Java",
                                "Spring Boot",
                                "REST API",
                                "SQL",
                                "MySQL",
                                "Git",
                                "Docker",
                                "Data Structures",
                                "Algorithms"
                        )
                );

        /*
         * STEP 4:
         * Screen all resumes.
         */
        ResumeScreeningService screeningService =
                new ResumeScreeningService();

        ScreeningReport report =
                screeningService.screenResumes(
                        resumes,
                        job
                );

        /*
         * STEP 5:
         * Display candidate ranking.
         */
        System.out.println();

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "              CANDIDATE RANKING"
        );

        System.out.println(
                "=================================================="
        );

        for (Candidate candidate :
                report.getCandidates()) {

            System.out.printf(
                    "#%d  %-20s  %.2f%%%n",

                    candidate.getRank(),

                    candidate
                            .getResume()
                            .getCandidateName(),

                    candidate
                            .getMatchResult()
                            .getOverallScore()
            );
        }

        /*
         * STEP 6:
         * Display detailed DSA scores.
         */
        System.out.println();

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "             ALGORITHM SCORE BREAKDOWN"
        );

        System.out.println(
                "=================================================="
        );

        for (Candidate candidate :
                report.getCandidates()) {

            System.out.println();

            System.out.println(
                    "Candidate: " +
                    candidate
                            .getResume()
                            .getCandidateName()
            );

            System.out.println(
                    "Rank: " +
                    candidate.getRank()
            );

            candidate
                    .getMatchResult()
                    .printBreakdown();
        }

        System.out.println();

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "              SCREENING COMPLETE"
        );

        System.out.println(
                "=================================================="
        );
    }
}