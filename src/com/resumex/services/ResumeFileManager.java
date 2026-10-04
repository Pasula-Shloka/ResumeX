package com.resumex.services;

import java.io.File;

public class ResumeFileManager {

    public boolean isValidResumeFile(
            String filePath) {

        if (filePath == null ||
                filePath.trim().isEmpty()) {

            return false;
        }

        File file =
                new File(filePath);

        if (!file.exists()) {
            return false;
        }

        if (!file.isFile()) {
            return false;
        }

        String name =
                file.getName().toLowerCase();

        return name.endsWith(".pdf");
    }

    public long getFileSize(
            String filePath) {

        File file =
                new File(filePath);

        if (!file.exists()) {
            return 0;
        }

        return file.length();
    }

    public String getFileName(
            String filePath) {

        File file =
                new File(filePath);

        return file.getName();
    }
}