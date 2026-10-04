package com.resumex.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.resumex.models.Resume;

public class ResumeFolderScanner {

    private PdfResumeParser pdfResumeParser;

    public ResumeFolderScanner() {

        pdfResumeParser =
                new PdfResumeParser();
    }

    public List<Resume> scanFolder(
            String folderPath) {

        List<Resume> resumes =
                new ArrayList<>();

        File folder =
                new File(folderPath);

        if (!folder.exists()) {

            System.out.println(
                    "Resume folder does not exist."
            );

            return resumes;
        }

        if (!folder.isDirectory()) {

            System.out.println(
                    "Provided path is not a folder."
            );

            return resumes;
        }

        File[] files =
                folder.listFiles();

        if (files == null) {
            return resumes;
        }

        int id = 1;

        for (File file : files) {

            if (!file.isFile()) {
                continue;
            }

            String fileName =
                    file.getName().toLowerCase();

            if (!fileName.endsWith(".pdf")) {
                continue;
            }

            try {

                Resume resume =
                        pdfResumeParser.parsePDF(
                                file.getAbsolutePath(),
                                id
                        );

                resumes.add(resume);

                System.out.println(
                        "Loaded: " +
                        file.getName()
                );

                id++;

            } catch (Exception e) {

                System.out.println(
                        "Could not process: " +
                        file.getName()
                );

                System.out.println(
                        e.getMessage()
                );
            }
        }

        return resumes;
    }
}