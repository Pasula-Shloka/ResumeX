import React, { useState } from 'react';
import {
  Briefcase,
  Plus,
  Users,
  SearchCheck,
  MapPin,
  Building2,
  CheckCircle2,
  X,
  FileText,
  ArrowRight,
  Sparkles,
} from 'lucide-react';
import { api } from '../services/api';

export default function JobOpenings({
  jobs = [],
  setJobs,
  candidates = [],
  onSelectJobForScreening,
  setActivePage,
}) {
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    department: 'Engineering',
    location: 'Hybrid / Remote',
    skillsInput: '',
    description: '',
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  const handleCreateJob = async (e) => {
    e.preventDefault();
    if (!formData.title.trim()) {
      setErrorMsg('Please enter a role title.');
      return;
    }

    const skills = formData.skillsInput
      .split(',')
      .map((s) => s.trim())
      .filter((s) => s.length > 0);

    if (skills.length === 0) {
      setErrorMsg('Please specify at least one required skill.');
      return;
    }

    setIsSubmitting(true);
    setErrorMsg('');

    const newJobObj = {
      id: Date.now(),
      title: formData.title.trim(),
      department: formData.department.trim(),
      location: formData.location.trim(),
      description: formData.description.trim() || formData.title.trim(),
      required: skills,
      requiredSkills: skills,
    };

    try {
      // Call Java backend to save opening
      await api.createJob(newJobObj).catch((err) => {
        console.warn('Backend job create warning (local state will still update):', err);
      });

      setJobs((prev) => [newJobObj, ...prev]);
      setShowCreateModal(false);
      setFormData({
        title: '',
        department: 'Engineering',
        location: 'Hybrid / Remote',
        skillsInput: '',
        description: '',
      });
    } catch (err) {
      setErrorMsg(err.message || 'Failed to save job opening');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="job-openings-page">
      {/* Header Row */}
      <div className="page-header-row">
        <div>
          <h2>Job Openings &amp; Requisitions</h2>
          <p>Manage open positions, configure required skills, and initiate resume screening.</p>
        </div>
        <button
          type="button"
          className="primary-button"
          onClick={() => {
            setErrorMsg('');
            setShowCreateModal(true);
          }}
        >
          <Plus size={16} />
          <span>New Job Opening</span>
        </button>
      </div>

      {/* Quick Summary Stats */}
      <div className="openings-stats-grid">
        <div className="stat-card-v3">
          <span className="stat-label">Active Requisitions</span>
          <div className="stat-value-group">
            <span className="stat-number">{jobs.length}</span>
            <span className="stat-sub">Open Positions</span>
          </div>
        </div>

        <div className="stat-card-v3">
          <span className="stat-label">Total Applicants Screened</span>
          <div className="stat-value-group">
            <span className="stat-number">{candidates.length}</span>
            <span className="stat-sub">Candidate Profiles</span>
          </div>
        </div>

        <div className="stat-card-v3">
          <span className="stat-label">Average Match Rate</span>
          <div className="stat-value-group">
            <span className="stat-number text-success">
              {candidates.length > 0
                ? (
                    candidates.reduce((sum, c) => sum + (c.overallScore || 0), 0) /
                    candidates.length
                  ).toFixed(1)
                : '0.0'}
              %
            </span>
            <span className="stat-sub">Across active pool</span>
          </div>
        </div>
      </div>

      {/* Requisitions Grid */}
      <div className="openings-list-grid">
        {jobs.map((job) => {
          const reqSkills = job.required || job.requiredSkills || [];
          return (
            <div key={job.id} className="job-card-v3">
              <div className="job-card-header">
                <div>
                  <h3 className="job-title">{job.title}</h3>
                  <div className="job-meta-chips">
                    <span className="job-meta-chip">
                      <Building2 size={12} /> {job.department || 'Engineering'}
                    </span>
                    <span className="job-meta-chip">
                      <MapPin size={12} /> {job.location || 'Remote / Hybrid'}
                    </span>
                  </div>
                </div>
                <span className="badge-active-status">Active</span>
              </div>

              <p className="job-description-preview">
                {job.description || 'No detailed description provided.'}
              </p>

              <div className="job-skills-section">
                <span className="job-skills-title">Required Skill Stack ({reqSkills.length}):</span>
                <div className="job-skills-chips">
                  {reqSkills.slice(0, 6).map((skill, idx) => (
                    <span key={idx} className="skill-chip-neutral">
                      {skill}
                    </span>
                  ))}
                  {reqSkills.length > 6 && (
                    <span className="skill-chip-more">+{reqSkills.length - 6} more</span>
                  )}
                </div>
              </div>

              <div className="job-card-actions">
                <button
                  type="button"
                  className="primary-button compact"
                  onClick={() => {
                    if (onSelectJobForScreening) onSelectJobForScreening(job);
                    setActivePage('Screen Resumes');
                  }}
                >
                  <SearchCheck size={14} />
                  <span>Screen Resumes</span>
                </button>

                <button
                  type="button"
                  className="secondary-button compact"
                  onClick={() => setActivePage('Candidates')}
                >
                  <Users size={14} />
                  <span>View Candidates</span>
                </button>
              </div>
            </div>
          );
        })}
      </div>

      {/* Create Job Modal */}
      {showCreateModal && (
        <div className="modal-overlay" onClick={() => setShowCreateModal(false)}>
          <div className="modal-container-modern modal-sm" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header-modern">
              <div className="modal-identity">
                <div className="modal-rank-badge">
                  <Briefcase size={16} />
                </div>
                <div>
                  <h2>Create New Job Opening</h2>
                  <span className="modal-file-info">Define job requirements and skill criteria</span>
                </div>
              </div>
              <button
                className="modal-close-btn"
                onClick={() => setShowCreateModal(false)}
                aria-label="Close"
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleCreateJob} className="modal-body-modern">
              {errorMsg && <div className="alert-danger-box">{errorMsg}</div>}

              <div className="form-group-v3">
                <label>Job Title *</label>
                <input
                  type="text"
                  placeholder="e.g. Senior Java Backend Engineer"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  required
                />
              </div>

              <div className="form-row-2col">
                <div className="form-group-v3">
                  <label>Department</label>
                  <input
                    type="text"
                    placeholder="e.g. Engineering, AI / Data"
                    value={formData.department}
                    onChange={(e) => setFormData({ ...formData, department: e.target.value })}
                  />
                </div>

                <div className="form-group-v3">
                  <label>Location</label>
                  <input
                    type="text"
                    placeholder="e.g. Hyderabad / Remote"
                    value={formData.location}
                    onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                  />
                </div>
              </div>

              <div className="form-group-v3">
                <label>Required Skills (comma separated) *</label>
                <input
                  type="text"
                  placeholder="e.g. Java, Spring Boot, SQL, Docker, REST API, Git"
                  value={formData.skillsInput}
                  onChange={(e) => setFormData({ ...formData, skillsInput: e.target.value })}
                  required
                />
                <small className="field-hint">
                  The Java screening engine will match resumes specifically against these skills.
                </small>
              </div>

              <div className="form-group-v3">
                <label>Job Description Summary</label>
                <textarea
                  rows={3}
                  placeholder="Briefly describe the candidate expectations, responsibilities, and qualifications..."
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                />
              </div>

              <div className="modal-footer-modern">
                <button
                  type="button"
                  className="secondary-button"
                  onClick={() => setShowCreateModal(false)}
                  disabled={isSubmitting}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="primary-button"
                  disabled={isSubmitting}
                >
                  {isSubmitting ? 'Saving...' : 'Create Opening'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
