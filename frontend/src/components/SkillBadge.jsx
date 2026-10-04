import React from 'react';
import { Check, X } from 'lucide-react';

export default function SkillBadge({ skill, type = 'default' }) {
  if (type === 'matched') {
    return (
      <span className="skill-badge matched" title="Matched Skill">
        <Check size={12} className="skill-icon" />
        <span>{skill}</span>
      </span>
    );
  }

  if (type === 'missing') {
    return (
      <span className="skill-badge missing" title="Missing Required Skill">
        <X size={12} className="skill-icon" />
        <span>{skill}</span>
      </span>
    );
  }

  return (
    <span className="skill-badge default">
      {skill}
    </span>
  );
}
