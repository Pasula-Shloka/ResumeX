package com.resumex.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.resumex.models.Resume;

public class PdfResumeParser {

    private final SkillExtractor skillExtractor;

    public PdfResumeParser() {
        skillExtractor = new SkillExtractor();
    }

    public Resume parsePDF(String filePath, int id) throws IOException {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new IOException("PDF file not found: " + filePath);
        }

        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            String name = extractName(text);
            if ("Unknown Candidate".equals(name)) {
                name = extractNameFromFilename(file.getName());
            }

            String email = extractEmail(text);
            String phone = extractPhone(text);
            List<String> skills = skillExtractor.extractSkills(text);

            return new Resume(id, name, email, phone, text, skills, file.getName());
        }
    }

    public Resume parsePDF(byte[] pdfBytes, String fileName, int id) throws IOException {
        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new IOException("PDF file buffer is empty.");
        }

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            String name = extractName(text);
            if ("Unknown Candidate".equals(name) && fileName != null) {
                name = extractNameFromFilename(fileName);
            }

            String email = extractEmail(text);
            String phone = extractPhone(text);
            List<String> skills = skillExtractor.extractSkills(text);

            return new Resume(id, name, email, phone, text, skills, fileName);
        }
    }

    public String extractTextFromPDF(byte[] pdfBytes) throws IOException {
        if (pdfBytes == null || pdfBytes.length == 0) {
            return "";
        }
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractNameFromFilename(String fileName) {
        if (fileName == null) {
            return "Unknown Candidate";
        }
        String clean = fileName
                .replace(".pdf", "")
                .replace(".PDF", "")
                .replaceAll("\\s*\\(\\d+\\)", "")
                .replace("_", " ")
                .replace("-", " ")
                .trim();
        return clean.isEmpty() ? "Unknown Candidate" : clean;
    }

    private String extractName(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "Unknown Candidate";
        }

        String[] lines = text.split("\\R");

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

            if (line.toLowerCase().contains("curriculum") || line.toLowerCase().contains("resume") || line.toLowerCase().contains("profile")) {
                continue;
            }

            if (line.length() > 50) {
                continue;
            }

            // Looks like a valid candidate name (e.g. 2 to 4 alphabetic words)
            if (line.matches("^[A-Za-z]+([ .'-][A-Za-z]+)*$")) {
                return line;
            }
        }

        return "Unknown Candidate";
    }

    private String extractEmail(String text) {
        if (text == null) {
            return "Not Found";
        }
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group();
        }

        return "Not Found";
    }

    private String extractPhone(String text) {
        if (text == null) {
            return "Not Found";
        }
        Pattern pattern = Pattern.compile("(\\+91[-\\s]?)?[6-9]\\d{9}");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group();
        }

        return "Not Found";
    }

    public SkillExtractor getSkillExtractor() {
        return skillExtractor;
    }
}