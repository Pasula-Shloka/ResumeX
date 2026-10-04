package com.resumex.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.resumex.models.Resume;

public class ResumeParser {

    private SkillExtractor skillExtractor;

    public ResumeParser() {

        skillExtractor =
                new SkillExtractor();
    }

    public Resume parseTextFile(
            String filePath,
            int id)
            throws IOException {

        Path path =
                Path.of(filePath);

        String text =
                Files.readString(path);

        String name =
                extractName(text);

        String email =
                extractEmail(text);

        String phone =
                extractPhone(text);

        List<String> skills =
                skillExtractor.extractSkills(text);

        return new Resume(
                id,
                name,
                email,
                phone,
                text,
                skills
        );
    }

    private String extractName(String text) {

        if (text == null ||
                text.trim().isEmpty()) {

            return "Unknown Candidate";
        }

        String[] lines =
                text.split("\\R");

        for (String line : lines) {

            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }

            if (line.contains("@")) {
                continue;
            }

            if (line.matches(".*\\d.*")) {
                continue;
            }

            if (line.length() > 50) {
                continue;
            }

            return line;
        }

        return "Unknown Candidate";
    }

    private String extractEmail(String text) {

        Pattern pattern =
                Pattern.compile(
                        "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
                );

        Matcher matcher =
                pattern.matcher(text);

        if (matcher.find()) {

            return matcher.group();
        }

        return "Not Found";
    }

    private String extractPhone(String text) {

        Pattern pattern =
                Pattern.compile(
                        "(\\+91[-\\s]?)?[6-9]\\d{9}"
                );

        Matcher matcher =
                pattern.matcher(text);

        if (matcher.find()) {

            return matcher.group();
        }

        return "Not Found";
    }
}