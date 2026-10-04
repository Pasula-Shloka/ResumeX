import React from 'react';
import { getScoreColor, getRecommendationBadge } from '../utils/formatters';
import SkillBadge from './SkillBadge';
import { Eye, Check, AlertTriangle, FileText, CheckSquare, Square } from 'lucide-react';

export default function CandidateCard({
  candidate,
  onViewDetails,
  onCompareToggle,
  isCompared = false,
  onStatusChange,
}) {
  const score = candidate.overallScore || 0;
  const scoreColor = getScoreColor(score);
  const rec = getRecommendationBadge(candidate.recommendation);
  const matched = candidate.matchedSkills || [];
  const missing = candidate.missingSkills || [];
  const total = matched.length + missing.length || matched.length;

  return (
    <article className={`candidate-card-v3 ${isCompared ? 'selected-for-compare' : ''}`}>
      <div className="card-top-bar">
        <div className="candidate-identity">
          <span className="rank-indicator">#{candidate.rank || 1}</span>
          <div>
            <h3 className="candidate-full-name">{candidate.name}</h3>
            <span className="candidate-file-name">
              <FileText size={12} /> {candidate.file || 'Resume.pdf'}
            </span>
          </div>
        </div>

        <div className="card-match-badge" style={{ borderColor: scoreColor, color: scoreColor }}>
          <span className="score-num">{score.toFixed(0)}%</span>
          <span className="score-lbl">Match</span>
        </div>
      </div>

      <div className="card-tags-row">
        <span className={`status-pill ${rec.class}`}>
          {rec.label}
        </span>
        {candidate.isDuplicate && (
          <span className="duplicate-warning-pill" title={candidate.duplicateReason}>
            <AlertTriangle size={12} /> Duplicate Detected
          </span>
        )}
      </div>

      <div className="card-skills-block">
        <div className="skills-stat-header">
          <span className="skills-stat-title">Skills Alignment</span>
          <strong className="skills-stat-count">
            {matched.length} of {total} Matched
          </strong>
        </div>

        <div className="skills-chips-flow">
          {matched.slice(0, 4).map((skill, idx) => (
            <SkillBadge key={idx} skill={skill} type="matched" />
          ))}
          {matched.length > 4 && (
            <span className="skills-extra-tag">+{matched.length - 4} more</span>
          )}
          {missing.slice(0, 2).map((skill, idx) => (
            <SkillBadge key={`miss-${idx}`} skill={skill} type="missing" />
          ))}
        </div>
      </div>

      <div className="card-bottom-actions">
        <button
          type="button"
          className={`compare-select-btn ${isCompared ? 'active' : ''}`}
          onClick={() => onCompareToggle && onCompareToggle(candidate.id)}
        >
          {isCompared ? <CheckSquare size={15} /> : <Square size={15} />}
          <span>{isCompared ? 'Selected' : 'Compare'}</span>
        </button>

        <button
          type="button"
          className="btn-view-profile"
          onClick={() => onViewDetails && onViewDetails(candidate)}
        >
          <Eye size={14} />
          <span>View Profile</span>
        </button>
      </div>
    </article>
  );
}
