package com.resumex.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

/**
 * Utility to generate a professional, publication-quality PDF document
 * for the ResumeX College DSA Project directly from Markdown / Structured Content.
 */
public class GenerateProjectPdf {

    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();   // 595.27
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight(); // 841.89
    private static final float MARGIN_LEFT = 45f;
    private static final float MARGIN_RIGHT = 45f;
    private static final float MARGIN_TOP = 50f;
    private static final float MARGIN_BOTTOM = 55f;
    private static final float USABLE_WIDTH = PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT; // 505.27

    private final PDDocument document;
    private final PDFont fontBold;
    private final PDFont fontRegular;
    private final PDFont fontItalic;
    private final PDFont fontCode;
    private final PDFont fontCodeBold;

    private PDPage currentPage;
    private PDPageContentStream cs;
    private float currentY;
    private int pageNumber;
    private final List<PDPage> pagesList;

    public GenerateProjectPdf() {
        this.document = new PDDocument();
        this.fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        this.fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        this.fontItalic = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);
        this.fontCode = new PDType1Font(Standard14Fonts.FontName.COURIER);
        this.fontCodeBold = new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD);
        this.pagesList = new ArrayList<>();
        this.pageNumber = 0;
    }

    private void newPage() throws IOException {
        if (cs != null) {
            cs.close();
        }
        currentPage = new PDPage(PDRectangle.A4);
        document.addPage(currentPage);
        pagesList.add(currentPage);
        pageNumber++;
        cs = new PDPageContentStream(document, currentPage);
        currentY = PAGE_HEIGHT - MARGIN_TOP;

        // Draw running header if not page 1
        if (pageNumber > 1) {
            cs.beginText();
            cs.setFont(fontRegular, 8.5f);
            cs.setNonStrokingColor(100/255f, 116/255f, 139/255f); // slate-500
            cs.newLineAtOffset(MARGIN_LEFT, PAGE_HEIGHT - 32f);
            cs.showText("ResumeX - AI Resume Screening & Intelligent Candidate Ranking System");
            cs.endText();

            // Header line
            cs.setStrokingColor(226/255f, 232/255f, 240/255f); // slate-200
            cs.setLineWidth(0.6f);
            cs.moveTo(MARGIN_LEFT, PAGE_HEIGHT - 36f);
            cs.lineTo(PAGE_WIDTH - MARGIN_RIGHT, PAGE_HEIGHT - 36f);
            cs.stroke();
        }
    }

    private void ensureSpace(float requiredHeight) throws IOException {
        if (currentY - requiredHeight < MARGIN_BOTTOM) {
            newPage();
        }
    }

    public void addCoverHeader(String title, String subtitle, String authorInfo) throws IOException {
        ensureSpace(120);

        // Decorative top pill badge
        cs.setNonStrokingColor(238/255f, 242/255f, 255/255f); // indigo-50
        cs.addRect(MARGIN_LEFT, currentY - 18, 175, 20);
        cs.fill();
        cs.beginText();
        cs.setFont(fontBold, 8.5f);
        cs.setNonStrokingColor(79/255f, 70/255f, 229/255f); // indigo-600
        cs.newLineAtOffset(MARGIN_LEFT + 8, currentY - 14);
        cs.showText("COLLEGE DSA PROJECT REPORT");
        cs.endText();
        currentY -= 30;

        // Title
        cs.beginText();
        cs.setFont(fontBold, 20f);
        cs.setNonStrokingColor(15/255f, 23/255f, 42/255f); // slate-900
        cs.newLineAtOffset(MARGIN_LEFT, currentY);
        cs.showText(sanitize(title));
        cs.endText();
        currentY -= 24;

        // Subtitle
        cs.beginText();
        cs.setFont(fontBold, 11f);
        cs.setNonStrokingColor(79/255f, 70/255f, 229/255f); // indigo-600
        cs.newLineAtOffset(MARGIN_LEFT, currentY);
        cs.showText(sanitize(subtitle));
        cs.endText();
        currentY -= 16;

        // Author & Metadata
        cs.beginText();
        cs.setFont(fontRegular, 9.5f);
        cs.setNonStrokingColor(100/255f, 116/255f, 139/255f); // slate-500
        cs.newLineAtOffset(MARGIN_LEFT, currentY);
        cs.showText(sanitize(authorInfo));
        cs.endText();
        currentY -= 12;

        // Divider
        cs.setStrokingColor(79/255f, 70/255f, 229/255f);
        cs.setLineWidth(1.5f);
        cs.moveTo(MARGIN_LEFT, currentY);
        cs.lineTo(PAGE_WIDTH - MARGIN_RIGHT, currentY);
        cs.stroke();
        currentY -= 20;
    }

    public void addHeading1(String text) throws IOException {
        ensureSpace(45);
        currentY -= 10;
        cs.beginText();
        cs.setFont(fontBold, 13.5f);
        cs.setNonStrokingColor(15/255f, 23/255f, 42/255f);
        cs.newLineAtOffset(MARGIN_LEFT, currentY);
        cs.showText(sanitize(text));
        cs.endText();
        currentY -= 6;

        // Underline
        cs.setStrokingColor(203/255f, 213/255f, 225/255f); // slate-300
        cs.setLineWidth(0.8f);
        cs.moveTo(MARGIN_LEFT, currentY);
        cs.lineTo(PAGE_WIDTH - MARGIN_RIGHT, currentY);
        cs.stroke();
        currentY -= 14;
    }

    public void addHeading2(String text) throws IOException {
        ensureSpace(35);
        currentY -= 6;
        cs.beginText();
        cs.setFont(fontBold, 11f);
        cs.setNonStrokingColor(51/255f, 65/255f, 85/255f); // slate-700
        cs.newLineAtOffset(MARGIN_LEFT, currentY);
        cs.showText(sanitize(text));
        cs.endText();
        currentY -= 14;
    }

    public void addParagraph(String text) throws IOException {
        List<String> lines = wrapText(text, USABLE_WIDTH, fontRegular, 9.5f);
        ensureSpace(lines.size() * 13f + 8f);

        cs.beginText();
        cs.setFont(fontRegular, 9.5f);
        cs.setNonStrokingColor(30/255f, 41/255f, 59/255f); // slate-800
        cs.newLineAtOffset(MARGIN_LEFT, currentY);
        for (int i = 0; i < lines.size(); i++) {
            cs.showText(sanitize(lines.get(i)));
            if (i < lines.size() - 1) {
                cs.newLineAtOffset(0, -13f);
            }
        }
        cs.endText();
        currentY -= (lines.size() * 13f + 8f);
    }

    public void addBulletPoint(String title, String body) throws IOException {
        String fullText = (title != null && !title.isEmpty() ? title + ": " : "") + body;
        List<String> lines = wrapText(fullText, USABLE_WIDTH - 16f, fontRegular, 9f);
        ensureSpace(lines.size() * 12.5f + 4f);

        // Bullet dot
        cs.beginText();
        cs.setFont(fontBold, 9f);
        cs.setNonStrokingColor(79/255f, 70/255f, 229/255f);
        cs.newLineAtOffset(MARGIN_LEFT + 2, currentY);
        cs.showText("-");
        cs.endText();

        // Content
        cs.beginText();
        cs.setFont(fontRegular, 9f);
        cs.setNonStrokingColor(30/255f, 41/255f, 59/255f);
        cs.newLineAtOffset(MARGIN_LEFT + 14, currentY);
        for (int i = 0; i < lines.size(); i++) {
            cs.showText(sanitize(lines.get(i)));
            if (i < lines.size() - 1) {
                cs.newLineAtOffset(0, -12.5f);
            }
        }
        cs.endText();
        currentY -= (lines.size() * 12.5f + 4f);
    }

    public void addCalloutBox(String title, String message) throws IOException {
        List<String> lines = wrapText(message, USABLE_WIDTH - 24f, fontRegular, 9f);
        float boxHeight = lines.size() * 12.5f + 26f;
        ensureSpace(boxHeight + 8f);

        // Box background
        cs.setNonStrokingColor(245/255f, 243/255f, 255/255f); // violet-50
        cs.addRect(MARGIN_LEFT, currentY - boxHeight + 4, USABLE_WIDTH, boxHeight);
        cs.fill();

        // Left accent line
        cs.setStrokingColor(79/255f, 70/255f, 229/255f);
        cs.setLineWidth(3f);
        cs.moveTo(MARGIN_LEFT, currentY + 4);
        cs.lineTo(MARGIN_LEFT, currentY - boxHeight + 4);
        cs.stroke();

        // Title
        cs.beginText();
        cs.setFont(fontBold, 9.5f);
        cs.setNonStrokingColor(67/255f, 56/255f, 202/255f);
        cs.newLineAtOffset(MARGIN_LEFT + 12, currentY - 8);
        cs.showText(sanitize(title));
        cs.endText();

        // Body
        cs.beginText();
        cs.setFont(fontRegular, 9f);
        cs.setNonStrokingColor(30/255f, 41/255f, 59/255f);
        cs.newLineAtOffset(MARGIN_LEFT + 12, currentY - 22);
        for (int i = 0; i < lines.size(); i++) {
            cs.showText(sanitize(lines.get(i)));
            if (i < lines.size() - 1) {
                cs.newLineAtOffset(0, -12.5f);
            }
        }
        cs.endText();
        currentY -= (boxHeight + 10f);
    }

    public void addCodeSnippet(String[] lines) throws IOException {
        float boxHeight = lines.length * 11.5f + 12f;
        ensureSpace(boxHeight + 6f);

        // Box background
        cs.setNonStrokingColor(248/255f, 250/255f, 252/255f); // slate-50
        cs.addRect(MARGIN_LEFT, currentY - boxHeight + 4, USABLE_WIDTH, boxHeight);
        cs.fill();

        // Border
        cs.setStrokingColor(226/255f, 232/255f, 240/255f); // slate-200
        cs.setLineWidth(0.6f);
        cs.addRect(MARGIN_LEFT, currentY - boxHeight + 4, USABLE_WIDTH, boxHeight);
        cs.stroke();

        // Text
        cs.beginText();
        cs.setFont(fontCode, 8f);
        cs.setNonStrokingColor(30/255f, 41/255f, 59/255f);
        cs.newLineAtOffset(MARGIN_LEFT + 8, currentY - 6);
        for (int i = 0; i < lines.length; i++) {
            cs.showText(sanitize(lines[i]));
            if (i < lines.length - 1) {
                cs.newLineAtOffset(0, -11.5f);
            }
        }
        cs.endText();
        currentY -= (boxHeight + 8f);
    }

    public void addTable(String[] headers, List<String[]> rows, float[] colWidths) throws IOException {
        float rowHeight = 18f;
        float totalHeight = (rows.size() + 1) * rowHeight;
        ensureSpace(totalHeight + 10f);

        // Draw header row background
        cs.setNonStrokingColor(241/255f, 245/255f, 249/255f); // slate-100
        cs.addRect(MARGIN_LEFT, currentY - rowHeight + 3, USABLE_WIDTH, rowHeight);
        cs.fill();

        // Draw header text
        float x = MARGIN_LEFT;
        for (int c = 0; c < headers.length; c++) {
            cs.beginText();
            cs.setFont(fontBold, 8.5f);
            cs.setNonStrokingColor(15/255f, 23/255f, 42/255f);
            cs.newLineAtOffset(x + 5, currentY - 9);
            cs.showText(sanitize(truncateText(headers[c], colWidths[c] - 10, fontBold, 8.5f)));
            cs.endText();
            x += colWidths[c];
        }

        // Draw header bottom border
        cs.setStrokingColor(203/255f, 213/255f, 225/255f);
        cs.setLineWidth(0.8f);
        cs.moveTo(MARGIN_LEFT, currentY - rowHeight + 3);
        cs.lineTo(MARGIN_LEFT + USABLE_WIDTH, currentY - rowHeight + 3);
        cs.stroke();

        currentY -= rowHeight;

        // Draw rows
        for (int r = 0; r < rows.size(); r++) {
            String[] row = rows.get(r);
            if (r % 2 == 1) {
                cs.setNonStrokingColor(248/255f, 250/255f, 252/255f);
                cs.addRect(MARGIN_LEFT, currentY - rowHeight + 3, USABLE_WIDTH, rowHeight);
                cs.fill();
            }

            x = MARGIN_LEFT;
            for (int c = 0; c < row.length; c++) {
                String val = c < row.length ? row[c] : "";
                cs.beginText();
                cs.setFont(fontRegular, 8f);
                cs.setNonStrokingColor(30/255f, 41/255f, 59/255f);
                cs.newLineAtOffset(x + 5, currentY - 9);
                cs.showText(sanitize(truncateText(val, colWidths[c] - 10, fontRegular, 8f)));
                cs.endText();
                x += colWidths[c];
            }

            // Row bottom line
            cs.setStrokingColor(241/255f, 245/255f, 249/255f);
            cs.setLineWidth(0.5f);
            cs.moveTo(MARGIN_LEFT, currentY - rowHeight + 3);
            cs.lineTo(MARGIN_LEFT + USABLE_WIDTH, currentY - rowHeight + 3);
            cs.stroke();

            currentY -= rowHeight;
        }

        // Outline table
        cs.setStrokingColor(203/255f, 213/255f, 225/255f);
        cs.setLineWidth(0.8f);
        cs.addRect(MARGIN_LEFT, currentY + 3, USABLE_WIDTH, totalHeight);
        cs.stroke();

        currentY -= 12f;
    }

    private void addPageNumbers() throws IOException {
        if (cs != null) {
            cs.close();
        }
        int total = document.getNumberOfPages();
        for (int i = 0; i < total; i++) {
            PDPage page = document.getPage(i);
            try (PDPageContentStream footerCs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                // Line
                footerCs.setStrokingColor(226/255f, 232/255f, 240/255f);
                footerCs.setLineWidth(0.6f);
                footerCs.moveTo(MARGIN_LEFT, 38f);
                footerCs.lineTo(PAGE_WIDTH - MARGIN_RIGHT, 38f);
                footerCs.stroke();

                // Left text
                footerCs.beginText();
                footerCs.setFont(fontRegular, 8f);
                footerCs.setNonStrokingColor(148/255f, 163/255f, 184/255f); // slate-400
                footerCs.newLineAtOffset(MARGIN_LEFT, 26f);
                footerCs.showText("ResumeX Project Report | Confidential & Educational Use");
                footerCs.endText();

                // Right page number
                String pageText = "Page " + (i + 1) + " of " + total;
                float w = fontRegular.getStringWidth(pageText) / 1000 * 8f;
                footerCs.beginText();
                footerCs.setFont(fontBold, 8f);
                footerCs.setNonStrokingColor(100/255f, 116/255f, 139/255f);
                footerCs.newLineAtOffset(PAGE_WIDTH - MARGIN_RIGHT - w, 26f);
                footerCs.showText(pageText);
                footerCs.endText();
            }
        }
    }

    public void save(File targetFile) throws IOException {
        addPageNumbers();
        document.save(targetFile);
        document.close();
    }

    // Helper: Wrap text into lines fitting target width
    private List<String> wrapText(String text, float maxWidth, PDFont font, float fontSize) throws IOException {
        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty()) return result;

        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String candidate = currentLine.length() == 0 ? word : currentLine + " " + word;
            float width = font.getStringWidth(sanitize(candidate)) / 1000 * fontSize;
            if (width > maxWidth && currentLine.length() > 0) {
                result.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(candidate);
            }
        }
        if (currentLine.length() > 0) {
            result.add(currentLine.toString());
        }
        return result;
    }

    private String truncateText(String text, float maxWidth, PDFont font, float fontSize) throws IOException {
        if (text == null) return "";
        float w = font.getStringWidth(sanitize(text)) / 1000 * fontSize;
        if (w <= maxWidth) return text;

        String curr = text;
        while (curr.length() > 3 && (font.getStringWidth(sanitize(curr + "...")) / 1000 * fontSize) > maxWidth) {
            curr = curr.substring(0, curr.length() - 1);
        }
        return curr + "...";
    }

    private static String sanitize(String text) {
        if (text == null) return "";
        return text.replace("✓", "[x]")
                   .replace("✗", "[-]")
                   .replace("⭐", "*")
                   .replace("🌟", "*")
                   .replace("🏛️", "")
                   .replace("⚡", "")
                   .replace("✨", "")
                   .replace("🚀", "")
                   .replace("📡", "")
                   .replace("📄", "")
                   .replace("📋", "")
                   .replace("🎙️", "")
                   .replace("⏳", "")
                   .replace("❌", "[x]")
                   .replace("•", "-")
                   .replace("—", "-")
                   .replace("–", "-")
                   .replace("“", "\"")
                   .replace("”", "\"")
                   .replace("’", "'")
                   .replace("‘", "'")
                   .replace("≥", ">=")
                   .replace("≤", "<=")
                   .replace("≠", "!=")
                   .replace("≈", "~")
                   .replace("→", "->")
                   .replace("←", "<-")
                   .replace("↔", "<->")
                   .replace("π", "pi")
                   .replace("∩", "intersect")
                   .replace("∪", "union")
                   .replace("sqsupset", "suffix")
                   .replaceAll("[^\\x00-\\x7F]", ""); // strip remaining non-ASCII
    }

    public static void main(String[] args) {
        try {
            System.out.println("Generating ResumeX Official Project Documentation PDF...");
            GenerateProjectPdf gen = new GenerateProjectPdf();
            gen.newPage();

            // Cover
            gen.addCoverHeader(
                "ResumeX - AI Resume Screening & Intelligent Candidate Ranking System",
                "Comprehensive System Architecture & Core DSA Algorithm Documentation",
                "Author: Pasula Shloka | Course: Data Structures & Algorithms Capstone | October 2026"
            );

            // 1. Executive Summary
            gen.addHeading1("1. Executive Summary");
            gen.addParagraph("ResumeX is an AI-powered resume screening and intelligent candidate ranking platform inspired by modern talent acquisition platforms such as LinkedIn Recruiter, Workday HCM, and Ashby ATS. The system is designed to automate resume evaluation against targeted job descriptions using verified, explainable Data Structures and Algorithms (DSA) rather than opaque black-box AI models.");
            gen.addBulletPoint("Problem Statement", "Recruiters receive hundreds of resumes per job opening. Manual screening takes 6-8 seconds per candidate, resulting in fatigue, bias, and overlooked qualifications.");
            gen.addBulletPoint("Deterministic DSA Foundation", "ResumeX implements exact string matching, dynamic programming edit distance, global sequence alignment, suffix arrays with LCP, SHA-256 hashing, and greedy approximation optimization to deliver mathematically sound, transparent rankings.");

            // 2. System Architecture
            gen.addHeading1("2. Decoupled System Architecture");
            gen.addParagraph("The architecture strictly separates the high-performance algorithmic backend from the modern recruiter user interface, maintaining modularity and independent scalability.");
            gen.addBulletPoint("Eclipse Java Backend (Port 8080)", "Pure Java SE 17+ HTTP Server (com.sun.net.httpserver) executing all 6 DSA algorithms, in-memory repository storage, and Apache PDFBox 3.0.8 PDF text extraction.");
            gen.addBulletPoint("VS Code Modern Frontend (Port 5173)", "React 19 + Vite Single Page Application designed with recruiter-centric workflows (Dashboard, Job Requisitions, PDF Screening, Candidate Directory with CSV export, Hiring Pipeline Kanban, Multi-Resume Comparator, and Duplicate Auditor).");

            // 3. Core DSA Algorithms
            gen.addHeading1("3. Core Data Structures & Algorithms Suite");
            gen.addParagraph("Every score and ranking produced by ResumeX is backed by actual algorithmic computation in the Eclipse Java project:");

            String[] algoHeaders = {"Algorithm", "DSA Category", "Primary Function", "Time Complexity"};
            List<String[]> algoRows = new ArrayList<>();
            algoRows.add(new String[]{"KMP Matcher", "String Matching", "Exact skill & keyword extraction", "O(N + M)"});
            algoRows.add(new String[]{"Levenshtein DP", "Dynamic Programming", "Typo tolerance & abbreviation matching", "O(N * M)"});
            algoRows.add(new String[]{"Needleman-Wunsch", "Sequence Alignment DP", "Qualification order & career progression", "O(N * M)"});
            algoRows.add(new String[]{"Suffix Array", "String Indexing", "Full-text substring search & indexing", "O(N log^2 N)"});
            algoRows.add(new String[]{"Kasai LCP Array", "Array Processing", "Longest common prefix for domain phrases", "O(N)"});
            algoRows.add(new String[]{"SHA-256 Hashing", "Cryptographic Hashing", "Exact duplicate resume detection", "O(N)"});
            algoRows.add(new String[]{"Greedy Approximation", "Optimization Algorithm", "Multi-criteria candidate score weighting", "O(K log K)"});
            float[] algoColWidths = {115f, 115f, 175f, 100f};
            gen.addTable(algoHeaders, algoRows, algoColWidths);

            gen.addHeading2("3.1 Algorithm 1: Knuth-Morris-Pratt (KMP) String Matching");
            gen.addParagraph("Implemented in KMPMatcher.java. Uses a precomputed prefix function (pi table) to identify exact required skills (e.g., 'Spring Boot', 'Data Structures', 'SQL') across candidate text in O(N + M) linear time without character backtracking.");

            gen.addHeading2("3.2 Algorithm 2: Levenshtein Edit Distance (Dynamic Programming)");
            gen.addParagraph("Implemented in EditDistance.java. Uses a 2D dynamic programming recurrence to quantify string distance between candidate skills and job requirements. Normalizes distance into a similarity percentage to seamlessly recognize spelling variants and typos (e.g., 'Jvaa' -> 'Java', 'ReactJS' -> 'React').");

            gen.addHeading2("3.3 Algorithm 3: Needleman-Wunsch Global Sequence Alignment");
            gen.addParagraph("Implemented in SequenceAlignment.java. Evaluates whether the candidate's career progression and listed technologies mirror the prioritization of the job description. Uses match bonus (+2), mismatch penalty (-1), and gap penalty (-1) to trace the optimal global alignment.");

            gen.addHeading2("3.4 Algorithms 4 & 5: Suffix Array and Kasai's LCP Algorithm");
            gen.addParagraph("Implemented in SuffixArray.java and LCPArray.java. Indexes all suffixes of the combined resume and job corpus. Kasai's algorithm derives the Longest Common Prefix array in linear O(N) time, identifying recurring domain collocations and multi-word project phrases.");

            gen.addHeading2("3.5 Algorithm 6: Cryptographic SHA-256 Hashing & Deduplication");
            gen.addParagraph("Implemented in Hashing.java and DuplicateDetector.java. Normalizes resume text to compute SHA-256 message digests for instant O(1) exact duplicate identification, alongside Jaccard token set similarity for near-duplicate revision tracking.");

            gen.addHeading2("3.6 Algorithm 7: Greedy Approximation Optimization");
            gen.addParagraph("Implemented in ApproximateMatcher.java. Solves multi-criteria candidate optimization by approximating the maximum qualification coverage density across competing skill requirements.");

            // 4. Scoring Model
            gen.addHeading1("4. Weighted Composite Scoring Model");
            gen.addParagraph("Final candidate match scores are calculated using a weighted linear combination of the 6 algorithmic evaluations:");

            String[] scoreHeaders = {"Evaluation Component", "Algorithm Used", "Weight", "Focus Area"};
            List<String[]> scoreRows = new ArrayList<>();
            scoreRows.add(new String[]{"Direct Skill Match", "KMP String Matching", "30%", "Mandatory core requirements"});
            scoreRows.add(new String[]{"String Match Density", "Keyword Matching", "20%", "General technical vocabulary"});
            scoreRows.add(new String[]{"Edit Distance", "Levenshtein DP", "15%", "Typo & variant tolerance"});
            scoreRows.add(new String[]{"Sequence Alignment", "Needleman-Wunsch DP", "15%", "Experience & workflow sequence"});
            scoreRows.add(new String[]{"Suffix / LCP Score", "Kasai Algorithm", "10%", "Deep domain phrase overlap"});
            scoreRows.add(new String[]{"Approximation Factor", "Greedy Set-Cover", "10%", "Multi-objective density factor"});
            float[] scoreColWidths = {130f, 125f, 75f, 175f};
            gen.addTable(scoreHeaders, scoreRows, scoreColWidths);

            gen.addCalloutBox(
                "Recruiter Recommendation Thresholds",
                "Strong Match (>= 70%): Highly recommended for immediate technical review.\n" +
                "Moderate Match (55% - 69%): Meets core criteria with minor preferred skill gaps.\n" +
                "Low Match (< 55%): Substantial requirement gaps; candidate not recommended."
            );

            // 5. System Features
            gen.addHeading1("5. Key Recruiter Modules & Workflow Features");
            gen.addBulletPoint("Job Openings Hub", "Manage open requisitions with required skill stacks and trigger one-click resume screening directly for open positions.");
            gen.addBulletPoint("Screen Resumes Studio", "Drag-and-drop runtime PDF resume ingestion with Apache PDFBox 3.0.8 text extraction and real-time Java DSA screening.");
            gen.addBulletPoint("Candidates Directory & CSV Export", "Live search, quick skill filter pills ([Java], [Spring Boot], [SQL], etc.), score sorting, card/table views, and One-Click CSV Shortlist Export.");
            gen.addBulletPoint("Hiring Pipeline Kanban", "Visual candidate tracking across stages: Shortlisted, Interview Scheduled, Under Review, and Rejected.");
            gen.addBulletPoint("Multi-Resume Comparator", "Simultaneously compares 2, 3, 4, 5+ resumes side-by-side across match score, verified skills, and missing requirements.");
            gen.addBulletPoint("Duplicate Detection Auditor", "Flags duplicate and near-duplicate submissions using SHA-256 digests and similarity percentages.");
            gen.addBulletPoint("Candidate Profile Dossier", "Detailed modal dossier featuring match dials, verified/missing skill columns, pipeline stage selector, and recruiter notes.");

            // 6. REST API Specification
            gen.addHeading1("6. REST API Specification");
            String[] apiHeaders = {"Method", "Endpoint", "Description", "Status"};
            List<String[]> apiRows = new ArrayList<>();
            apiRows.add(new String[]{"GET", "/api/health", "Backend engine health check", "200 OK"});
            apiRows.add(new String[]{"GET", "/api/jobs", "Retrieve active job requisitions", "200 OK"});
            apiRows.add(new String[]{"POST", "/api/jobs", "Create new job opening", "201 Created"});
            apiRows.add(new String[]{"GET", "/api/resumes", "Retrieve loaded candidate resumes", "200 OK"});
            apiRows.add(new String[]{"POST", "/api/resumes/upload", "Upload runtime PDF resumes", "200 OK"});
            apiRows.add(new String[]{"POST", "/api/analyze", "Trigger Java DSA screening pipeline", "200 OK"});
            apiRows.add(new String[]{"GET", "/api/candidates", "Retrieve ranked candidates list", "200 OK"});
            apiRows.add(new String[]{"GET", "/api/candidates/{id}", "Retrieve individual candidate profile", "200 OK"});
            apiRows.add(new String[]{"POST", "/api/compare", "Multi-resume side-by-side comparison", "200 OK"});
            apiRows.add(new String[]{"GET", "/api/duplicates", "Exact and near-duplicate audit report", "200 OK"});
            apiRows.add(new String[]{"GET", "/api/analytics", "Recruiter dashboard summary metrics", "200 OK"});
            float[] apiColWidths = {65f, 150f, 210f, 80f};
            gen.addTable(apiHeaders, apiRows, apiColWidths);

            // 7. Setup & Execution
            gen.addHeading1("7. Setup & Execution Guide");
            gen.addParagraph("The project can be executed seamlessly from Eclipse and terminal environments:");
            gen.addCodeSnippet(new String[]{
                "# 1. Running the Java DSA Backend (Terminal)",
                "javac -d bin -cp \"lib/*:bin\" $(find src -name \"*.java\")",
                "java -cp \"lib/*:bin\" com.resumex.api.ResumeXApiServer",
                "",
                "# 2. Running the React Frontend (Terminal)",
                "npm run dev    # Starts Vite server at http://localhost:5173",
                "",
                "# 3. Running from Eclipse IDE",
                "File -> Open Projects from File System -> Select ResumeX -> Run ResumeXApiServer.java"
            });

            // Save PDF
            File outFile = new File("/Users/pasulashlokareddy/eclipse-workspace/ResumeX/ResumeX_Project_Documentation.pdf");
            gen.save(outFile);
            System.out.println("PDF saved successfully to: " + outFile.getAbsolutePath() + " (" + outFile.length() + " bytes)");

            // Copy to Artifact directory
            File artifactFile = new File("/Users/pasulashlokareddy/.gemini/antigravity/brain/f01edc72-2418-4f62-9f00-6c205dbb9d7e/ResumeX_Project_Documentation.pdf");
            Files.copy(outFile.toPath(), artifactFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("PDF copied to Artifacts: " + artifactFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
