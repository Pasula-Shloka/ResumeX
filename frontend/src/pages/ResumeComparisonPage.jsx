import React from 'react';
import { Scale, Users, CheckSquare, Square, ArrowLeft } from 'lucide-react';
import ResumeComparison from '../components/ResumeComparison';

export default function ResumeComparisonPage({
  candidates = [],
  comparedIds = [],
  setComparedIds,
  setActivePage,
}) {
  // If no candidates selected yet, pick the first 3 (or all if < 3)
  const effectiveComparedIds = comparedIds.length > 0
    ? comparedIds
    : candidates.slice(0, 3).map(c => c.id);

  const toggleCandidateSelection = (id) => {
    if (effectiveComparedIds.includes(id)) {
      setComparedIds(effectiveComparedIds.filter(i => i !== id));
    } else {
      setComparedIds([...effectiveComparedIds, id]);
    }
  };

  const selectedCandidates = candidates.filter((c) => effectiveComparedIds.includes(c.id));

  return (
    <div className="comparison-page-container">
      <div className="page-header-row">
        <div>
          <h3>Side-by-Side Candidate Comparison</h3>
          <p>Comparing {selectedCandidates.length} candidate profile(s) across qualifications and required skills.</p>
        </div>
        <button className="secondary-button" onClick={() => setActivePage('Candidates')}>
          <ArrowLeft size={15} /> Back to Candidates
        </button>
      </div>

      {/* Candidate Checkbox Chips */}
      <div className="compare-filter-chips-card">
        <span className="chips-label">
          <Users size={15} /> Select Candidates ({selectedCandidates.length} selected):
        </span>
        <div className="chips-flex-wrap">
          {candidates.map((c) => {
            const isSelected = effectiveComparedIds.includes(c.id);
            return (
              <button
                key={c.id}
                type="button"
                className={`candidate-chip-btn ${isSelected ? 'active' : ''}`}
                onClick={() => toggleCandidateSelection(c.id)}
              >
                {isSelected ? <CheckSquare size={14} /> : <Square size={14} />}
                <span className="chip-name">{c.name}</span>
                <span className="chip-score">{c.overallScore?.toFixed(0)}%</span>
              </button>
            );
          })}
        </div>
      </div>

      {/* Comparison Matrix */}
      <ResumeComparison candidates={selectedCandidates} />
    </div>
  );
}
