import React, { useState, useMemo } from 'react';
import {
  Search,
  Scale,
  LayoutGrid,
  Table as TableIcon,
  Filter,
  Download,
  CheckSquare,
  Square,
  ArrowRight,
  Sparkles,
  Tag,
  CheckCircle2,
} from 'lucide-react';
import CandidateCard from '../components/CandidateCard';
import RankingTable from '../components/RankingTable';

export default function Candidates({
  candidates = [],
  onViewCandidate,
  onCompareToggle,
  comparedIds = [],
  setComparedIds,
  setActivePage,
  candidateStages = {},
}) {
  const [viewMode, setViewMode] = useState('grid');
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('All');
  const [selectedSkillFilter, setSelectedSkillFilter] = useState('All');
  const [sortBy, setSortBy] = useState('overall');

  // Compute common skills for quick-filter pills
  const availableSkills = useMemo(() => {
    const counts = {};
    candidates.forEach((c) => {
      (c.skills || []).forEach((s) => {
        counts[s] = (counts[s] || 0) + 1;
      });
    });
    return Object.entries(counts)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 8)
      .map(([s]) => s);
  }, [candidates]);

  // Filter candidates
  const filteredCandidates = candidates.filter((c) => {
    const query = searchQuery.toLowerCase();
    const matchesSearch =
      (c.name || '').toLowerCase().includes(query) ||
      (c.file || '').toLowerCase().includes(query) ||
      (c.skills || []).some((s) => s.toLowerCase().includes(query));

    if (!matchesSearch) return false;

    // Skill Pill Filter
    if (selectedSkillFilter !== 'All') {
      const hasSkill = (c.skills || []).some(
        (s) => s.toLowerCase() === selectedSkillFilter.toLowerCase()
      );
      if (!hasSkill) return false;
    }

    // Status Filter (checks candidate stages if assigned, else falls back to score tiers)
    const stage = candidateStages[c.id] || (
      (c.overallScore || 0) >= 70 ? 'Shortlisted' :
      (c.overallScore || 0) >= 55 ? 'Under Review' : 'Rejected'
    );

    if (statusFilter === 'Shortlisted') return stage === 'Shortlisted';
    if (statusFilter === 'Under Review') return stage === 'Under Review';
    if (statusFilter === 'Interview') return stage === 'Interview Scheduled';
    if (statusFilter === 'Rejected') return stage === 'Rejected';
    return true;
  });

  // Sort candidates
  const sortedCandidates = [...filteredCandidates].sort((a, b) => {
    if (sortBy === 'name') {
      return (a.name || '').localeCompare(b.name || '');
    }
    return (b.overallScore || 0) - (a.overallScore || 0);
  });

  // CSV Exporter
  const handleExportCSV = () => {
    if (sortedCandidates.length === 0) return;

    const headers = [
      'Rank',
      'Candidate Name',
      'Match Score (%)',
      'Status / Stage',
      'Email',
      'Phone',
      'Resume File',
      'Matched Skills Count',
      'Matched Skills',
      'Missing Skills',
      'Duplicate Flag',
    ];

    const rows = sortedCandidates.map((c, idx) => {
      const stage = candidateStages[c.id] || (
        (c.overallScore || 0) >= 70 ? 'Shortlisted' :
        (c.overallScore || 0) >= 55 ? 'Under Review' : 'Rejected'
      );
      return [
        c.rank || idx + 1,
        `"${(c.name || '').replace(/"/g, '""')}"`,
        (c.overallScore || 0).toFixed(1),
        `"${stage}"`,
        `"${c.email || ''}"`,
        `"${c.phone || ''}"`,
        `"${c.file || ''}"`,
        c.matchedSkills ? c.matchedSkills.length : 0,
        `"${(c.matchedSkills || []).join('; ')}"`,
        `"${(c.missingSkills || []).join('; ')}"`,
        c.isDuplicate ? 'YES' : 'NO',
      ].join(',');
    });

    const csvContent = [headers.join(','), ...rows].join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.setAttribute('href', url);
    link.setAttribute(
      'download',
      `ResumeX_Candidate_Shortlist_${new Date().toISOString().slice(0, 10)}.csv`
    );
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleSelectTop3 = () => {
    const top3Ids = sortedCandidates.slice(0, 3).map((c) => c.id);
    if (setComparedIds) setComparedIds(top3Ids);
  };

  return (
    <div className="candidates-page-container">
      {/* Search, Filter & Actions Toolbar */}
      <div className="candidates-toolbar">
        <div className="search-input-wrapper">
          <Search size={16} />
          <input
            placeholder="Search candidate by name, resume file, or skills..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        <div className="toolbar-controls-group">
          <div className="control-select-box">
            <Filter size={14} />
            <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
              <option value="All">All Statuses ({candidates.length})</option>
              <option value="Shortlisted">Shortlisted</option>
              <option value="Interview">Interview Scheduled</option>
              <option value="Under Review">Under Review</option>
              <option value="Rejected">Rejected</option>
            </select>
          </div>

          <div className="control-select-box">
            <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
              <option value="overall">Sort by Match Score</option>
              <option value="name">Sort by Candidate Name</option>
            </select>
          </div>

          <button
            type="button"
            className="secondary-button compact"
            onClick={handleExportCSV}
            title="Download candidate shortlist as CSV"
          >
            <Download size={14} />
            <span>Export CSV</span>
          </button>

          <div className="view-toggle-btns">
            <button
              type="button"
              className={`toggle-btn ${viewMode === 'grid' ? 'active' : ''}`}
              onClick={() => setViewMode('grid')}
              title="Card Grid View"
            >
              <LayoutGrid size={16} />
            </button>
            <button
              type="button"
              className={`toggle-btn ${viewMode === 'table' ? 'active' : ''}`}
              onClick={() => setViewMode('table')}
              title="Table View"
            >
              <TableIcon size={16} />
            </button>
          </div>
        </div>
      </div>

      {/* Quick Skill Filter Pills Row */}
      {availableSkills.length > 0 && (
        <div className="skill-filter-pills-bar">
          <span className="pills-label">
            <Tag size={13} /> Filter by Verified Skill:
          </span>
          <div className="pills-scroll-row">
            <button
              type="button"
              className={`skill-pill-filter ${selectedSkillFilter === 'All' ? 'active' : ''}`}
              onClick={() => setSelectedSkillFilter('All')}
            >
              All Skills ({candidates.length})
            </button>
            {availableSkills.map((skill) => (
              <button
                key={skill}
                type="button"
                className={`skill-pill-filter ${selectedSkillFilter === skill ? 'active' : ''}`}
                onClick={() => setSelectedSkillFilter(skill)}
              >
                {skill}
              </button>
            ))}
          </div>

          {comparedIds.length === 0 && sortedCandidates.length >= 3 && (
            <button
              type="button"
              className="quick-select-top-btn"
              onClick={handleSelectTop3}
            >
              Select Top 3 for Compare
            </button>
          )}
        </div>
      )}

      {/* Floating Comparison Action Bar when candidates are selected */}
      {comparedIds.length > 0 && (
        <div className="comparison-banner-bar">
          <div className="banner-left">
            <Scale size={18} className="text-primary" />
            <span>
              <strong>{comparedIds.length}</strong> candidate profile(s) selected
            </span>
          </div>
          <div className="banner-right">
            <button
              type="button"
              className="secondary-button compact"
              onClick={() => setComparedIds && setComparedIds([])}
            >
              Clear Selection
            </button>
            <button
              type="button"
              className="primary-button compact"
              onClick={() => setActivePage('Compare Resumes')}
            >
              <span>Compare {comparedIds.length} Resumes</span>
              <ArrowRight size={14} />
            </button>
          </div>
        </div>
      )}

      {/* Candidates Display View */}
      {viewMode === 'grid' ? (
        <div className="candidate-grid">
          {sortedCandidates.length === 0 ? (
            <div className="empty-state-panel full-width">
              <p>No candidates match your search or filter criteria.</p>
            </div>
          ) : (
            sortedCandidates.map((candidate) => (
              <CandidateCard
                key={candidate.id}
                candidate={candidate}
                onViewDetails={onViewCandidate}
                onCompareToggle={onCompareToggle}
                isCompared={comparedIds.includes(candidate.id)}
              />
            ))
          )}
        </div>
      ) : (
        <RankingTable
          candidates={sortedCandidates}
          onViewCandidate={onViewCandidate}
        />
      )}
    </div>
  );
}
