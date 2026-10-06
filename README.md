# 🚀 ResumeX — AI Resume Screening & Intelligent Candidate Ranking System

### 🌐 Live Working Link:
👉 **[https://pasula-shloka.github.io/AI-ResumeScreening/](https://pasula-shloka.github.io/AI-ResumeScreening/)** 👈

---

## 💻 Quick Run Guide

### 1. Run Java DSA Backend (Eclipse)
- Open project in **Eclipse IDE**.
- Right-click `src/com/resumex/api/ResumeXApiServer.java` → **Run As** → **Java Application**.
- Server runs on `http://localhost:8080`.

### 2. Run Modern Frontend (VS Code / Terminal)
```bash
npm run dev
```
Open **[http://localhost:5173](http://localhost:5173)** in your browser.

---

## 🧠 Core DSA Algorithms (Java Backend)

All 6 algorithms are implemented in **pure Java** inside the Eclipse project:

1. **String Matching**: `KMPMatcher.java` — Knuth-Morris-Pratt $O(N + M)$ skill & keyword search.
2. **Edit Distance**: `EditDistance.java` — Levenshtein Dynamic Programming for typo & spelling tolerance.
3. **Sequence Alignment**: `SequenceAlignment.java` — Needleman-Wunsch DP global sequence alignment.
4. **Suffix Array**: `SuffixArray.java` — Full-text substring search and index construction.
5. **LCP Array**: `LCPArray.java` — Kasai's linear $O(N)$ Longest Common Prefix analysis.
6. **Hashing**: `Hashing.java` & `DuplicateDetector.java` — SHA-256 cryptographic hashing & duplicate audit.
7. **Approximation Algorithm**: `ApproximateMatcher.java` — Greedy multi-criteria optimization.

---

## 📁 Project Architecture
```text
ResumeX/
├── Eclipse/     --> Java Backend + 6 DSA Algorithms (Port 8080)
└── VS Code/     --> Modern React + Vite Frontend (Port 5173)
```

---

## 📄 Documentation & PDF Report
- Comprehensive Project Documentation: [`PROJECT_DOCUMENTATION.md`](./PROJECT_DOCUMENTATION.md)
- Official PDF Report: [`ResumeX_Project_Documentation.pdf`](./ResumeX_Project_Documentation.pdf)
