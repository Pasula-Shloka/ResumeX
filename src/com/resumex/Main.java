package com.resumex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.resumex.models.Candidate;
import com.resumex.models.JobDescription;
import com.resumex.models.MatchResult;
import com.resumex.models.Resume;

import com.resumex.services.DuplicateDetector;
import com.resumex.services.RankingEngine;
import com.resumex.services.ScreeningEngine;

public class Main {

    public static void main(String[] args) {

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "                 RESUMEX"
        );

        System.out.println(
                "        INTELLIGENT RESUME SCREENING"
        );

        System.out.println(
                "=================================================="
        );


        /*
         * ==============================================
         * 1. CREATE JOB DESCRIPTION
         * ==============================================
         */

        JobDescription job =
                new JobDescription(
                        101,

                        "Java Backend Developer",

                        "Looking for a Java backend developer " +
                        "with experience in Java Spring Boot " +
                        "MySQL REST API Git and Docker. " +
                        "Candidate should understand data " +
                        "structures algorithms and backend development.",

                        Arrays.asList(
                                "Java",
                                "Spring Boot",
                                "MySQL",
                                "REST API",
                                "Git",
                                "Docker",
                                "Data Structures",
                                "Algorithms"
                        )
                );


        /*
         * ==============================================
         * 2. CREATE SAMPLE RESUMES
         * ==============================================
         */

        Resume resume1 =
                new Resume(

                        1,

                        "Ananya Sharma",

                        "ananya@example.com",

                        "9876543210",

                        "Java backend developer with strong " +
                        "experience in Java Spring Boot MySQL " +
                        "REST API Git Docker Data Structures " +
                        "and Algorithms. Experienced in developing " +
                        "scalable backend applications.",

                        Arrays.asList(
                                "Java",
                                "Spring Boot",
                                "MySQL",
                                "REST API",
                                "Git",
                                "Docker",
                                "Data Structures",
                                "Algorithms"
                        )
                );


        Resume resume2 =
                new Resume(

                        2,

                        "Rahul Verma",

                        "rahul@example.com",

                        "9876543211",

                        "Software developer experienced in Java " +
                        "Spring Boot and MySQL. Worked with REST " +
                        "services and Git. Familiar with algorithms " +
                        "and backend development.",

                        Arrays.asList(
                                "Java",
                                "Spring Boot",
                                "MySQL",
                                "REST API",
                                "Git",
                                "Algorithms"
                        )
                );


        Resume resume3 =
                new Resume(

                        3,

                        "Priya Reddy",

                        "priya@example.com",

                        "9876543212",

                        "Python developer with experience in Django " +
                        "PostgreSQL machine learning data analysis " +
                        "and basic web development.",

                        Arrays.asList(
                                "Python",
                                "Django",
                                "PostgreSQL",
                                "Machine Learning"
                        )
                );


        /*
         * ==============================================
         * 3. STORE RESUMES
         * ==============================================
         */

        List<Resume> resumes =
                new ArrayList<>();

        resumes.add(resume1);
        resumes.add(resume2);
        resumes.add(resume3);


        /*
         * ==============================================
         * 4. CREATE SCREENING ENGINE
         * ==============================================
         */

        ScreeningEngine screeningEngine =
                new ScreeningEngine();


        /*
         * ==============================================
         * 5. SCREEN EVERY RESUME
         * ==============================================
         */

        List<Candidate> candidates =
                new ArrayList<>();

        System.out.println();

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "              SCREENING RESUMES"
        );

        System.out.println(
                "=================================================="
        );

        for (Resume resume : resumes) {

            System.out.println();

            System.out.println(
                    "Processing: " +
                    resume.getCandidateName()
            );

            MatchResult result =
                    screeningEngine.screen(
                            resume,
                            job
                    );

            Candidate candidate =
                    new Candidate(
                            resume,
                            result
                    );

            candidates.add(candidate);

            System.out.println(
                    "Screening completed."
            );
        }


        /*
         * ==============================================
         * 6. RANK CANDIDATES
         * ==============================================
         */

        RankingEngine rankingEngine =
                new RankingEngine();

        List<Candidate> rankedCandidates =
                rankingEngine.rank(
                        candidates
                );

        rankingEngine.printRanking(
                rankedCandidates
        );


        /*
         * ==============================================
         * 7. DISPLAY DETAILED RESULTS
         * ==============================================
         */

        System.out.println();

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "          CANDIDATE SCORE BREAKDOWN"
        );

        System.out.println(
                "=================================================="
        );

        for (Candidate candidate :
                rankedCandidates) {

            System.out.println();

            System.out.println(
                    "Rank " +
                    candidate.getRank() +
                    " - " +
                    candidate
                            .getResume()
                            .getCandidateName()
            );

            candidate
                    .getMatchResult()
                    .printBreakdown();
        }


        /*
         * ==============================================
         * 8. DUPLICATE DETECTION
         * ==============================================
         */

        DuplicateDetector duplicateDetector =
                new DuplicateDetector();

        duplicateDetector.findDuplicates(
                resumes
        );


        /*
         * ==============================================
         * 9. FINISHED
         * ==============================================
         */

        System.out.println();

        System.out.println(
                "=================================================="
        );

        System.out.println(
                "           RESUMEX SCREENING COMPLETE"
        );

        System.out.println(
                "=================================================="
        );
    }
}