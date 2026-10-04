import React, { useState } from 'react';
import {
  Kanban,
  FileText,
  CheckCircle2,
  ChevronRight,
  ChevronLeft,
  Eye,
  Filter,
  Search,
  UserCheck,
  Calendar,
  XCircle,
  Clock,
} from 'lucide-react';
import { getScoreColor } from '../utils/formatters';

const STAGES = [
  { id: 'Shortlisted', title: 'Shortlisted', color: '#10b981', icon: UserCheck, desc: 'High match candidates' },
  { id: 'Interview Scheduled', title: 'Interview', color: '#3b82f6', icon: Calendar, desc: 'Technical & HR rounds' },
  { id: 'Under Review', title: 'Under Review', color: '#f59e0b', icon: Clock, desc: 'Awaiting team evaluation' },
  { id: 'Rejected', title: 'Rejected', color: '#ef4444', icon: XCircle, desc: 'Skill / experience gap' },
];

export default function HiringPipeline({
  candidates = [],
  candidateStages = {},
  onUpdateCandidateStage,
  onViewCandidate,
}) {
  const [searchQuery, setSearchQuery] = useState('');

  // Default stage mapper if not explicitly overridden by recruiter
  const getStageForCandidate = (c) => {
    if (candidateStages[c.id]) return candidateStages[c.id];
    const score = c.overallScore || 0;
    if (score >= 70) return 'Shortlisted';
    if (score >= 55) return 'Under Review';
    return 'Rejected';
  };

  // Filter candidates
  const filteredCandidates = candidates.filter((c) => {
    const q = searchQuery.toLowerCase();
    return (
      (c.name || '').toLowerCase().includes(q) ||
      (c.file || '').toLowerCase().includes(q) ||
      (c.skills || []).some((s) => s.toLowerCase().includes(q))
    );
  });

  // Group candidates into stages
  const grouped = {
    'Shortlisted': [],
    'Interview Scheduled': [],
    'Under Review': [],
    'Rejected': [],
  };

  filteredCandidates.forEach((c) => {
    const stage = getStageForCandidate(c);
    if (grouped[stage]) {
      grouped[stage].push(c);
    } else {
      grouped['Under Review'].push(c);
    }
  });

  const advanceStage = (candidateId, currentStage) => {
    const stageIds = STAGES.map((s) => s.id);
    const currIdx = stageIds.indexOf(currentStage);
    if (currIdx < stageIds.length - 1) {
      onUpdateCandidateStage(candidateId, stageIds[currIdx + 1]);
    }
  };

  const regressStage = (candidateId, currentStage) => {
    const stageIds = STAGES.map((s) => s.id);
    const currIdx = stageIds.indexOf(currentStage);
    if (currIdx > 0) {
      onUpdateCandidateStage(candidateId, stageIds[currIdx - 1]);
    }
  };

  return (
    <div className="pipeline-page-container">
      {/* Top Header */}
      <div className="page-header-row">
        <div>
          <h2>Hiring Pipeline &amp; Stage Tracker</h2>
          <p>Track screened candidates across interview stages and recruitment milestones.</p>
        </div>

        <div className="search-input-wrapper" style={{ maxWidth: 320 }}>
          <Search size={16} />
          <input
            type="text"
            placeholder="Search candidate in pipeline..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      {/* Kanban Board Columns */}
      <div className="kanban-columns-row">
        {STAGES.map((col) => {
          const items = grouped[col.id] || [];
          const ColIcon = col.icon;

          return (
            <div key={col.id} className="kanban-column">
              <div className="kanban-column-header">
                <div className="column-title-group">
                  <span className="col-status-indicator" style={{ backgroundColor: col.color }} />
                  <ColIcon size={16} style={{ color: col.color }} />
                  <span className="col-heading">{col.title}</span>
                </div>
                <span className="col-count-badge">{items.length}</span>
              </div>

              <div className="kanban-cards-stack">
                {items.length === 0 ? (
                  <div className="kanban-empty-drop">
                    <span>No candidates in this stage</span>
                  </div>
                ) : (
                  items.map((c) => {
                    const score = c.overallScore || 0;
                    const scoreColor = getScoreColor(score);
                    const matchedCount = c.matchedSkills?.length || 0;
                    const totalSkills = matchedCount + (c.missingSkills?.length || 0);

                    return (
                      <div key={c.id} className="pipeline-card">
                        <div className="pipeline-card-top">
                          <div className="p-card-identity">
                            <span className="p-card-rank">#{c.rank || 1}</span>
                            <div>
                              <strong className="p-card-name">{c.name}</strong>
                              <span className="p-card-file">
                                <FileText size={11} /> {c.file}
                              </span>
                            </div>
                          </div>
                          <div
                            className="p-card-score"
                            style={{ color: scoreColor, backgroundColor: `${scoreColor}15` }}
                          >
                            {score.toFixed(0)}%
                          </div>
                        </div>

                        <div className="p-card-skills-row">
                          <span className="p-card-skills-count">
                            <CheckCircle2 size={12} className="text-success" />
                            {matchedCount} / {totalSkills || matchedCount} Skills
                          </span>

                          <button
                            type="button"
                            className="p-card-view-btn"
                            onClick={() => onViewCandidate(c)}
                            title="View candidate details"
                          >
                            <Eye size={13} /> View
                          </button>
                        </div>

                        {/* Stage Mover Controls */}
                        <div className="p-card-stage-actions">
                          <select
                            value={col.id}
                            onChange={(e) => onUpdateCandidateStage(c.id, e.target.value)}
                            className="p-card-stage-select"
                          >
                            {STAGES.map((s) => (
                              <option key={s.id} value={s.id}>
                                Move: {s.title}
                              </option>
                            ))}
                          </select>
                        </div>
                      </div>
                    );
                  })
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
