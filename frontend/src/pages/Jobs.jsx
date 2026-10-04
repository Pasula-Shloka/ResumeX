import React, { useState } from 'react';
import { BriefcaseBusiness, Plus, Search, Archive, MoreHorizontal, CheckCircle2 } from 'lucide-react';
import SkillBadge from '../components/SkillBadge';
import { api } from '../services/api';

export default function Jobs({ jobs = [], setJobs, setActivePage }) {
  const [query, setQuery] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    department: 'Engineering',
    location: 'Hyderabad / Hybrid',
    description: '',
    requiredSkills: '',
  });

  const filteredJobs = jobs.filter((j) =>
    (j.title || '').toLowerCase().includes(query.toLowerCase()) ||
    (j.description || '').toLowerCase().includes(query.toLowerCase())
  );

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.title.trim()) return;

    const skillsArray = formData.requiredSkills
      .split(',')
      .map(s => s.trim())
      .filter(s => s.length > 0);

    const newJobPayload = {
      title: formData.title,
      description: formData.description || formData.title,
      requiredSkills: skillsArray,
    };

    try {
      const res = await api.createJob(newJobPayload);
      if (res.job) {
        setJobs([res.job, ...jobs]);
      }
      setShowModal(false);
      setFormData({ title: '', department: 'Engineering', location: 'Hyderabad / Hybrid', description: '', requiredSkills: '' });
    } catch (err) {
      alert(`Could not save job: ${err.message}`);
    }
  };

  return (
    <div className="jobs-page">
      <div className="panel">
        <div className="panel-header">
          <div>
            <h3>Job Descriptions Repository</h3>
            <p>Active positions screened by the Java DSA matching engine</p>
          </div>
          <button className="primary-button" onClick={() => setShowModal(true)}>
            <Plus size={16} /> Create Job Position
          </button>
        </div>

        <div className="filter-row" style={{ marginTop: 16 }}>
          <div className="search-box">
            <Search size={16} />
            <input
              placeholder="Search job titles, keywords, skills..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
          </div>
        </div>

        <div className="job-grid" style={{ marginTop: 20 }}>
          {filteredJobs.map((job) => (
            <article className="job-card" key={job.id}>
              <div className="job-card-header">
                <div>
                  <h3>{job.title}</h3>
                  <p>{job.department || 'Engineering'} • {job.location || 'Hybrid'}</p>
                </div>
                <span className="panel-badge success">Active</span>
              </div>

              <p className="muted" style={{ fontSize: 13, minHeight: 40 }}>
                {job.description}
              </p>

              <div className="skills-wrap" style={{ margin: '12px 0' }}>
                {(job.required || job.requiredSkills || []).map((skill, idx) => (
                  <SkillBadge key={idx} skill={skill} />
                ))}
              </div>

              <div className="job-metrics">
                <div>
                  <small>Job ID</small>
                  <strong>#{job.id}</strong>
                </div>
                <div>
                  <small>Target Skills</small>
                  <strong>{(job.required || job.requiredSkills || []).length} Skills</strong>
                </div>
              </div>

              <div className="button-row" style={{ marginTop: 16 }}>
                <button
                  className="primary-button compact"
                  onClick={() => setActivePage('Screen Resume')}
                >
                  Screen with this Job
                </button>
              </div>
            </article>
          ))}
        </div>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-container" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Create New Job Position</h3>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-grid">
                <div className="field">
                  <label>Job Title *</label>
                  <input
                    required
                    placeholder="e.g. Java Microservices Developer"
                    value={formData.title}
                    onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  />
                </div>
                <div className="field">
                  <label>Department</label>
                  <input
                    value={formData.department}
                    onChange={(e) => setFormData({ ...formData, department: e.target.value })}
                  />
                </div>
                <div className="field">
                  <label>Job Description</label>
                  <textarea
                    rows={4}
                    placeholder="Describe role responsibilities and criteria..."
                    value={formData.description}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  />
                </div>
                <div className="field">
                  <label>Required Skills (Comma separated)</label>
                  <input
                    placeholder="Java, Spring Boot, SQL, Docker, Algorithms"
                    value={formData.requiredSkills}
                    onChange={(e) => setFormData({ ...formData, requiredSkills: e.target.value })}
                  />
                </div>
              </div>
              <div className="modal-actions" style={{ marginTop: 20 }}>
                <button className="secondary-button" type="button" onClick={() => setShowModal(false)}>
                  Cancel
                </button>
                <button className="primary-button" type="submit">
                  Save Job Position
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
