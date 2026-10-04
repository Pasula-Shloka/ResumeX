import React from 'react';
import { CopyCheck, AlertTriangle, CheckCircle2, FileText, ArrowRight } from 'lucide-react';
import { formatScore, getScoreColor } from '../utils/formatters';

export default function DuplicateDetection({ duplicates = {} }) {
  const exactDuplicates = duplicates.exactDuplicates || [];
  const nearDuplicates = duplicates.nearDuplicates || [];
  const totalDuplicates = duplicates.totalDuplicateCount || (exactDuplicates.length + nearDuplicates.length);

  return (
    <div className="duplicates-page-container">
      {/* Top Banner */}
      <div className="page-header-row">
        <div>
          <h3>Duplicate Resume Audit</h3>
          <p>Detect identical submissions and high-similarity candidate resumes to eliminate duplicate applicants.</p>
        </div>
      </div>

      {totalDuplicates === 0 ? (
        <div className="empty-state-panel">
          <CheckCircle2 size={36} className="text-success" style={{ marginBottom: 12 }} />
          <h4>No Duplicate Resumes Detected</h4>
          <p>All currently loaded candidate resumes are unique within the database.</p>
        </div>
      ) : (
        <div className="duplicates-list-stack">
          {exactDuplicates.map((dup, idx) => (
            <article key={`exact-${idx}`} className="clean-dup-card exact">
              <div className="dup-badge-row">
                <span className="dup-flag-tag exact">100% Exact Duplicate</span>
                <span className="dup-subtext">Identical resume text detected</span>
              </div>

              <div className="dup-profiles-comparison">
                <div className="dup-profile-box">
                  <small>Original Submission</small>
                  <strong>{dup.original}</strong>
                  <span className="file-pill">
                    <FileText size={12} /> {dup.file || "Primary Resume.pdf"}
                  </span>
                </div>

                <div className="dup-connector-arrow">
                  <ArrowRight size={18} />
                </div>

                <div className="dup-profile-box duplicate">
                  <small>Duplicate Submission</small>
                  <strong>{dup.duplicate}</strong>
                  <span className="file-pill">
                    <FileText size={12} /> {dup.file || "Duplicate.pdf"}
                  </span>
                </div>
              </div>

              <div className="dup-card-actions">
                <button type="button" className="secondary-button compact">
                  Keep Original Only
                </button>
                <button type="button" className="text-button compact">
                  Dismiss Alert
                </button>
              </div>
            </article>
          ))}

          {nearDuplicates.map((dup, idx) => (
            <article key={`near-${idx}`} className="clean-dup-card near">
              <div className="dup-badge-row">
                <span className="dup-flag-tag near">{formatScore(dup.similarity)} Similar</span>
                <span className="dup-subtext">High textual similarity — likely an updated version</span>
              </div>

              <div className="dup-profiles-comparison">
                <div className="dup-profile-box">
                  <small>Profile A</small>
                  <strong>{dup.candidateA}</strong>
                  <span className="file-pill">
                    <FileText size={12} /> {dup.fileA || "Resume.pdf"}
                  </span>
                </div>

                <div className="dup-connector-arrow">
                  <ArrowRight size={18} />
                </div>

                <div className="dup-profile-box duplicate">
                  <small>Profile B</small>
                  <strong>{dup.candidateB}</strong>
                  <span className="file-pill">
                    <FileText size={12} /> {dup.fileB || "Resume.pdf"}
                  </span>
                </div>
              </div>

              <div className="dup-card-actions">
                <button type="button" className="secondary-button compact">
                  Compare Both Resumes
                </button>
                <button type="button" className="text-button compact">
                  Dismiss
                </button>
              </div>
            </article>
          ))}
        </div>
      )}
    </div>
  );
}
