# ResumeX — AI Resume Screening & Intelligent Candidate Ranking System

> **DSA-Powered Resume Intelligence Platform** inspired by modern recruitment SaaS workflows (Ashby, Workday, LinkedIn Recruiter).

ResumeX automatically screens, compares, and ranks candidate resumes against targeted Job Descriptions using **6 foundational Data Structures and Algorithms (DSA)** implemented in an **Eclipse Java backend**, paired with a **React + Vite frontend**.

---

## 🏛️ Architecture Overview

ResumeX is architected with a decoupled full-stack design:

```text
ResumeX/
│
├── Eclipse / Java Backend (Port 8080)
│   ├── src/com/resumex/
│   │   ├── algorithms/     # Core DSA algorithms (KMP, Levenshtein, Needleman-Wunsch, SuffixArray/Kasai LCP, Hashing)
│   │   ├── api/            # High-performance pure Java SE HttpServer REST API
│   │   ├── engine/         # Screening engine & weighted composite scoring
│   │   ├── models/         # Resume, Candidate, JobDescription, ScreeningReport
│   │   ├── parser/         # Apache PDFBox 3.0.8 PDF text extraction & regex skill parsing
│   │   ├── repositories/   # In-memory thread-safe Resume & Job repositories
│   │   └── service/        # Resume screening orchestrator
│   ├── lib/                # Apache PDFBox 3.0.8 JAR
│   └── resumes/            # Default sample candidate PDF resumes
│
└── VS Code / React Frontend (Port 5173)
    ├── src/
    │   ├── components/     # Modern recruiter UI (CandidateCard, RankingTable, ResumeComparison, Modals)
    │   ├── pages/          # Dashboard, JobOpenings, ScreenResume, Candidates, HiringPipeline, DuplicateDetection
    │   ├── services/       # API client connecting to Java backend
    │   └── utils/          # Recruiter formatting & scoring badges
    └── package.json
```

---

## ⚡ Core DSA Algorithms (Java Backend)

All algorithmic logic is implemented in pure Java:

1. **String Matching (Knuth-Morris-Pratt / KMP)**:
   - Uses precomputed prefix-function (`\pi` table) to achieve \(O(N + M)\) keyword and phrase searching across resume text.
2. **Edit / Levenshtein Distance (Dynamic Programming)**:
   - Evaluates string distance and typo tolerance between job requirements and resume tokens.
3. **Sequence Alignment (Needleman-Wunsch DP)**:
   - Performs global sequence alignment with scoring matrix and gap penalties to evaluate term order and contextual flow.
4. **Suffix Array & Longest Common Prefix (LCP via Kasai's Algorithm)**:
   - Builds sorted suffix arrays with \(O(N)\) Kasai LCP computation for substring analysis and shared domain phrase extraction.
5. **Cryptographic Hashing & Duplicate Detection (SHA-256 + Similarity Thresholds)**:
   - Fast exact duplicate detection via SHA-256 message digests, and near-duplicate detection via content overlap.
6. **Approximation Algorithm (Greedy Optimization)**:
   - Approximation-based multi-criteria skill coverage optimization to compute candidate qualification density.

---

## ✨ Features

- **Recruiter Dashboard**: High-level metrics, candidate score distributions, duplicate alerts, and top applicant highlights.
- **Job Requisitions Hub**: Manage open positions, customize required skill stacks, and trigger one-click screening.
- **Resume Screening**: Runtime PDF drag-and-drop upload, Apache PDFBox text extraction, and Java DSA scoring.
- **Candidates Directory**: Fast search, skill filter pills (`Java`, `Spring Boot`, `SQL`, `Docker`), score sorting, card/table views, and **One-Click CSV Shortlist Export**.
- **Hiring Pipeline**: Visual Kanban board to track candidate progression (*Shortlisted*, *Interview Scheduled*, *Under Review*, *Rejected*).
- **Multi-Resume Comparison**: Compare 2, 3, 4, 5+ candidates side-by-side across overall match score, verified skills, and missing requirements.
- **Duplicate Resume Detection**: Audit exact and near-duplicate submissions.
- **Recruiter Profile Dossier**: Detailed candidate analysis modal with pipeline stage selector and recruiter evaluation notes.

---

## 🚀 Getting Started

### Prerequisites

- **Java JDK 17+**
- **Node.js 18+** & **npm**
- **Eclipse IDE** (or command line)

---

### Running the Java Backend

#### Option A: Via Eclipse
1. Open Eclipse IDE.
2. Select `File` → `Open Projects from File System...` → Choose the `ResumeX` directory.
3. Right-click `com.resumex.api.ResumeXApiServer.java` → `Run As` → `Java Application`.
4. The server starts at `http://localhost:8080`.

#### Option B: Via Terminal
```bash
# From the project root
javac -d bin -cp "lib/*:bin" $(find src -name "*.java")
java -cp "lib/*:bin" com.resumex.api.ResumeXApiServer
```

---

### Running the Frontend

```bash
# From the project root
npm run dev

# Or from the frontend directory
cd frontend
npm install
npm run dev
```

Visit **[http://localhost:5173](http://localhost:5173)** in your browser.

---

## 📡 API Endpoints (Java Backend)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/health` | Health check & DSA engine status |
| `GET` | `/api/jobs` | Retrieve all job descriptions |
| `POST` | `/api/jobs` | Create a new job requisition |
| `GET` | `/api/resumes` | Retrieve loaded resumes |
| `POST` | `/api/resumes/upload` | Multipart PDF resume upload |
| `POST` | `/api/analyze` | Run DSA screening pipeline |
| `GET` | `/api/candidates` | Get ranked candidates list |
| `GET` | `/api/candidates/{id}` | Candidate detail profile |
| `POST` | `/api/compare` | Multi-candidate comparison |
| `GET` | `/api/duplicates` | Exact & near-duplicate report |
| `GET` | `/api/analytics` | Recruiter dashboard metrics |

---

## 📄 License

Academic / College Project — Developed for educational DSA demonstration.
