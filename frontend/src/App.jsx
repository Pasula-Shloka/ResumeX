import React, { useState, useEffect } from 'react';
import './index.css';

// Core Components
import Navbar from './components/Navbar';
import Sidebar from './components/Sidebar';
import CandidateDetailModal from './components/CandidateDetailModal';

// Clean Recruiter Pages
import Dashboard from './pages/Dashboard';
import JobOpenings from './pages/JobOpenings';
import ScreenResume from './pages/ScreenResume';
import Candidates from './pages/Rankings';
import HiringPipeline from './pages/HiringPipeline';
import ResumeComparisonPage from './pages/ResumeComparisonPage';
import DuplicateDetection from './pages/DuplicateDetection';

// API Client Service
import { api } from './services/api';

// Initial fallback seeds
const initialJobsSeed = [
  {
    id: 101,
    title: 'Java Backend Developer',
    department: 'Engineering',
    location: 'Hyderabad / Hybrid',
    description: 'We are seeking an experienced Java Backend Developer to build scalable enterprise microservices. Expertise in Java, Spring Boot, REST APIs, SQL, MySQL, Git, Docker, Data Structures, and Algorithms.',
    required: ['Java', 'Spring Boot', 'REST API', 'SQL', 'MySQL', 'Git', 'Docker', 'Data Structures', 'Algorithms'],
  },
  {
    id: 102,
    title: 'Frontend React Developer',
    department: 'Engineering',
    location: 'Bengaluru / Remote',
    description: 'Looking for a Frontend Developer proficient in React, JavaScript, HTML, CSS, and Git. Must be skilled in building responsive user interfaces.',
    required: ['React', 'JavaScript', 'HTML', 'CSS', 'Git', 'REST API'],
  },
  {
    id: 103,
    title: 'Data Analyst & ML Engineer',
    department: 'Data Intelligence',
    location: 'Remote',
    description: 'Seeking a Data Analyst with practical experience in Python, SQL, Machine Learning, Deep Learning, and database management.',
    required: ['Python', 'SQL', 'Machine Learning', 'Deep Learning', 'PostgreSQL', 'DBMS'],
  },
];

