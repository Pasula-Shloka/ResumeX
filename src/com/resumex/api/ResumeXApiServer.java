package com.resumex.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.resumex.models.Candidate;
import com.resumex.models.JobDescription;
import com.resumex.models.Resume;
import com.resumex.models.ScreeningReport;
import com.resumex.repositories.JobRepository;
import com.resumex.repositories.ResumeRepository;
import com.resumex.services.DuplicateDetector;
import com.resumex.services.NearDuplicateDetector;
import com.resumex.services.PdfResumeParser;
import com.resumex.services.RankingEngine;
import com.resumex.services.ResumeProcessingService;
import com.resumex.services.ResumeScreeningService;
import com.resumex.services.ScreeningEngine;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class ResumeXApiServer {

    private static final int PORT = 8080;
    private static final String DEFAULT_RESUME_FOLDER =
            "/Users/pasulashlokareddy/eclipse-workspace/ResumeX/resumes";

    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final PdfResumeParser pdfResumeParser;
    private final ResumeProcessingService processingService;
    private final ResumeScreeningService screeningService;
    private final DuplicateDetector duplicateDetector;
    private final NearDuplicateDetector nearDuplicateDetector;

    private ScreeningReport latestReport;
    private JobDescription activeJob;

    public ResumeXApiServer() {
        jobRepository = new JobRepository();
        resumeRepository = new ResumeRepository();
        pdfResumeParser = new PdfResumeParser();
        processingService = new ResumeProcessingService();
        screeningService = new ResumeScreeningService();
        duplicateDetector = new DuplicateDetector();
        nearDuplicateDetector = new NearDuplicateDetector();

        // Set default job
        activeJob = jobRepository.findById(101);
        if (activeJob == null && !jobRepository.getAllJobs().isEmpty()) {
            activeJob = jobRepository.getAllJobs().get(0);
        }

        // Preload existing resumes from project directory
        loadInitialResumes();

        // Perform initial screening so endpoints are populated immediately
        performScreening(activeJob);
    }

    private void loadInitialResumes() {
        try {
            File folder = new File(DEFAULT_RESUME_FOLDER);
            if (folder.exists() && folder.isDirectory()) {
                List<Resume> defaultResumes = processingService.processFolder(DEFAULT_RESUME_FOLDER);
                for (Resume resume : defaultResumes) {
                    resumeRepository.addResume(resume);
                }
                System.out.println("Loaded " + resumeRepository.size() + " default resumes into memory repository.");
            }
        } catch (Exception e) {
            System.err.println("Notice: Could not load initial resumes from folder: " + e.getMessage());
        }
    }

    private synchronized void performScreening(JobDescription job) {
        if (job == null) return;
        this.activeJob = job;
        List<Resume> resumes = resumeRepository.getAllResumes();
        this.latestReport = screeningService.screenResumes(resumes, job);
    }

    public static void main(String[] args) throws IOException {
        ResumeXApiServer api = new ResumeXApiServer();
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/health", exchange -> {
            if (handleCors(exchange)) return;
            api.writeJson(exchange, 200, "{\"status\":\"ok\",\"service\":\"ResumeX API\",\"engine\":\"Java DSA Core\",\"version\":\"2.0\"}");
        });

        server.createContext("/api/jobs", exchange -> {
            if (handleCors(exchange)) return;
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                api.writeJson(exchange, 200, api.jobsJson());
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                api.handleCreateJob(exchange);
            } else {
                api.writeJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        });

        server.createContext("/api/resumes/upload", exchange -> {
            if (handleCors(exchange)) return;
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                api.handleResumeUpload(exchange);
            } else {
                api.writeJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        });

        server.createContext("/api/resumes", exchange -> {
            if (handleCors(exchange)) return;
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                api.writeJson(exchange, 200, api.resumesJson());
            } else {
                api.writeJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        });

        server.createContext("/api/analyze", exchange -> {
            if (handleCors(exchange)) return;
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                api.handleAnalyze(exchange);
            } else {
                api.writeJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        });

        server.createContext("/api/screenings", exchange -> {
            if (handleCors(exchange)) return;
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                api.writeJson(exchange, 200, api.screeningJson());
            } else {
                api.writeJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        });

        server.createContext("/api/candidates", exchange -> {
            if (handleCors(exchange)) return;
            String path = exchange.getRequestURI().getPath();
            if (path.matches("^/api/candidates/\\d+$")) {
                int id = Integer.parseInt(path.substring("/api/candidates/".length()));
                api.handleGetCandidateById(exchange, id);
            } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                api.writeJson(exchange, 200, api.candidatesJson());
            } else {
                api.writeJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        });

        server.createContext("/api/compare", exchange -> {
            if (handleCors(exchange)) return;
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                api.handleCompare(exchange);
            } else {
                api.writeJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        });

        server.createContext("/api/duplicates", exchange -> {
            if (handleCors(exchange)) return;
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                api.writeJson(exchange, 200, api.duplicatesJson());
            } else {
                api.writeJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        });

        server.createContext("/api/analytics", exchange -> {
            if (handleCors(exchange)) return;
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                api.writeJson(exchange, 200, api.analyticsJson());
            } else {
                api.writeJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        });

        server.createContext("/api/algorithms/explain", exchange -> {
            if (handleCors(exchange)) return;
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                api.writeJson(exchange, 200, api.algorithmsExplainJson());
            } else {
                api.writeJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        });

        server.setExecutor(null);
        server.start();

        System.out.println("==========================================================");
        System.out.println("   ResumeX Java DSA API Server running at http://localhost:" + PORT);
        System.out.println("   Endpoints: /api/health, /api/jobs, /api/resumes, /api/resumes/upload");
        System.out.println("              /api/analyze, /api/candidates, /api/compare, /api/duplicates");
        System.out.println("              /api/analytics, /api/algorithms/explain");
        System.out.println("==========================================================");
    }

    private static boolean handleCors(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }

    /*
     * ------------------------------------------------------------
     * CONTROLLER HANDLERS
     * ------------------------------------------------------------
     */

    private void handleCreateJob(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        try {
            String title = extractJsonField(body, "title");
            String description = extractJsonField(body, "description");
            List<String> requiredSkills = extractJsonArray(body, "requiredSkills");
            if (requiredSkills.isEmpty()) {
                requiredSkills = extractJsonArray(body, "required");
            }

            if (title == null || title.trim().isEmpty()) {
                writeJson(exchange, 400, "{\"error\":\"Job title is required\"}");
                return;
            }

            int nextId = 100 + jobRepository.size() + 1;
            JobDescription newJob = new JobDescription(nextId, title, description != null ? description : title, requiredSkills);
            jobRepository.addJob(newJob);

            writeJson(exchange, 201, "{\"status\":\"success\",\"job\":" + jobSummaryJson(newJob) + "}");
        } catch (Exception e) {
            writeJson(exchange, 500, "{\"error\":\"Failed to create job: " + escape(e.getMessage()) + "\"}");
        }
    }

    private void handleResumeUpload(HttpExchange exchange) throws IOException {
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null) contentType = "";

        List<Resume> newlyParsed = new ArrayList<>();

        try {
            if (contentType.toLowerCase().contains("multipart/form-data")) {
                // Parse multipart
                String boundary = extractBoundary(contentType);
                byte[] requestBytes = readRequestBodyBytes(exchange);
                List<MultipartPart> parts = parseMultipart(requestBytes, boundary);

                for (MultipartPart part : parts) {
                    if (part.filename != null && part.filename.toLowerCase().endsWith(".pdf")) {
                        int id = resumeRepository.getNextId();
                        Resume resume = pdfResumeParser.parsePDF(part.data, part.filename, id);
                        resumeRepository.addResume(resume);
                        newlyParsed.add(resume);
                    }
                }
            } else {
                // Parse JSON with base64 encoded PDF or text
                String body = readRequestBody(exchange);
                Pattern filePattern = Pattern.compile("\\{\\s*\"name\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"data\"\\s*:\\s*\"([^\"]+)\"\\s*\\}");
                Matcher matcher = filePattern.matcher(body);

                while (matcher.find()) {
                    String filename = matcher.group(1);
                    String base64Data = matcher.group(2);

                    if (base64Data.contains(",")) {
                        base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
                    }

                    byte[] pdfBytes = Base64.getDecoder().decode(base64Data);
                    int id = resumeRepository.getNextId();
                    Resume resume = pdfResumeParser.parsePDF(pdfBytes, filename, id);
                    resumeRepository.addResume(resume);
                    newlyParsed.add(resume);
                }

                // Also check if rawText single resume upload was sent
                if (newlyParsed.isEmpty()) {
                    String candidateName = extractJsonField(body, "candidateName");
                    String rawText = extractJsonField(body, "rawText");
                    String fileName = extractJsonField(body, "fileName");
                    if (rawText != null && !rawText.trim().isEmpty()) {
                        int id = resumeRepository.getNextId();
                        List<String> skills = pdfResumeParser.getSkillExtractor().extractSkills(rawText);
                        Resume resume = new Resume(id, candidateName != null ? candidateName : "Uploaded Candidate",
                                "candidate@resumex.local", "Not Found", rawText, skills, fileName != null ? fileName : "manual_input.txt");
                        resumeRepository.addResume(resume);
                        newlyParsed.add(resume);
                    }
                }
            }

            if (newlyParsed.isEmpty()) {
                writeJson(exchange, 400, "{\"error\":\"No valid PDF files detected in upload\"}");
                return;
            }

            // Re-run screening with the new resumes included
            performScreening(activeJob);

            StringBuilder json = new StringBuilder();
            json.append("{\"status\":\"success\",\"uploadedCount\":").append(newlyParsed.size()).append(",\"resumes\":[");
            for (int i = 0; i < newlyParsed.size(); i++) {
                if (i > 0) json.append(",");
                json.append(resumeJson(newlyParsed.get(i)));
            }
            json.append("]}");

            writeJson(exchange, 200, json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            writeJson(exchange, 500, "{\"error\":\"Failed to process resume upload: " + escape(e.getMessage()) + "\"}");
        }
    }

    private void handleAnalyze(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        try {
            JobDescription targetJob = activeJob;

            String jobIdStr = extractJsonField(body, "jobId");
            if (jobIdStr != null && !jobIdStr.isEmpty()) {
                int jobId = Integer.parseInt(jobIdStr);
                JobDescription found = jobRepository.findById(jobId);
                if (found != null) {
                    targetJob = found;
                }
            } else {
                String title = extractJsonField(body, "jobTitle");
                String description = extractJsonField(body, "description");
                List<String> skills = extractJsonArray(body, "requiredSkills");
                if (skills.isEmpty()) {
                    skills = extractJsonArray(body, "required");
                }

                if (title != null && !title.trim().isEmpty()) {
                    int id = 999;
                    targetJob = new JobDescription(id, title, description != null ? description : title, skills);
                    jobRepository.addJob(targetJob);
                }
            }

            performScreening(targetJob);
            writeJson(exchange, 200, screeningJson());

        } catch (Exception e) {
            e.printStackTrace();
            writeJson(exchange, 500, "{\"error\":\"Analysis failed: " + escape(e.getMessage()) + "\"}");
        }
    }

    private void handleGetCandidateById(HttpExchange exchange, int id) throws IOException {
        if (latestReport == null) {
            performScreening(activeJob);
        }

        for (Candidate c : latestReport.getCandidates()) {
            if (c.getResume().getId() == id) {
                writeJson(exchange, 200, "{\"candidate\":" + candidateJson(c) + "}");
                return;
            }
        }

        // If not in latest report, look up in repository
        Resume resume = resumeRepository.findById(id);
        if (resume != null) {
            Candidate c = screeningService.getScreeningEngine().screenCandidate(resume, activeJob);
            writeJson(exchange, 200, "{\"candidate\":" + candidateJson(c) + "}");
            return;
        }

        writeJson(exchange, 404, "{\"error\":\"Candidate with ID " + id + " not found\"}");
    }

    private void handleCompare(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        List<String> idStrings = extractJsonArray(body, "candidateIds");
        if (idStrings.isEmpty()) {
            idStrings = extractJsonArray(body, "ids");
        }

        List<Candidate> compared = new ArrayList<>();
        if (latestReport == null) {
            performScreening(activeJob);
        }

        for (String idStr : idStrings) {
            try {
                int id = Integer.parseInt(idStr);
                for (Candidate c : latestReport.getCandidates()) {
                    if (c.getResume().getId() == id) {
                        compared.add(c);
                        break;
                    }
                }
            } catch (NumberFormatException ignored) {}
        }

        if (compared.isEmpty() && latestReport.getCandidates().size() >= 2) {
            compared.add(latestReport.getCandidates().get(0));
            compared.add(latestReport.getCandidates().get(1));
        }

        StringBuilder json = new StringBuilder();
        json.append("{\"job\":").append(jobSummaryJson(activeJob)).append(",\"candidates\":[");
        for (int i = 0; i < compared.size(); i++) {
            if (i > 0) json.append(",");
            json.append(candidateJson(compared.get(i)));
        }
        json.append("]}");

        writeJson(exchange, 200, json.toString());
    }

    /*
     * ------------------------------------------------------------
     * JSON SERIALIZATION HELPERS
     * ------------------------------------------------------------
     */

    private String jobsJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\"jobs\":[");
        List<JobDescription> jobs = jobRepository.getAllJobs();

        for (int i = 0; i < jobs.size(); i++) {
            JobDescription j = jobs.get(i);
            if (i > 0) json.append(",");
            json.append("{")
                    .append("\"id\":").append(j.getId()).append(",")
                    .append("\"title\":\"").append(escape(j.getJobTitle())).append("\",")
                    .append("\"department\":\"Engineering\",")
                    .append("\"location\":\"Hybrid\",")
                    .append("\"type\":\"Full Time\",")
                    .append("\"experience\":\"0-3 years\",")
                    .append("\"education\":\"B.Tech / B.E. / M.C.A.\",")
                    .append("\"description\":\"").append(escape(j.getDescription())).append("\",")
                    .append("\"required\":").append(stringListJson(j.getRequiredSkills())).append(",")
                    .append("\"preferred\":[\"Microservices\",\"Cloud\",\"DSA\"],")
                    .append("\"keywords\":[\"backend\",\"scalable\",\"database\",\"api\"]")
                    .append("}");
        }
        json.append("]}");
        return json.toString();
    }

    private String resumesJson() {
        List<Resume> resumes = resumeRepository.getAllResumes();
        StringBuilder json = new StringBuilder();
        json.append("{\"total\":").append(resumes.size()).append(",\"resumes\":[");
        for (int i = 0; i < resumes.size(); i++) {
            if (i > 0) json.append(",");
            json.append(resumeJson(resumes.get(i)));
        }
        json.append("]}");
        return json.toString();
    }

    private String screeningJson() {
        if (latestReport == null) {
            performScreening(activeJob);
        }

        StringBuilder json = new StringBuilder();
        json.append("{\"job\":").append(jobSummaryJson(activeJob));
        json.append(",\"pipeline\":[\"Resume Uploaded\",\"Text Extracted\",\"Text Preprocessed\",\"KMP String Matching\",\"Edit Distance\",\"Sequence Alignment\",\"Suffix Array\",\"Kasai LCP\",\"Hashing\",\"Greedy Approximation\",\"Resume Ranking\",\"Duplicate Detection\",\"Screening Complete\"]");
        json.append(",\"candidates\":[");

        List<Candidate> candidates = latestReport.getCandidates();
        for (int i = 0; i < candidates.size(); i++) {
            if (i > 0) json.append(",");
            json.append(candidateJson(candidates.get(i)));
        }
        json.append("]");

        // Append duplicates and analytics
        json.append(",\"duplicates\":").append(duplicatesJson());
        json.append(",\"analytics\":").append(analyticsJson());
        json.append("}");

        return json.toString();
    }

    private String candidatesJson() {
        if (latestReport == null) {
            performScreening(activeJob);
        }

        StringBuilder json = new StringBuilder();
        json.append("{\"candidates\":[");
        List<Candidate> candidates = latestReport.getCandidates();
        for (int i = 0; i < candidates.size(); i++) {
            if (i > 0) json.append(",");
            json.append(candidateJson(candidates.get(i)));
        }
        json.append("]}");
        return json.toString();
    }

    private String duplicatesJson() {
        List<Resume> resumes = resumeRepository.getAllResumes();
        Map<String, Resume> exactDuplicates = duplicateDetector.findDuplicateMap(resumes);
        List<NearDuplicateDetector.DuplicatePair> nearDuplicates = nearDuplicateDetector.findDuplicatePairs(resumes, 80.0);

        StringBuilder json = new StringBuilder();
        json.append("{\"exactDuplicates\":[");

        int index = 0;
        for (Map.Entry<String, Resume> entry : exactDuplicates.entrySet()) {
            if (index > 0) json.append(",");
            json.append("{\"duplicate\":\"").append(escape(entry.getKey()))
                    .append("\",\"original\":\"").append(escape(entry.getValue().getCandidateName()))
                    .append("\",\"file\":\"").append(escape(entry.getValue().getFileName()))
                    .append("\",\"method\":\"SHA-256 Text Hashing\",\"similarity\":100.0}");
            index++;
        }
        json.append("],\"nearDuplicates\":[");

        for (int i = 0; i < nearDuplicates.size(); i++) {
            NearDuplicateDetector.DuplicatePair pair = nearDuplicates.get(i);
            if (i > 0) json.append(",");
            json.append("{\"candidateA\":\"").append(escape(pair.getCandidateA()))
                    .append("\",\"fileA\":\"").append(escape(pair.getFileA()))
                    .append("\",\"candidateB\":\"").append(escape(pair.getCandidateB()))
                    .append("\",\"fileB\":\"").append(escape(pair.getFileB()))
                    .append("\",\"similarity\":").append(pair.getSimilarity())
                    .append(",\"method\":\"").append(escape(pair.getMethod()))
                    .append("\"}");
        }
        json.append("],\"totalDuplicateCount\":").append(exactDuplicates.size() + nearDuplicates.size()).append("}");
        return json.toString();
    }

    private String analyticsJson() {
        if (latestReport == null) {
            performScreening(activeJob);
        }

        List<Resume> resumes = resumeRepository.getAllResumes();
        List<Candidate> candidates = latestReport.getCandidates();

        int shortlisted = 0;
        int review = 0;
        int rejected = 0;
        double totalScore = 0;

        for (Candidate c : candidates) {
            double score = c.getMatchResult().getOverallScore();
            totalScore += score;
            if (score >= 70.0) {
                shortlisted++;
            } else if (score >= 55.0) {
                review++;
            } else {
                rejected++;
            }
        }

        int count = candidates.size();
        double average = count == 0 ? 0 : totalScore / count;

        return "{"
                + "\"totalResumes\":" + resumes.size()
                + ",\"candidatesAnalyzed\":" + count
                + ",\"shortlisted\":" + shortlisted
                + ",\"underReview\":" + review
                + ",\"rejected\":" + rejected
                + ",\"averageMatch\":" + round(average)
                + ",\"duplicateThreshold\":80"
                + ",\"screeningThreshold\":70"
                + "}";
    }

    private String candidateJson(Candidate candidate) {
        Resume resume = candidate.getResume();
        return "{"
                + "\"id\":" + resume.getId() + ","
                + "\"rank\":" + candidate.getRank() + ","
                + "\"name\":\"" + escape(resume.getCandidateName()) + "\","
                + "\"email\":\"" + escape(resume.getEmail()) + "\","
                + "\"phone\":\"" + escape(resume.getPhone()) + "\","
                + "\"file\":\"" + escape(resume.getFileName()) + "\","
                + "\"skills\":" + stringListJson(resume.getSkills()) + ","
                + "\"matchedSkills\":" + stringListJson(candidate.getMatchedSkills()) + ","
                + "\"missingSkills\":" + stringListJson(candidate.getMissingSkills()) + ","
                + "\"skillMatchPercentage\":" + round(candidate.getSkillMatchPercentage()) + ","
                + "\"commonPhrases\":" + stringListJson(candidate.getCommonPhrases()) + ","
                + "\"recommendation\":\"" + escape(candidate.getRecommendation()) + "\","
                + "\"isDuplicate\":" + candidate.isDuplicate() + ","
                + "\"duplicateReason\":\"" + escape(candidate.getDuplicateReason()) + "\","
                + "\"overallScore\":" + round(candidate.getMatchResult().getOverallScore()) + ","
                + "\"algorithms\":{"
                + "\"skillMatch\":" + round(candidate.getMatchResult().getSkillMatchScore()) + ","
                + "\"stringMatch\":" + round(candidate.getMatchResult().getStringMatchScore()) + ","
                + "\"editDistance\":" + round(candidate.getMatchResult().getEditDistanceScore()) + ","
                + "\"sequenceAlignment\":" + round(candidate.getMatchResult().getSequenceAlignmentScore()) + ","
                + "\"suffixLcp\":" + round(candidate.getMatchResult().getSuffixLcpScore()) + ","
                + "\"approximate\":" + round(candidate.getMatchResult().getApproximateScore())
                + "}"
                + "}";
    }

    private String resumeJson(Resume resume) {
        return "{"
                + "\"id\":" + resume.getId() + ","
                + "\"candidateName\":\"" + escape(resume.getCandidateName()) + "\","
                + "\"email\":\"" + escape(resume.getEmail()) + "\","
                + "\"phone\":\"" + escape(resume.getPhone()) + "\","
                + "\"file\":\"" + escape(resume.getFileName()) + "\","
                + "\"skills\":" + stringListJson(resume.getSkills()) + ","
                + "\"snippet\":\"" + escape(getSnippet(resume.getRawText())) + "\""
                + "}";
    }

    private String jobSummaryJson(JobDescription job) {
        return "{"
                + "\"id\":" + job.getId() + ","
                + "\"title\":\"" + escape(job.getJobTitle()) + "\","
                + "\"description\":\"" + escape(job.getDescription()) + "\","
                + "\"requiredSkills\":" + stringListJson(job.getRequiredSkills())
                + "}";
    }

    private String algorithmsExplainJson() {
        return "{\"algorithms\":["
                + "{"
                + "\"name\":\"KMP String Matching\","
                + "\"category\":\"Exact String Matching\","
                + "\"timeComplexity\":\"O(n + m)\","
                + "\"spaceComplexity\":\"O(m)\","
                + "\"role\":\"Identifies required skills, technologies, and job terms with zero backtracking via LPS array.\","
                + "\"vivaTip\":\"Explain how the Longest Prefix Suffix (LPS) array avoids rescanning characters upon mismatch.\""
                + "},"
                + "{"
                + "\"name\":\"Levenshtein Edit Distance\","
                + "\"category\":\"Dynamic Programming\","
                + "\"timeComplexity\":\"O(m * n)\","
                + "\"spaceComplexity\":\"O(m * n)\","
                + "\"role\":\"Measures typographical edit distance for spelling mistakes, technology variations (e.g. Java vs Jvaa).\","
                + "\"vivaTip\":\"Demonstrate the 3 operations: insertion, deletion, and substitution with recurrence relation.\""
                + "},"
                + "{"
                + "\"name\":\"Needleman-Wunsch Sequence Alignment\","
                + "\"category\":\"Dynamic Programming\","
                + "\"timeComplexity\":\"O(m * n)\","
                + "\"spaceComplexity\":\"O(m * n)\","
                + "\"role\":\"Assesses structural ordering and hierarchy of technical skills between candidate experience and job requirements.\","
                + "\"vivaTip\":\"Explain the scoring matrix: Match (+2), Mismatch (-1), Gap Penalty (-2) and traceback.\""
                + "},"
                + "{"
                + "\"name\":\"Suffix Array + Kasai's LCP\","
                + "\"category\":\"Advanced Text Indexing\","
                + "\"timeComplexity\":\"O(n log n) construction, O(n) LCP\","
                + "\"spaceComplexity\":\"O(n)\","
                + "\"role\":\"Enables lightning-fast substring search via binary search O(m log n) and finds longest common phrases between resume and JD.\","
                + "\"vivaTip\":\"Show how generalized suffix array combined with '#' extracts shared technical phrases.\""
                + "},"
                + "{"
                + "\"name\":\"SHA-256 Cryptographic Hashing\","
                + "\"category\":\"Hashing & Duplicate Detection\","
                + "\"timeComplexity\":\"O(n)\","
                + "\"spaceComplexity\":\"O(1) hash digest\","
                + "\"role\":\"Generates unique fingerprint of normalized text for instant O(1) exact duplicate candidate identification.\","
                + "\"vivaTip\":\"Contrast SHA-256 for exact duplicates with Edit Distance for near-duplicate resumes.\""
                + "},"
                + "{"
                + "\"name\":\"Greedy Bipartite Approximation\","
                + "\"category\":\"Approximation Algorithm\","
                + "\"timeComplexity\":\"O(|C|*|R| log(|C|*|R|))\","
                + "\"spaceComplexity\":\"O(|C|*|R|)\","
                + "\"role\":\"Solves maximum weight bipartite matching between candidate skills and job requirements in near-linear time with 1/2-approximation factor.\","
                + "\"vivaTip\":\"Explain why exact bipartite matching (Hungarian O(V^2 E)) is approximated greedily for fast screening.\""
                + "}"
                + "]}";
    }

    private String stringListJson(List<String> values) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                if (i > 0) json.append(",");
                json.append("\"").append(escape(values.get(i))).append("\"");
            }
        }
        json.append("]");
        return json.toString();
    }

    private String getSnippet(String text) {
        if (text == null) return "";
        String clean = text.replaceAll("\\s+", " ").trim();
        return clean.length() <= 160 ? clean : clean.substring(0, 160) + "...";
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private void writeJson(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
            return baos.toString(StandardCharsets.UTF_8);
        }
    }

    private byte[] readRequestBodyBytes(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = is.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
            return baos.toByteArray();
        }
    }

    /*
     * ------------------------------------------------------------
     * PARSING UTILITIES (JSON & MULTIPART)
     * ------------------------------------------------------------
     */

    private static String extractJsonField(String json, String field) {
        if (json == null) return null;
        Pattern p = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1).replace("\\\"", "\"").replace("\\n", "\n").replace("\\\\", "\\");
        }
        // Check number
        Pattern pNum = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*([0-9.]+)");
        Matcher mNum = pNum.matcher(json);
        if (mNum.find()) {
            return mNum.group(1);
        }
        return null;
    }

    private static List<String> extractJsonArray(String json, String field) {
        List<String> list = new ArrayList<>();
        if (json == null) return list;
        Pattern p = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\\[([^\\]]*)\\]");
        Matcher m = p.matcher(json);
        if (m.find()) {
            String arrayContent = m.group(1).trim();
            if (!arrayContent.isEmpty()) {
                String[] tokens = arrayContent.split(",");
                for (String t : tokens) {
                    String clean = t.trim().replaceAll("^\"|\"$", "").trim();
                    if (!clean.isEmpty()) {
                        list.add(clean);
                    }
                }
            }
        }
        return list;
    }

    private static String extractBoundary(String contentType) {
        int idx = contentType.indexOf("boundary=");
        if (idx != -1) {
            String b = contentType.substring(idx + 9).trim();
            if (b.startsWith("\"") && b.endsWith("\"")) {
                b = b.substring(1, b.length() - 1);
            }
            return b;
        }
        return "";
    }

    private static class MultipartPart {
        String filename;
        byte[] data;
    }

    private static List<MultipartPart> parseMultipart(byte[] body, String boundary) {
        List<MultipartPart> parts = new ArrayList<>();
        if (boundary.isEmpty() || body.length == 0) return parts;

        byte[] boundaryBytes = ("--" + boundary).getBytes(StandardCharsets.ISO_8859_1);
        List<Integer> boundaryPositions = findSubarrayOccurrences(body, boundaryBytes);

        for (int i = 0; i < boundaryPositions.size() - 1; i++) {
            int start = boundaryPositions.get(i) + boundaryBytes.length;
            int end = boundaryPositions.get(i + 1);

            // Skip \r\n after boundary
            if (start + 2 <= end && body[start] == '\r' && body[start + 1] == '\n') {
                start += 2;
            }

            // Find header delimiter \r\n\r\n
            byte[] headerDelim = "\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1);
            int headerDelimIdx = findSubarray(body, headerDelim, start, end);
            if (headerDelimIdx == -1) continue;

            String headers = new String(body, start, headerDelimIdx - start, StandardCharsets.UTF_8);
            int dataStart = headerDelimIdx + 4;
            int dataEnd = end;

            // Trim trailing \r\n before next boundary
            if (dataEnd >= dataStart + 2 && body[dataEnd - 2] == '\r' && body[dataEnd - 1] == '\n') {
                dataEnd -= 2;
            }

            String filename = null;
            Matcher fnMatcher = Pattern.compile("filename=\"([^\"]+)\"").matcher(headers);
            if (fnMatcher.find()) {
                filename = fnMatcher.group(1);
            }

            if (filename != null && dataEnd > dataStart) {
                byte[] fileBytes = new byte[dataEnd - dataStart];
                System.arraycopy(body, dataStart, fileBytes, 0, fileBytes.length);
                MultipartPart part = new MultipartPart();
                part.filename = filename;
                part.data = fileBytes;
                parts.add(part);
            }
        }
        return parts;
    }

    private static List<Integer> findSubarrayOccurrences(byte[] src, byte[] target) {
        List<Integer> list = new ArrayList<>();
        int idx = 0;
        while (idx <= src.length - target.length) {
            int found = findSubarray(src, target, idx, src.length);
            if (found != -1) {
                list.add(found);
                idx = found + target.length;
            } else {
                break;
            }
        }
        return list;
    }

    private static int findSubarray(byte[] src, byte[] target, int from, int to) {
        int max = to - target.length;
        for (int i = from; i <= max; i++) {
            boolean match = true;
            for (int j = 0; j < target.length; j++) {
                if (src[i + j] != target[j]) {
                    match = false;
                    break;
                }
            }
            if (match) return i;
        }
        return -1;
    }
}
