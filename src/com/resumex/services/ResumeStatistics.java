package com.resumex.services;

import java.util.List;

import com.resumex.models.Resume;

public class ResumeStatistics {

    public int countResumes(
            List<Resume> resumes) {

        if (resumes == null) {
            return 0;
        }

        return resumes.size();
    }

    public int countResumesWithSkills(
            List<Resume> resumes) {

        if (resumes == null) {
            return 0;
        }

        int count = 0;

        for (Resume resume : resumes) {

            if (resume.getSkills() != null &&
                    !resume.getSkills().isEmpty()) {

                count++;
            }
        }

        return count;
    }

    public double averageSkillCount(
            List<Resume> resumes) {

        if (resumes == null ||
                resumes.isEmpty()) {

            return 0;
        }

        int totalSkills = 0;

        for (Resume resume : resumes) {

            if (resume.getSkills() != null) {

                totalSkills +=
                        resume.getSkills().size();
            }
        }

        return (double) totalSkills /
                resumes.size();
    }
}