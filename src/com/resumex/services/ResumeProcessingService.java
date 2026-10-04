package com.resumex.services;

import java.util.List;

import com.resumex.models.Resume;

public class ResumeProcessingService {

    private ResumeFolderScanner folderScanner;
    private ResumeStatistics statistics;

    public ResumeProcessingService() {

        folderScanner =
                new ResumeFolderScanner();

        statistics =
                new ResumeStatistics();
    }

    public List<Resume> processFolder(
            String folderPath) {

        System.out.println();

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "        RESUMEX RESUME PROCESSING"
        );

        System.out.println(
                "=========================================="
        );

        List<Resume> resumes =
                folderScanner.scanFolder(
                        folderPath
                );

        System.out.println();

        System.out.println(
                "Total resumes loaded: " +
                statistics.countResumes(resumes)
        );

        System.out.println(
                "Resumes with detected skills: " +
                statistics.countResumesWithSkills(
                        resumes
                )
        );

        System.out.printf(
                "Average skills per resume: %.2f%n",
                statistics.averageSkillCount(
                        resumes
                )
        );

        System.out.println(
                "=========================================="
        );

        return resumes;
    }

    public void printCandidateInformation(
            List<Resume> resumes) {

        System.out.println();

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "       EXTRACTED CANDIDATE DATA"
        );

        System.out.println(
                "=========================================="
        );

        for (Resume resume : resumes) {

            System.out.println();

            System.out.println(
                    "Name  : " +
                    resume.getCandidateName()
            );

            System.out.println(
                    "Email : " +
                    resume.getEmail()
            );

            System.out.println(
                    "Phone : " +
                    resume.getPhone()
            );

            System.out.println(
                    "Skills: " +
                    resume.getSkills()
            );

            System.out.println(
                    "------------------------------------------"
            );
        }
    }
}