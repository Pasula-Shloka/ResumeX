import React from 'react';

export default function ScoreCard({ title, value, delta, icon: Icon, color }) {
  return (
    <article className="stat-card">
      <div className="stat-top">
        <span>{title}</span>
        {Icon && (
          <div className="stat-icon-wrapper" style={color ? { color, backgroundColor: `${color}15` } : {}}>
            <Icon size={18} />
          </div>
        )}
      </div>
      <strong>{value}</strong>
      {delta && <small>{delta}</small>}
    </article>
  );
}
