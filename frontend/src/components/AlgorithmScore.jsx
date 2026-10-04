import React from 'react';
import { getScoreColor } from '../utils/formatters';

export default function AlgorithmScore({ name, score, weight, complexity, use, compact = false }) {
  const color = getScoreColor(score);

  if (compact) {
    return (
      <div className="algo-score-compact">
        <div className="algo-score-header">
          <span>{name}</span>
          <strong style={{ color }}>{score.toFixed(1)}%</strong>
        </div>
        <div className="progress-track">
          <div className="progress-fill" style={{ width: `${Math.min(100, score)}%`, backgroundColor: color }} />
        </div>
      </div>
    );
  }

  return (
    <div className="algorithm-card">
      <div className="algorithm-top">
        <div>
          <h3>{name}</h3>
          {weight && <span className="algo-weight">Weight: {weight}%</span>}
        </div>
        <div className="algo-score-badge" style={{ backgroundColor: `${color}18`, color }}>
          <strong>{score.toFixed(1)}%</strong>
        </div>
      </div>

      <div className="progress-track" style={{ margin: '10px 0' }}>
        <div className="progress-fill" style={{ width: `${Math.min(100, score)}%`, backgroundColor: color }} />
      </div>

      {use && <p className="algo-use">{use}</p>}

      {complexity && (
        <div className="code-row">
          <span>Complexity</span>
          <code>{complexity}</code>
        </div>
      )}
    </div>
  );
}
