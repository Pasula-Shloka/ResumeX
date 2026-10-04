import React, { useState } from 'react';
import {
  UploadCloud,
  FileText,
  SearchCheck,
  CheckCircle2,
  AlertCircle,
  ArrowRight,
  Plus,
  X,
  Loader2,
  Trash2,
} from 'lucide-react';
import UploadZone from '../components/UploadZone';
import CandidateCard from '../components/CandidateCard';
import { api } from '../services/api';

export default function ScreenResume({
  jobs = [],
  resumes = [],
  onAnalysisComplete,
  setActivePage,
  onViewCandidate,
  initialJobId,
}) {
  const [selectedJobId, setSelectedJobId] = useState(initialJobId || jobs[0]?.id || 101);

  React.useEffect(() => {
    if (initialJobId) {
      setSelectedJobId(initialJobId);
    }
  }, [initialJobId]);
  const [isCustomJob, setIsCustomJob] = useState(false);
  const [customTitle, setCustomTitle] = useState('');
  const [customDesc, setCustomDesc] = useState('');
  const [customSkills, setCustomSkills] = useState(['Java', 'Spring Boot', 'REST API', 'MySQL', 'Git', 'Docker']);
  const [skillInput, setSkillInput] = useState('');

  // Staged files
  const [stagedFiles, setStagedFiles] = useState([]);
  const [isProcessing, setIsProcessing] = useState(false);
  const [statusMessage, setStatusMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [results, setResults] = useState(null);

  const activeJob = jobs.find((j) => j.id === selectedJobId) || jobs[0];

  const handleFilesAdded = async (files) => {
    setStagedFiles((prev) => [...prev, ...files]);
    setStatusMessage('');
    setErrorMessage('');
    try {
      const res = await api.uploadResumes(files);
      setStatusMessage(`${res.uploadedCount || files.length} PDF resume(s) uploaded and parsed.`);
    } catch (err) {
      console.warn("Upload notice:", err.message);
    }
  };

  const handleRemoveFile = (index) => {
    setStagedFiles((prev) => prev.filter((_, idx) => idx !== index));
  };

  const handleAddSkill = (e) => {
    e.preventDefault();
    if (skillInput.trim() && !customSkills.includes(skillInput.trim())) {
      setCustomSkills([...customSkills, skillInput.trim()]);
      setSkillInput('');
    }
  };

  const handleRemoveSkill = (skill) => {
    setCustomSkills(customSkills.filter((s) => s !== skill));
  };

  const handleScreenResumes = async () => {
    setIsProcessing(true);
    setErrorMessage('');
    setStatusMessage('');

    try {
      const payload = isCustomJob
        ? {
            jobTitle: customTitle || 'Custom Technical Position',
            description: customDesc || customTitle,
            requiredSkills: customSkills,
          }
        : {
            jobId: selectedJobId,
          };

      const report = await api.analyze(payload);

      // Brief delay for smooth UI transition
      setTimeout(() => {
        setIsProcessing(false);
        setResults(report);
        if (onAnalysisComplete) {
          onAnalysisComplete(report);
        }
      }, 1000);
    } catch (err) {
      setIsProcessing(false);
      setErrorMessage(`Screening failed: ${err.message}`);
    }
  };

  return (
    <div className="screen-resumes-container">
      {/* Top Banner */}
      <div className="page-header-row">
        <div>
          <h3>Screen & Rank Resumes</h3>
          <p>Match candidate resumes against target job criteria to calculate ranking and skill coverage.</p>
        </div>
      </div>

      {errorMessage && (
        <div className="alert-banner-error">
          <AlertCircle size={17} />
          <span>{errorMessage}</span>
        </div>
      )}

      {statusMessage && (
        <div className="alert-banner-success">
          <CheckCircle2 size={17} />
          <span>{statusMessage}</span>
        </div>
      )}

      {/* Main 2-Column Screening Form */}
      <div className="screening-grid-layout">
        {/* Left Column: Job Description & Target Skills */}
        <div className="screening-panel">
          <div className="panel-title-bar">
            <h4>1. Job Position & Skills</h4>
          </div>

          <div className="job-mode-tabs">
            <button
              type="button"
              className={`mode-tab ${!isCustomJob ? 'active' : ''}`}
              onClick={() => setIsCustomJob(false)}
            >
              Select Existing Job
            </button>
            <button
              type="button"
              className={`mode-tab ${isCustomJob ? 'active' : ''}`}
              onClick={() => setIsCustomJob(true)}
            >
              Custom Job Criteria
            </button>
          </div>

          {!isCustomJob ? (
            <div className="form-fields-stack">
              <div className="input-group">
                <label>Job Position</label>
                <select
                  value={selectedJobId}
                  onChange={(e) => setSelectedJobId(Number(e.target.value))}
                >
                  {jobs.map((j) => (
                    <option key={j.id} value={j.id}>
                      {j.title}
                    </option>
                  ))}
                </select>
              </div>

              <div className="input-group">
                <label>Job Overview</label>
                <textarea
                  readOnly
                  rows={4}
                  value={activeJob?.description || ''}
                  className="textarea-readonly"
                />
              </div>

              <div className="input-group">
                <label>Target Required Skills ({(activeJob?.required || []).length})</label>
                <div className="target-skills-cloud">
                  {(activeJob?.required || []).map((skill, idx) => (
                    <span key={idx} className="target-skill-pill">
                      {skill}
                    </span>
                  ))}
                </div>
              </div>
            </div>
          ) : (
            <div className="form-fields-stack">
              <div className="input-group">
                <label>Position Title</label>
                <input
                  placeholder="e.g. Senior Software Engineer"
                  value={customTitle}
                  onChange={(e) => setCustomTitle(e.target.value)}
                />
              </div>

              <div className="input-group">
                <label>Job Description</label>
                <textarea
                  rows={4}
                  placeholder="Paste or write the job description here..."
                  value={customDesc}
                  onChange={(e) => setCustomDesc(e.target.value)}
                />
              </div>

              <div className="input-group">
                <label>Required Skills</label>
                <div className="add-skill-row">
                  <input
                    placeholder="Enter skill (e.g. Python, SQL, Docker)..."
                    value={skillInput}
                    onChange={(e) => setSkillInput(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && handleAddSkill(e)}
                  />
                  <button type="button" className="secondary-button" onClick={handleAddSkill}>
                    <Plus size={15} /> Add
                  </button>
                </div>
                <div className="target-skills-cloud" style={{ marginTop: 8 }}>
                  {customSkills.map((skill, idx) => (
                    <span key={idx} className="target-skill-pill editable">
                      {skill}
                      <X size={12} onClick={() => handleRemoveSkill(skill)} />
                    </span>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Right Column: PDF Resume Upload */}
        <div className="screening-panel">
          <div className="panel-title-bar">
            <h4>2. Upload Candidate Resumes (PDF)</h4>
            <span className="count-tag">
              {stagedFiles.length > 0 ? `${stagedFiles.length} New Uploads` : `${resumes.length || 5} Ready Resumes`}
            </span>
          </div>

          <UploadZone
            onFilesSelected={handleFilesAdded}
            uploadedFiles={stagedFiles.length > 0 ? stagedFiles : resumes}
            uploading={isProcessing}
          />

          {stagedFiles.length > 0 && (
            <div className="staged-files-container">
              <span className="staged-header">Newly Staged Files ({stagedFiles.length})</span>
              <div className="staged-list">
                {stagedFiles.map((file, idx) => (
                  <div key={idx} className="staged-file-chip">
                    <FileText size={14} />
                    <span className="file-chip-name">{file.name}</span>
                    <button
                      type="button"
                      className="chip-remove-btn"
                      onClick={() => handleRemoveFile(idx)}
                    >
                      <X size={13} />
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Screen Resumes Action Button */}
      <div className="screen-action-card">
        <button
          type="button"
          className="primary-button cta-screen-btn"
          disabled={isProcessing}
          onClick={handleScreenResumes}
        >
          {isProcessing ? (
            <>
              <Loader2 size={18} className="spin" />
              <span>Analyzing Resumes & Ranking Candidates...</span>
            </>
          ) : (
            <>
              <SearchCheck size={18} />
              <span>Analyze & Rank Resumes</span>
            </>
          )}
        </button>
      </div>

      {/* Post-Screening Results View */}
      {results && (
        <div className="results-summary-section">
          <div className="results-header-row">
            <div>
              <h3>Screening Results</h3>
              <p>Top candidate matches for {results.job?.title || 'Selected Position'}</p>
            </div>
            <button className="primary-button" onClick={() => setActivePage('Candidates')}>
              <span>View All Candidates</span>
              <ArrowRight size={15} />
            </button>
          </div>

          <div className="candidate-grid" style={{ marginTop: 18 }}>
            {(results.candidates || []).slice(0, 4).map((c) => (
              <CandidateCard
                key={c.id}
                candidate={c}
                onViewDetails={onViewCandidate}
              />
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
