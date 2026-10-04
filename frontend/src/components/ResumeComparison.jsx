import React from 'react';
import { getScoreColor } from '../utils/formatters';
import { Award, FileText, Check, X } from 'lucide-react';
import SkillBadge from './SkillBadge';

export default function ResumeComparison({ candidates = [] }) {
  if (!candidates || candidates.length === 0) {
    return (
      <div className="empty-comparison-state">
        <p>No candidates selected for comparison. Select candidates from the list above or from the Candidates page.</p>
      </div>
    );
  }

  // Identify highest scoring candidate
  const topCandidate = candidates.reduce((prev, curr) =>
    (curr.overallScore > prev.overallScore) ? curr : prev, candidates[0]
  );

  return (
    <div className="modern-comparison-container">
      {candidates.length > 1 && topCandidate && (
        <div className="top-candidate-callout">
          <div className="callout-icon">
            <Award size={20} />
          </div>
          <div>
            <h4>Top Match: {topCandidate.name} ({topCandidate.overallScore?.toFixed(0)}%)</h4>
            <p>Demonstrates the highest overall qualification alignment with the current job criteria.</p>
          </div>
        </div>
      )}

      <div className="comparison-grid-table-wrap">
        <table className="modern-compare-table">
          <thead>
            <tr>
              <th className="th-criteria">Criteria</th>
              {candidates.map((c) => (
                <th key={c.id} className={`th-candidate ${c.id === topCandidate.id ? 'is-leader' : ''}`}>
                  <div className="candidate-col-head">
                    <span className="col-rank-tag">#{c.rank || 1}</span>
                    <strong className="col-name">{c.name}</strong>
                    <span className="col-file">
                      <FileText size={11} /> {c.file || 'Resume.pdf'}
                    </span>
                  </div>
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {/* Overall Score */}
            <tr className="tr-highlight">
              <td className="td-label">
                <strong>Overall Match Score</strong>
              </td>
              {candidates.map((c) => {
                const color = getScoreColor(c.overallScore || 0);
                return (
                  <td key={c.id} className="td-val">
                    <div className="compare-score-pill" style={{ color, backgroundColor: `${color}15` }}>
                      {c.overallScore?.toFixed(0)}%
                    </div>
                  </td>
                );
              })}
            </tr>

            {/* Skills Matched Count */}
            <tr>
              <td className="td-label">
                <strong>Skills Matched</strong>
              </td>
              {candidates.map((c) => {
                const matchedCount = c.matchedSkills?.length || 0;
                const totalCount = matchedCount + (c.missingSkills?.length || 0);
                return (
                  <td key={c.id} className="td-val">
                    <strong>{matchedCount} of {totalCount || matchedCount}</strong>
                  </td>
                );
              })}
            </tr>

            {/* Matched Skills List */}
            <tr>
              <td className="td-label">
                <strong>Verified Skills</strong>
              </td>
              {candidates.map((c) => (
                <td key={c.id} className="td-val">
                  <div className="compare-skills-list">
                    {(c.matchedSkills || []).map((skill, idx) => (
                      <SkillBadge key={idx} skill={skill} type="matched" />
                    ))}
                    {(c.matchedSkills || []).length === 0 && (
                      <span className="muted-text">None</span>
                    )}
                  </div>
                </td>
              ))}
            </tr>

            {/* Missing Skills List */}
            <tr>
              <td className="td-label">
                <strong>Missing Requirements</strong>
              </td>
              {candidates.map((c) => (
                <td key={c.id} className="td-val">
                  <div className="compare-skills-list">
                    {(c.missingSkills || []).map((skill, idx) => (
                      <SkillBadge key={idx} skill={skill} type="missing" />
                    ))}
                    {(c.missingSkills || []).length === 0 && (
                      <span className="text-success" style={{ fontSize: 13, fontWeight: 600 }}>
                        All matched
                      </span>
                    )}
                  </div>
                </td>
              ))}
            </tr>

            {/* Candidate Recommendation */}
            <tr>
              <td className="td-label">
                <strong>Recruiter Recommendation</strong>
              </td>
              {candidates.map((c) => (
                <td key={c.id} className="td-val">
                  <p className="compare-rec-text">{c.recommendation}</p>
                </td>
              ))}
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
}
