import React from 'react';
import { AlertCircle, CopyCheck, FileText, ArrowRight } from 'lucide-react';
import { formatScore, getScoreColor } from '../utils/formatters';

export default function DuplicateCard({ duplicate, type = 'near' }) {
  const isExact = type === 'exact' || duplicate.similarity === 100;
  const sim = duplicate.similarity || 100;
  const simColor = getScoreColor(sim);

  return (
    <article className={`duplicate-card-v2 ${isExact ? 'exact-match' : 'near-match'}`}>
      <div className="dup-header">
        <div className="dup-icon-badge">
          {isExact ? <CopyCheck size={18} /> : <AlertCircle size={18} />}
        </div>
        <div className="dup-title-block">
          <h4>{isExact ? "Exact Duplicate Resume Detected" : "High Similarity Near-Duplicate Found"}</h4>
          <span className="dup-method-pill">{duplicate.method || (isExact ? "SHA-256 Hash Fingerprint" : "Levenshtein Edit Distance")}</span>
        </div>
        <div className="dup-score-badge" style={{ color: simColor, backgroundColor: `${simColor}18` }}>
          <strong>{formatScore(sim)}</strong>
        </div>
      </div>

      <div className="dup-comparison-row">
        <div className="dup-file-box original">
          <small>Original Candidate</small>
          <div className="dup-file-name">
            <FileText size={15} />
            <span>{duplicate.original || duplicate.candidateA || "Primary Resume"}</span>
          </div>
          {duplicate.file && <span className="dup-file-meta">{duplicate.file}</span>}
          {duplicate.fileA && <span className="dup-file-meta">{duplicate.fileA}</span>}
        </div>

        <div className="dup-arrow">
          <ArrowRight size={18} />
        </div>

        <div className="dup-file-box duplicate">
          <small>Duplicate / Similar Candidate</small>
          <div className="dup-file-name">
            <FileText size={15} />
            <span>{duplicate.duplicate || duplicate.candidateB || "Matching Resume"}</span>
          </div>
          {duplicate.fileB && <span className="dup-file-meta">{duplicate.fileB}</span>}
        </div>
      </div>

      <div className="dup-footer-hint">
        {isExact ? (
          <p>The normalized text of these documents generated identical cryptographic SHA-256 digests. Only one candidate record should proceed in the recruitment pipeline.</p>
        ) : (
          <p>These candidates share an exceptionally high Levenshtein text similarity ({formatScore(sim)}). Verify if this is an updated submission of the same candidate portfolio.</p>
        )}
      </div>
    </article>
  );
}
