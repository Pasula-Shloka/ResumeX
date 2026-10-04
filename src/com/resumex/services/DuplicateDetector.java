package com.resumex.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.resumex.algorithms.Hashing;
import com.resumex.models.Resume;

public class DuplicateDetector {

    private Hashing hashing;

    public DuplicateDetector() {

        hashing = new Hashing();
    }

    public Map<String, Resume> findDuplicateMap(
            List<Resume> resumes) {

        Map<String, Resume> originalResumes =
                new HashMap<>();

        Map<String, Resume> duplicates =
                new HashMap<>();

        for (Resume resume : resumes) {

            String normalizedText =
                    hashing.normalize(
                            resume.getRawText()
                    );

            String hash =
                    hashing.generateHash(
                            normalizedText
                    );

            if (originalResumes.containsKey(hash)) {

                duplicates.put(
                        resume.getCandidateName(),
                        originalResumes.get(hash)
                );

            } else {

                originalResumes.put(
                        hash,
                        resume
                );
            }
        }

        return duplicates;
    }

    public boolean isDuplicate(
            Resume first,
            Resume second) {

        return hashing.areDuplicates(
                first.getRawText(),
                second.getRawText()
        );
    }

    public void findDuplicates(
            List<Resume> resumes) {

        System.out.println();

        System.out.println(
                "=============================================="
        );

        System.out.println(
                "           DUPLICATE DETECTION"
        );

        System.out.println(
                "=============================================="
        );

        Map<String, Resume> seen =
                new HashMap<>();

        boolean duplicateFound = false;

        for (Resume resume : resumes) {

            String normalizedText =
                    hashing.normalize(
                            resume.getRawText()
                    );

            String hash =
                    hashing.generateHash(
                            normalizedText
                    );

            if (seen.containsKey(hash)) {

                Resume original =
                        seen.get(hash);

                System.out.println(
                        "Duplicate detected:"
                );

                System.out.println(
                        "Original : " +
                        original.getCandidateName()
                );

                System.out.println(
                        "Duplicate: " +
                        resume.getCandidateName()
                );

                System.out.println();

                duplicateFound = true;

            } else {

                seen.put(hash, resume);
            }
        }

        if (!duplicateFound) {

            System.out.println(
                    "No duplicate resumes found."
            );
        }

        System.out.println(
                "=============================================="
        );
    }
}