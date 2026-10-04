import React from 'react';
import {
  Sparkles,
  ArrowRight,
  SearchCheck,
  LayoutDashboard,
  Layers,
  Cpu,
  FileCheck2,
  CheckCircle,
  ShieldCheck,
  Zap,
} from 'lucide-react';

export default function LandingPage({ setActivePage }) {
  return (
    <div className="landing-page">
      {/* Hero Section */}
      <section className="hero-section">
        <div className="hero-content">
          <div className="hero-pill">
            <Sparkles size={14} className="hero-pill-icon" />
            <span>Academic DSA Capstone Project • Java & React</span>
          </div>

          <h1 className="hero-title">
            Resume<span className="hero-gradient-text">X</span>
          </h1>
          <h2 className="hero-subtitle">Find the right candidate faster.</h2>

          <p className="hero-description">
            Analyze and screen resumes against Job Descriptions using intelligent, foundational
            <strong> DSA algorithms</strong>: KMP String Matching, Levenshtein Edit Distance,
            Needleman-Wunsch Sequence Alignment, Generalized Suffix Arrays, Kasai's LCP,
            Cryptographic Hashing, and Greedy Approximation.
          </p>

          <div className="hero-cta-group">
            <button className="primary-button hero-cta" onClick={() => setActivePage('Screen Resume')}>
              <SearchCheck size={18} />
              <span>Analyze Resumes</span>
              <ArrowRight size={16} />
            </button>
            <button className="secondary-button hero-cta" onClick={() => setActivePage('Dashboard')}>
              <LayoutDashboard size={18} />
              <span>View Dashboard</span>
            </button>
          </div>

          <div className="hero-trust-row">
            <div className="trust-item">
              <CheckCircle size={15} /> 100% Real Java Backend Calculations
            </div>
            <div className="trust-item">
              <CheckCircle size={15} /> Apache PDFBox Text Extraction
            </div>
            <div className="trust-item">
              <CheckCircle size={15} /> Runtime PDF Multi-Upload
            </div>
          </div>
        </div>

        {/* Hero Visual Showcase */}
        <div className="hero-visual">
          <div className="hero-card-glass">
            <div className="glass-header">
              <div className="glass-dots">
                <span /> <span /> <span />
              </div>
              <small>ResumeX Processing Engine (Java SE 21)</small>
            </div>

            <div className="glass-content">
              <div className="pipeline-flow-mini">
                <div className="flow-step">
                  <div className="flow-icon pdf">PDF</div>
                  <span>Resume Upload</span>
                </div>
                <div className="flow-connector">→</div>
                <div className="flow-step">
                  <div className="flow-icon dsa">DSA</div>
                  <span>6 Core Algorithms</span>
                </div>
                <div className="flow-connector">→</div>
                <div className="flow-step">
                  <div className="flow-icon score">85.4%</div>
                  <span>Ranked Candidate</span>
                </div>
              </div>

              <div className="mini-candidate-preview">
                <div className="mini-cand-header">
                  <div>
                    <strong>Rahul Sharma</strong>
                    <small>Java Backend Developer</small>
                  </div>
                  <span className="badge-strong">STRONG MATCH</span>
                </div>
                <div className="mini-algo-bars">
                  <div className="mini-bar-row">
                    <span>Skill Match (KMP)</span>
                    <strong>100%</strong>
                  </div>
                  <div className="mini-bar-row">
                    <span>String Matching</span>
                    <strong>86.4%</strong>
                  </div>
                  <div className="mini-bar-row">
                    <span>Sequence Alignment</span>
                    <strong>74.0%</strong>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Algorithms Showcase Grid */}
      <section className="features-section">
        <div className="section-title-center">
          <div className="section-pill">
            <Cpu size={14} /> Core Algorithmic Architecture
          </div>
          <h2>Implemented DSA Algorithms</h2>
          <p>Each algorithm serves a precise mathematical purpose in candidate similarity evaluation.</p>
        </div>

        <div className="features-grid">
          <div className="feature-card">
            <div className="feature-icon-box">1</div>
            <h3>KMP String Matching</h3>
            <p>Employs Longest Prefix Suffix (LPS) table in O(n + m) time to identify required skills and technologies with zero backtracking.</p>
            <code>Time: O(n + m) • Space: O(m)</code>
          </div>

          <div className="feature-card">
            <div className="feature-icon-box">2</div>
            <h3>Levenshtein Edit Distance</h3>
            <p>Computes minimum insertion, deletion, and substitution operations via Dynamic Programming to catch spelling variations (e.g. "Jvaa" → "Java").</p>
            <code>Time: O(m * n) • Space: O(m * n)</code>
          </div>

          <div className="feature-card">
            <div className="feature-icon-box">3</div>
            <h3>Needleman-Wunsch Alignment</h3>
            <p>Performs global sequence alignment between candidate career skill progression and job description requirement order with gap penalties.</p>
            <code>Time: O(m * n) • Space: O(m * n)</code>
          </div>

          <div className="feature-card">
            <div className="feature-icon-box">4</div>
            <h3>Suffix Array + Kasai LCP</h3>
            <p>Indexes text for O(m log n) substring queries and uses Kasai's algorithm to extract the longest common technical phrases shared between resume and JD.</p>
            <code>Time: O(n log n) + O(n)</code>
          </div>

          <div className="feature-card">
            <div className="feature-icon-box">5</div>
            <h3>SHA-256 Text Hashing</h3>
            <p>Computes 256-bit cryptographic digest of normalized candidate text for instantaneous O(1) exact duplicate detection and fraud prevention.</p>
            <code>Time: O(n) • Space: O(1)</code>
          </div>

          <div className="feature-card">
            <div className="feature-icon-box">6</div>
            <h3>Greedy Bipartite Approximation</h3>
            <p>Solves maximum-weight bipartite matching between candidate skills and job requirements in near-linear time with a guaranteed 1/2-approximation factor.</p>
            <code>Time: O(|C|*|R| log)</code>
          </div>
        </div>
      </section>

      {/* Call to Action Banner */}
      <section className="cta-banner">
        <div className="cta-banner-content">
          <h2>Ready to Screen Your Resumes?</h2>
          <p>Upload candidate PDF documents and let the Java DSA engine calculate rank, similarity, and duplicate checks.</p>
          <button className="primary-button large" onClick={() => setActivePage('Screen Resume')}>
            <span>Start Screening Pipeline</span>
            <ArrowRight size={18} />
          </button>
        </div>
      </section>
    </div>
  );
}
