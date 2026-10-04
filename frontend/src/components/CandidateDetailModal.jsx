import React, { useState } from 'react';
import {
  X,
  Check,
  FileText,
  Mail,
  Phone,
  AlertTriangle,
  Sparkles,
  UserCheck,
  Calendar,
  Clock,
  XCircle,
  MessageSquare,
  Save,
} from 'lucide-react';
import { getScoreColor, getRecommendationBadge } from '../utils/formatters';
import SkillBadge from './SkillBadge';

const STAGE_OPTIONS = [
  { id: 'Shortlisted', label: 'Shortlisted', color: '#10b981' },
  { id: 'Interview Scheduled', label: 'Interview Scheduled', color: '#3b82f6' },
  { id: 'Under Review', label: 'Under Review', color: '#f59e0b' },
  { id: 'Rejected', label: 'Rejected', color: '#ef4444' },
];

export default function CandidateDetailModal({
  candidate,
  onClose,
  currentStage,
  onUpdateStage,
  recruiterNotes = '',
  onSaveNotes,
}) {
  if (!candidate) return null;

  const score = candidate.overallScore || 0;
  const scoreColor = getScoreColor(score);
  const rec = getRecommendationBadge(candidate.recommendation);
  const matched = candidate.matchedSkills || [];
  const missing = candidate.missingSkills || [];
  const total = matched.length + missing.length || matched.length;
  const skillPercent = total > 0 ? (matched.length / total) * 100 : 0;

  const initialStage = currentStage || (
    score >= 70 ? 'Shortlisted' : score >= 55 ? 'Under Review' : 'Rejected'
  );
  const [selectedStage, setSelectedStage] = useState(initialStage);
  const [notes, setNotes] = useState(recruiterNotes);
  const [notesSaved, setNotesSaved] = useState(false);

  const handleStageChange = (e) => {
    const newStage = e.target.value;
    setSelectedStage(newStage);
    if (onUpdateStage) onUpdateStage(candidate.id, newStage);
  };

  const handleSaveNotes = () => {
    if (onSaveNotes) onSaveNotes(candidate.id, notes);
    setNotesSaved(true);
    setTimeout(() => setNotesSaved(false), 2000);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-container-modern" onClick={(e) => e.stopPropagation()}>
        {/* Header */}
        <div className="modal-header-modern">
          <div className="modal-identity">
            <div className="modal-rank-badge">#{candidate.rank || 1}</div>
            <div>
              <h2>{candidate.name}</h2>
              <span className="modal-file-info">
                <FileText size={13} /> {candidate.file || 'Resume.pdf'}
              </span>
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            <div className="modal-stage-selector-wrap">
              <label className="stage-select-label">Pipeline Stage:</label>
              <select
                value={selectedStage}
                onChange={handleStageChange}
                className="modal-stage-select"
              >
                {STAGE_OPTIONS.map((st) => (
                  <option key={st.id} value={st.id}>
                    {st.label}
                  </option>
                ))}
              </select>
            </div>

            <button className="modal-close-btn" onClick={onClose} aria-label="Close modal">
              <X size={20} />
            </button>
          </div>
        </div>

        {/* Modal Body */}
        <div className="modal-body-modern">
          {/* Top Summary Banner */}
          <div className="candidate-overview-card">
            <div className="overview-score-dial" style={{ borderColor: scoreColor }}>
              <span className="dial-value" style={{ color: scoreColor }}>
                {score.toFixed(0)}%
              </span>
              <span className="dial-label">Match Score</span>
            </div>

            <div className="overview-rec-info">
              <span className={`status-pill ${rec.class}`}>{rec.label}</span>
              <p className="overview-rec-text">
                {candidate.recommendation ||
                  'Candidate demonstrates strong alignment with job requirements.'}
              </p>
              <div className="overview-contact-chips">
                {candidate.email && candidate.email !== 'Not Found' && (
                  <span>
                    <Mail size={13} /> {candidate.email}
                  </span>
                )}
                {candidate.phone && candidate.phone !== 'Not Found' && (
                  <span>
                    <Phone size={13} /> {candidate.phone}
                  </span>
                )}
              </div>
            </div>
          </div>

          {/* Duplicate Alert if Flagged */}
          {candidate.isDuplicate && (
            <div className="modal-duplicate-warning">
              <AlertTriangle size={18} />
              <div>
                <strong>Duplicate Resume Notice</strong>
                <p>
                  {candidate.duplicateReason ||
                    'This resume shares high similarity with an existing candidate profile.'}
                </p>
              </div>
            </div>
          )}

          {/* Skills Breakdown */}
          <div className="modal-skills-section">
            <div className="modal-section-title">
              <h3>Required Skills Alignment</h3>
              <span>
                {matched.length} of {total} Skills Verified ({skillPercent.toFixed(0)}%)
              </span>
            </div>

            <div className="skills-comparison-columns">
              <div className="skills-column-box matched-box">
                <div className="column-title text-success">
                  <Check size={16} /> Matched Skills ({matched.length})
                </div>
                <div className="skills-flex-wrap">
                  {matched.length === 0 ? (
                    <span className="muted-italic">No required skills detected</span>
                  ) : (
                    matched.map((s, idx) => <SkillBadge key={idx} skill={s} type="matched" />)
                  )}
                </div>
              </div>

              <div className="skills-column-box missing-box">
                <div className="column-title text-danger">
                  <X size={16} /> Missing Requirements ({missing.length})
                </div>
                <div className="skills-flex-wrap">
                  {missing.length === 0 ? (
                    <span className="text-success" style={{ fontSize: 13, fontWeight: 500 }}>
                      ✓ All required skills matched!
                    </span>
                  ) : (
                    missing.map((s, idx) => (
                      <SkillBadge key={idx} skill={s} type="missing" />
                    ))
                  )}
                </div>
              </div>
            </div>
          </div>

          {/* Key Identified Keywords / Phrases */}
          {candidate.commonPhrases && candidate.commonPhrases.length > 0 && (
            <div className="modal-keywords-section">
              <div className="modal-section-title">
                <h3>Key Technical Terms Identified in Resume</h3>
              </div>
              <div className="keywords-chip-container">
                {candidate.commonPhrases.map((phrase, idx) => (
                  <span className="keyword-chip" key={idx}>
                    {phrase}
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* Internal Recruiter Notes */}
          <div className="modal-notes-section">
            <div className="modal-section-title">
              <h3>
                <MessageSquare size={15} style={{ display: 'inline', marginRight: 6 }} />
                Recruiter Notes &amp; Observations
              </h3>
              {notesSaved && <span className="text-success" style={{ fontSize: 12 }}>Saved!</span>}
            </div>
            <textarea
              className="modal-notes-textarea"
              rows={3}
              placeholder="Add interview notes, feedback from technical leads, or salary expectations..."
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
            />
            <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: 8 }}>
              <button
                type="button"
                className="secondary-button compact"
                onClick={handleSaveNotes}
              >
                <Save size={13} />
                <span>Save Notes</span>
              </button>
            </div>
          </div>
        </div>

        {/* Modal Footer */}
        <div className="modal-footer-modern">
          <button className="primary-button" onClick={onClose}>
            Done
          </button>
        </div>
      </div>
    </div>
  );
}
