import React, { useState } from 'react';
import {
  FlaskConical,
  Cpu,
  Layers,
  Code2,
  CheckCircle,
  HelpCircle,
  Play,
  ArrowRight,
} from 'lucide-react';

export default function AlgorithmInsights({ algorithms = [] }) {
  // Interactive Viva Playground States
  const [activeTab, setActiveTab] = useState('overview'); // 'overview' | 'playground'

  // Edit Distance Playground
  const [wordA, setWordA] = useState('Java');
  const [wordB, setWordB] = useState('Jvaa');

  // KMP Playground
  const [kmpText, setKmpText] = useState('Experienced with Java Spring Boot and MySQL database');
  const [kmpPattern, setKmpPattern] = useState('Spring Boot');

  // Calculate Levenshtein in JS for immediate UI demo
  const computeLevenshtein = (s1, s2) => {
    const a = s1.toLowerCase();
    const b = s2.toLowerCase();
    const m = a.length;
    const n = b.length;
    const dp = Array.from({ length: m + 1 }, () => Array(n + 1).fill(0));

    for (let i = 0; i <= m; i++) dp[i][0] = i;
    for (let j = 0; j <= n; j++) dp[0][j] = j;

    for (let i = 1; i <= m; i++) {
      for (let j = 1; j <= n; j++) {
        if (a[i - 1] === b[j - 1]) {
          dp[i][j] = dp[i - 1][j - 1];
        } else {
          dp[i][j] = 1 + Math.min(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1]);
        }
      }
    }

    const dist = dp[m][n];
    const maxLen = Math.max(m, n);
    const sim = maxLen === 0 ? 100 : (1 - dist / maxLen) * 100;
    return { dist, sim: Math.max(0, sim), dp };
  };

  const levResult = computeLevenshtein(wordA, wordB);

  // KMP LPS computation for demo
  const computeLPS = (pat) => {
    const lps = Array(pat.length).fill(0);
    let len = 0;
    let i = 1;
    while (i < pat.length) {
      if (pat[i].toLowerCase() === pat[len].toLowerCase()) {
        len++;
        lps[i] = len;
        i++;
      } else {
        if (len !== 0) {
          len = lps[len - 1];
        } else {
          lps[i] = 0;
          i++;
        }
      }
    }
    return lps;
  };

  const lpsArray = computeLPS(kmpPattern);
  const kmpFound = kmpText.toLowerCase().includes(kmpPattern.toLowerCase());

  return (
    <div className="algorithm-insights-page">
      {/* Top Banner */}
      <div className="algo-hero-card">
        <div className="algo-hero-left">
          <div className="algo-hero-badge">
            <FlaskConical size={26} />
          </div>
          <div>
            <h2>Algorithm Insights & Viva Examination Guide</h2>
            <p>
              Detailed theoretical breakdown, time & space complexities, and mathematical formulations
              for all algorithms implemented in the Java Eclipse backend.
            </p>
          </div>
        </div>

        <div className="tab-pill-toggle">
          <button
            className={`pill-btn ${activeTab === 'overview' ? 'active' : ''}`}
            onClick={() => setActiveTab('overview')}
          >
            DSA Reference Guide
          </button>
          <button
            className={`pill-btn ${activeTab === 'playground' ? 'active' : ''}`}
            onClick={() => setActiveTab('playground')}
          >
            <Play size={13} /> Interactive Playground
          </button>
        </div>
      </div>

      {activeTab === 'overview' ? (
        <div className="algo-theory-stack">
          {/* 1. KMP String Matching */}
          <section className="algo-theory-card">
            <div className="theory-header">
              <span className="algo-number">01</span>
              <div>
                <h3>Knuth-Morris-Pratt (KMP) String Matching</h3>
                <small>Package: com.resumex.algorithms.KMPMatcher • Used by: ScreeningEngine</small>
              </div>
              <div className="complexity-pills">
                <span className="comp-pill">Time: O(n + m)</span>
                <span className="comp-pill">Space: O(m)</span>
              </div>
            </div>
            <div className="theory-body">
              <p>
                <strong>Why used in ResumeX:</strong> When screening resumes for dozens of technical skills (e.g., "Spring Boot", "REST API", "Docker"), naive string searching incurs O(n * m) worst-case time with frequent backtracks. KMP precomputes a <strong>Longest Prefix Suffix (LPS)</strong> table of length m. When a mismatch occurs, the LPS table determines the maximum characters we can bypass without missing any match, yielding optimal linear O(n + m) execution.
              </p>
              <div className="viva-box">
                <strong>Viva Q&A Talking Point:</strong> "How does KMP avoid rescanning text characters?"
                <p>Answer: KMP utilizes the LPS array to slide the pattern to the longest proper prefix that is also a suffix, ensuring the main text index 'i' never decrements.</p>
              </div>
            </div>
          </section>

          {/* 2. Levenshtein Edit Distance */}
          <section className="algo-theory-card">
            <div className="theory-header">
              <span className="algo-number">02</span>
              <div>
                <h3>Levenshtein Edit Distance (Dynamic Programming)</h3>
                <small>Package: com.resumex.algorithms.EditDistance • Used by: ApproximateMatcher & NearDuplicateDetector</small>
              </div>
              <div className="complexity-pills">
                <span className="comp-pill">Time: O(m * n)</span>
                <span className="comp-pill">Space: O(m * n)</span>
              </div>
            </div>
            <div className="theory-body">
              <p>
                <strong>Why used in ResumeX:</strong> Candidates often make typographical errors or spell technology names with variations (e.g. "Jvaa" for "Java", "Springboot" for "Spring Boot", "ReactJS" for "React"). Levenshtein Edit Distance measures the minimum number of single-character insertions, deletions, or substitutions required to transform one string into another.
              </p>
              <div className="formula-box">
                <code>DP[i][j] = if (s1[i-1] == s2[j-1]) DP[i-1][j-1] else 1 + min(Insert: DP[i][j-1], Delete: DP[i-1][j], Replace: DP[i-1][j-1])</code>
              </div>
              <div className="viva-box">
                <strong>Viva Q&A Talking Point:</strong> "How is Edit Distance converted into a similarity percentage?"
                <p>Answer: <code>Similarity = (1.0 - (Distance / max(length1, length2))) * 100%</code>. A distance of 0 yields 100% similarity.</p>
              </div>
            </div>
          </section>

          {/* 3. Needleman-Wunsch Sequence Alignment */}
          <section className="algo-theory-card">
            <div className="theory-header">
              <span className="algo-number">03</span>
              <div>
                <h3>Needleman-Wunsch Global Sequence Alignment</h3>
                <small>Package: com.resumex.algorithms.SequenceAlignment • Used by: ScreeningEngine</small>
              </div>
              <div className="complexity-pills">
                <span className="comp-pill">Time: O(m * n)</span>
                <span className="comp-pill">Space: O(m * n)</span>
              </div>
            </div>
            <div className="theory-body">
              <p>
                <strong>Why used in ResumeX:</strong> Job descriptions often specify skills in a preferred hierarchy or progression (e.g. foundational language → framework → database → containerization). Sequence Alignment evaluates whether the candidate's skill acquisition sequence matches the expected order in the job description, penalizing mismatches and skill gaps.
              </p>
              <div className="formula-box">
                <code>Scoring: Match = +2, Mismatch = -1, Gap Penalty = -2</code>
              </div>
              <div className="viva-box">
                <strong>Viva Q&A Talking Point:</strong> "What is the difference between Edit Distance and Sequence Alignment?"
                <p>Answer: Edit distance counts the minimum unweighted edits to make strings equal, whereas Needleman-Wunsch optimizes a custom scoring matrix with positive rewards for matches and tunable penalties for gaps and mismatches.</p>
              </div>
            </div>
          </section>

          {/* 4. Suffix Array + Kasai's LCP */}
          <section className="algo-theory-card">
            <div className="theory-header">
              <span className="algo-number">04</span>
              <div>
                <h3>Suffix Array & Kasai's Longest Common Prefix (LCP)</h3>
                <small>Package: com.resumex.algorithms.SuffixArray & LCPArray • Used by: ScreeningEngine</small>
              </div>
              <div className="complexity-pills">
                <span className="comp-pill">Time: O(n log n) + O(n)</span>
                <span className="comp-pill">Space: O(n)</span>
              </div>
            </div>
            <div className="theory-body">
              <p>
                <strong>Why used in ResumeX:</strong> To extract multi-word technical phrases shared between the resume and job description (e.g. "scalable backend microservices"), we construct a <strong>Generalized Suffix Array</strong> on <code>resumeText + "#" + jobText</code> and compute the LCP array in linear O(n) time using <strong>Kasai's algorithm</strong>. Adjacent suffixes crossing the '#' boundary with high LCP identify the longest common technical phrases.
              </p>
              <div className="viva-box">
                <strong>Viva Q&A Talking Point:</strong> "Why is Kasai's algorithm O(n) instead of O(n^2)?"
                <p>Answer: Kasai observes that when moving from suffix i to suffix i+1, the LCP value decreases by at most 1: <code>LCP[rank[i+1]] &gt;= LCP[rank[i]] - 1</code>. Thus the character comparison pointer 'k' decrements at most n times, giving linear amortized complexity.</p>
              </div>
            </div>
          </section>

          {/* 5. Cryptographic & Rolling Hashing */}
          <section className="algo-theory-card">
            <div className="theory-header">
              <span className="algo-number">05</span>
              <div>
                <h3>SHA-256 Hashing & Fast Hash Lookups</h3>
                <small>Package: com.resumex.algorithms.Hashing • Used by: DuplicateDetector</small>
              </div>
              <div className="complexity-pills">
                <span className="comp-pill">Time: O(n)</span>
                <span className="comp-pill">Space: O(1) hash digest</span>
              </div>
            </div>
            <div className="theory-body">
              <p>
                <strong>Why used in ResumeX:</strong> Comparing every resume pairwise with all other resumes using string equality is O(k^2 * n). By computing a 256-bit cryptographic SHA-256 digest of normalized text, we store hashes in a HashMap for O(1) instantaneous exact duplicate detection.
              </p>
              <div className="viva-box">
                <strong>Viva Q&A Talking Point:</strong> "Why do we normalize before hashing?"
                <p>Answer: Resumes submitted with different carriage returns (\r\n vs \n) or excess spaces would produce different SHA-256 digests. Normalization ensures semantically identical resumes hash to the exact same fingerprint.</p>
              </div>
            </div>
          </section>

          {/* 6. Greedy Bipartite Approximation */}
          <section className="algo-theory-card">
            <div className="theory-header">
              <span className="algo-number">06</span>
              <div>
                <h3>Greedy Bipartite Skill Matching Approximation</h3>
                <small>Package: com.resumex.algorithms.ApproximateMatcher • Used by: ScreeningEngine</small>
              </div>
              <div className="complexity-pills">
                <span className="comp-pill">Time: O(|C|*|R| log(|C|*|R|))</span>
                <span className="comp-pill">Approximation Factor: 1/2</span>
              </div>
            </div>
            <div className="theory-body">
              <p>
                <strong>Why used in ResumeX:</strong> Exact Maximum Weight Bipartite Matching using the Hungarian algorithm runs in O(V^2 E) time, which is computationally expensive during bulk screening. Our greedy approximation algorithm pairs candidate skills with required skills by similarity descending in near-linear time, achieving a proven 1/2-approximation factor.
              </p>
            </div>
          </section>
        </div>
      ) : (
        /* Interactive Viva Playground */
        <div className="playground-container">
          <div className="panel">
            <div className="panel-header">
              <div>
                <h3>Interactive Edit Distance Playground</h3>
                <p>Test Levenshtein distance and similarity percentage between two words live</p>
              </div>
            </div>

            <div className="form-grid">
              <div className="field">
                <label>String A (e.g. Job Requirement)</label>
                <input value={wordA} onChange={(e) => setWordA(e.target.value)} />
              </div>
              <div className="field">
                <label>String B (e.g. Candidate Resume Keyword)</label>
                <input value={wordB} onChange={(e) => setWordB(e.target.value)} />
              </div>
            </div>

            <div className="demo-result-card" style={{ marginTop: 16 }}>
              <div className="demo-metric">
                <small>Edit Distance</small>
                <strong>{levResult.dist} operation(s)</strong>
              </div>
              <div className="demo-metric">
                <small>Computed Similarity</small>
                <strong style={{ color: '#10b981' }}>{levResult.sim.toFixed(1)}%</strong>
              </div>
              <div className="demo-metric">
                <small>Status</small>
                <span>{levResult.sim >= 75 ? "✓ Valid Technical Match" : "✗ Typo / Dissimilar"}</span>
              </div>
            </div>
          </div>

          <div className="panel" style={{ marginTop: 24 }}>
            <div className="panel-header">
              <div>
                <h3>KMP Prefix Suffix (LPS) Array Demonstration</h3>
                <p>Observe how pattern characters map to the Longest Prefix Suffix table</p>
              </div>
            </div>

            <div className="field">
              <label>Pattern String</label>
              <input value={kmpPattern} onChange={(e) => setKmpPattern(e.target.value)} />
            </div>

            <div className="lps-visual-row" style={{ marginTop: 16 }}>
              <div className="lps-table-preview">
                <div className="lps-row-chars">
                  <span className="lps-label">Char:</span>
                  {kmpPattern.split('').map((char, i) => (
                    <div className="lps-cell" key={i}>{char}</div>
                  ))}
                </div>
                <div className="lps-row-values">
                  <span className="lps-label">LPS:</span>
                  {lpsArray.map((val, i) => (
                    <div className="lps-cell val" key={i}>{val}</div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
