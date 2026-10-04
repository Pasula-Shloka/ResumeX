import React, { useState } from 'react';
import { getScoreColor, getRecommendationBadge } from '../utils/formatters';
import { Eye, ArrowUpDown, FileText, CheckCircle2, XCircle } from 'lucide-react';

export default function RankingTable({ candidates = [], onViewCandidate }) {
  const [sortKey, setSortKey] = useState('overallScore');
  const [sortAsc, setSortAsc] = useState(false);

  const handleSort = (key) => {
    if (sortKey === key) {
      setSortAsc(!sortAsc);
    } else {
      setSortKey(key);
      setSortAsc(false);
    }
  };

  const sortedCandidates = [...candidates].sort((a, b) => {
    let valA = a[sortKey];
    let valB = b[sortKey];

    if (sortKey === 'matchedCount') {
      valA = a.matchedSkills?.length || 0;
      valB = b.matchedSkills?.length || 0;
    }

    if (typeof valA === 'string') {
      return sortAsc ? valA.localeCompare(valB) : valB.localeCompare(valA);
    }
    return sortAsc ? (valA || 0) - (valB || 0) : (valB || 0) - (valA || 0);
  });

  return (
    <div className="table-responsive">
      <table className="ranking-table">
        <thead>
          <tr>
            <th onClick={() => handleSort('rank')}>
              Rank <ArrowUpDown size={12} />
            </th>
            <th onClick={() => handleSort('name')}>
              Candidate <ArrowUpDown size={12} />
            </th>
            <th onClick={() => handleSort('overallScore')}>
              Match Score <ArrowUpDown size={12} />
            </th>
            <th onClick={() => handleSort('matchedCount')}>
              Skills Matched <ArrowUpDown size={12} />
            </th>
            <th>Missing Skills</th>
            <th>Recommendation</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {sortedCandidates.length === 0 ? (
            <tr>
              <td colSpan={7} className="empty-table-cell">
                No candidates available.
              </td>
            </tr>
          ) : (
            sortedCandidates.map((c, index) => {
              const score = c.overallScore || 0;
              const color = getScoreColor(score);
              const rec = getRecommendationBadge(c.recommendation);
              const matchedCount = c.matchedSkills?.length || 0;
              const missingCount = c.missingSkills?.length || 0;
              const totalSkills = matchedCount + missingCount;

              return (
                <tr key={c.id || index} className="ranking-row">
                  <td className="rank-cell">
                    <span className="rank-tag">#{c.rank || index + 1}</span>
                  </td>
                  <td className="candidate-info-cell">
                    <div className="candidate-name-row">
                      <strong>{c.name}</strong>
                    </div>
                    <small className="file-subtext">
                      <FileText size={12} style={{ display: 'inline', marginRight: 4 }} />
                      {c.file}
                    </small>
                  </td>
                  <td>
                    <div className="table-score-badge" style={{ color, backgroundColor: `${color}16` }}>
                      {score.toFixed(1)}%
                    </div>
                  </td>
                  <td>
                    <div className="skills-match-summary">
                      <span className="skills-count-pill">
                        <CheckCircle2 size={13} className="text-success" />
                        {matchedCount} / {totalSkills || matchedCount}
                      </span>
                      <span className="skills-pct-sub">({c.skillMatchPercentage?.toFixed(0) || '0'}%)</span>
                    </div>
                  </td>
                  <td>
                    <div className="table-missing-skills">
                      {c.missingSkills && c.missingSkills.length > 0 ? (
                        c.missingSkills.slice(0, 3).map((s, idx) => (
                          <span key={idx} className="missing-skill-pill">
                            {s}
                          </span>
                        ))
                      ) : (
                        <span className="all-skills-matched-pill">All matched</span>
                      )}
                      {c.missingSkills && c.missingSkills.length > 3 && (
                        <span className="more-skills-pill">+{c.missingSkills.length - 3}</span>
                      )}
                    </div>
                  </td>
                  <td>
                    <span className={`recommendation-tag ${rec.class}`}>
                      {rec.label}
                    </span>
                  </td>
                  <td>
                    <button
                      className="table-action-button"
                      onClick={() => onViewCandidate(c)}
                      title="View candidate profile"
                    >
                      <Eye size={14} /> View
                    </button>
                  </td>
                </tr>
              );
            })
          )}
        </tbody>
      </table>
    </div>
  );
}