export default function App() {
  const [activePage, setActivePage] = useState('Dashboard');
  const [isDark, setIsDark] = useState(false);
  const [sidebarOpen, setSidebarOpen] = useState(false);

  // Core Data
  const [jobs, setJobs] = useState(initialJobsSeed);
  const [resumes, setResumes] = useState([]);
  const [candidates, setCandidates] = useState([]);
  const [duplicates, setDuplicates] = useState({ exactDuplicates: [], nearDuplicates: [], totalDuplicateCount: 0 });
  const [analytics, setAnalytics] = useState({ totalResumes: 5, candidatesAnalyzed: 5, averageMatch: 74.2, shortlisted: 2, underReview: 2, rejected: 1 });

  // Recruiter Workflow State
  const [candidateStages, setCandidateStages] = useState({});
  const [recruiterNotes, setRecruiterNotes] = useState({});
  const [targetJobId, setTargetJobId] = useState(101);

  // Modals & Selection
  const [selectedCandidate, setSelectedCandidate] = useState(null);
  const [comparedIds, setComparedIds] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');

  // Initial Data Synchronization
  const loadBackendData = async () => {
    try {
      const health = await api.checkHealth();
      if (health.connected) {
        const [jobsData, resumesData, candidatesData, duplicatesData, analyticsData] = await Promise.all([
          api.getJobs().catch(() => initialJobsSeed),
          api.getResumes().catch(() => []),
          api.getCandidates().catch(() => []),
          api.getDuplicates().catch(() => ({ exactDuplicates: [], nearDuplicates: [], totalDuplicateCount: 0 })),
          api.getAnalytics().catch(() => ({})),
        ]);

        if (jobsData && jobsData.length > 0) setJobs(jobsData);
        if (resumesData && resumesData.length > 0) setResumes(resumesData);
        if (candidatesData && candidatesData.length > 0) {
          setCandidates(candidatesData);
        }
        if (duplicatesData) setDuplicates(duplicatesData);
        if (analyticsData && analyticsData.totalResumes !== undefined) setAnalytics(analyticsData);
      }
    } catch (e) {
      console.warn("Could not sync with backend:", e);
    }
  };

  useEffect(() => {
    loadBackendData();
  }, []);

  // Multi-candidate comparison toggle (smoothly supports 2, 3, 4, 5+ resumes without resetting!)
  const handleCompareToggle = (id) => {
    if (comparedIds.includes(id)) {
      setComparedIds(comparedIds.filter(i => i !== id));
    } else {
      setComparedIds([...comparedIds, id]);
    }
  };

  const handleUpdateCandidateStage = (id, newStage) => {
    setCandidateStages((prev) => ({
      ...prev,
      [id]: newStage,
    }));
  };

  const handleSaveNotes = (id, notesText) => {
    setRecruiterNotes((prev) => ({
      ...prev,
      [id]: notesText,
    }));
  };

  const handleAnalysisComplete = (report) => {
    if (report && report.candidates) {
      setCandidates(report.candidates);
      if (report.duplicates) setDuplicates(report.duplicates);
      if (report.analytics) setAnalytics(report.analytics);
    }
  };

  const handleSelectJobForScreening = (job) => {
    if (job && job.id) {
      setTargetJobId(job.id);
    }
  };

  return (
    <div className={`app ${isDark ? 'dark' : ''}`}>
      <Sidebar
        activePage={activePage}
        setActivePage={(page) => {
          setActivePage(page);
          setSidebarOpen(false);
        }}
        open={sidebarOpen}
        onClose={() => setSidebarOpen(false)}
        comparedCount={comparedIds.length}
      />

      <main className="main-content">
        <Navbar
          activePage={activePage}
          isDark={isDark}
          setIsDark={setIsDark}
          onMenu={() => setSidebarOpen(true)}
          searchQuery={searchQuery}
          setSearchQuery={setSearchQuery}
        />

        <div className="page-body">
          {activePage === 'Dashboard' && (
            <Dashboard
              analytics={analytics}
              candidates={candidates}
              duplicates={duplicates}
              setActivePage={setActivePage}
              onViewCandidate={(c) => setSelectedCandidate(c)}
              onCompareToggle={handleCompareToggle}
              comparedIds={comparedIds}
            />
          )}

          {activePage === 'Job Openings' && (
            <JobOpenings
              jobs={jobs}
              setJobs={setJobs}
              candidates={candidates}
              onSelectJobForScreening={handleSelectJobForScreening}
              setActivePage={setActivePage}
            />
          )}

          {activePage === 'Screen Resumes' && (
            <ScreenResume
              jobs={jobs}
              resumes={resumes}
              initialJobId={targetJobId}
              onAnalysisComplete={handleAnalysisComplete}
              setActivePage={setActivePage}
              onViewCandidate={(c) => setSelectedCandidate(c)}
            />
          )}

          {activePage === 'Candidates' && (
            <Candidates
              candidates={candidates}
              onViewCandidate={(c) => setSelectedCandidate(c)}
              onCompareToggle={handleCompareToggle}
              comparedIds={comparedIds}
              setComparedIds={setComparedIds}
              setActivePage={setActivePage}
              candidateStages={candidateStages}
            />
          )}

          {activePage === 'Hiring Pipeline' && (
            <HiringPipeline
              candidates={candidates}
              candidateStages={candidateStages}
              onUpdateCandidateStage={handleUpdateCandidateStage}
              onViewCandidate={(c) => setSelectedCandidate(c)}
            />
          )}

          {activePage === 'Compare Resumes' && (
            <ResumeComparisonPage
              candidates={candidates}
              comparedIds={comparedIds}
              setComparedIds={setComparedIds}
              setActivePage={setActivePage}
            />
          )}

          {activePage === 'Duplicate Detection' && (
            <DuplicateDetection duplicates={duplicates} />
          )}
        </div>
      </main>

      {/* Candidate Profile Details Modal */}
      {selectedCandidate && (
        <CandidateDetailModal
          candidate={selectedCandidate}
          onClose={() => setSelectedCandidate(null)}
          currentStage={candidateStages[selectedCandidate.id]}
          onUpdateStage={handleUpdateCandidateStage}
          recruiterNotes={recruiterNotes[selectedCandidate.id] || ''}
          onSaveNotes={handleSaveNotes}
        />
      )}

      {sidebarOpen && (
        <button
          className="scrim"
          aria-label="Close menu"
          onClick={() => setSidebarOpen(false)}
        />
      )}
    </div>
  );
}
