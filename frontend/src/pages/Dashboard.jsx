import React from 'react';
import {
  FileText,
  SearchCheck,
  UserCheck,
  BarChart3,
  CopyCheck,
  ArrowRight,
  Sparkles,
} from 'lucide-react';
import ScoreCard from '../components/ScoreCard';
import CandidateCard from '../components/CandidateCard';
import { formatScore } from '../utils/formatters';

export default function Dashboard({
  analytics = {},
  candidates = [],
  duplicates = {},
  setActivePage,
  onViewCandidate,
  onCompareToggle,
  comparedIds = [],
}) {
  const totalResumes = analytics.totalResumes || candidates.length || 0;
  const candidatesAnalyzed = analytics.candidatesAnalyzed || candidates.length || 0;
  const averageMatch = analytics.averageMatch || 0;
  const duplicateCount = duplicates.totalDuplicateCount || 0;
  const shortlistedCount = analytics.shortlisted || candidates.filter(c => (c.overallScore || 0) >= 70).length;

  const topCandidates = [...candidates].slice(0, 4);

  return (
    <div className="dashboard-container">
      {/* Metric Cards Row */}
      <section className="stats-grid">
        <ScoreCard
          title="Total Resumes"
          value={totalResumes}
          delta="Parsed & indexed"
          icon={FileText}
          color="#3b82f6"
        />
        <ScoreCard
          title="Screened Candidates"
          value={candidatesAnalyzed}
          delta="Automated evaluation"
          icon={SearchCheck}
          color="#10b981"
        />
        <ScoreCard
          title="Average Match Score"
          value={formatScore(averageMatch)}
          delta="Across active criteria"
          icon={BarChart3}
          color="#8b5cf6"
        />
        <ScoreCard
          title="Shortlisted"
          value={shortlistedCount}
          delta="Score ≥ 70%"
          icon={UserCheck}
          color="#06b6d4"
        />
        <ScoreCard
          title="Duplicate Resumes"
          value={duplicateCount}
          delta={duplicateCount > 0 ? "Review flagged profiles" : "0 duplicates"}
          icon={CopyCheck}
          color={duplicateCount > 0 ? "#f59e0b" : "#64748b"}
        />
      </section>

      {/* Middle Grid: Score Bands & Quick Action Banner */}
      <section className="dashboard-middle-grid">
        <div className="panel score-distribution-card">
          <div className="panel-header">
            <div>
              <h3>Candidate Match Distribution</h3>
              <p>Breakdown of screened candidates across qualification tiers</p>
            </div>
          </div>

          <div className="clean-distribution-list">
            <div className="clean-dist-row">
              <span className="tier-name">90% - 100% (High Match)</span>
              <div className="tier-bar-track">
                <div
                  className="tier-bar-fill"
                  style={{
                    width: `${Math.max(6, (candidates.filter(c => (c.overallScore || 0) >= 90).length / (candidates.length || 1)) * 100)}%`,
                    backgroundColor: '#10b981',
                  }}
                />
              </div>
              <strong className="tier-count">{candidates.filter(c => (c.overallScore || 0) >= 90).length}</strong>
            </div>

            <div className="clean-dist-row">
              <span className="tier-name">70% - 89% (Strong Match)</span>
              <div className="tier-bar-track">
                <div
                  className="tier-bar-fill"
                  style={{
                    width: `${Math.max(6, (candidates.filter(c => (c.overallScore || 0) >= 70 && (c.overallScore || 0) < 90).length / (candidates.length || 1)) * 100)}%`,
                    backgroundColor: '#3b82f6',
                  }}
                />
              </div>
              <strong className="tier-count">{candidates.filter(c => (c.overallScore || 0) >= 70 && (c.overallScore || 0) < 90).length}</strong>
            </div>

            <div className="clean-dist-row">
              <span className="tier-name">55% - 69% (Under Review)</span>
              <div className="tier-bar-track">
                <div
                  className="tier-bar-fill"
                  style={{
                    width: `${Math.max(6, (candidates.filter(c => (c.overallScore || 0) >= 55 && (c.overallScore || 0) < 70).length / (candidates.length || 1)) * 100)}%`,
                    backgroundColor: '#f59e0b',
                  }}
                />
              </div>
              <strong className="tier-count">{candidates.filter(c => (c.overallScore || 0) >= 55 && (c.overallScore || 0) < 70).length}</strong>
            </div>

            <div className="clean-dist-row">
              <span className="tier-name">Below 55% (Low Match)</span>
              <div className="tier-bar-track">
                <div
                  className="tier-bar-fill"
                  style={{
                    width: `${Math.max(6, (candidates.filter(c => (c.overallScore || 0) < 55).length / (candidates.length || 1)) * 100)}%`,
                    backgroundColor: '#ef4444',
                  }}
                />
              </div>
              <strong className="tier-count">{candidates.filter(c => (c.overallScore || 0) < 55).length}</strong>
            </div>
          </div>
        </div>

        {/* Quick Launch Card */}
        <div className="panel quick-screening-callout">
          <div className="callout-header">
            <Sparkles size={20} className="text-primary" />
            <div>
              <h3>Automated Screening</h3>
              <p>Evaluate multiple candidate resumes in seconds.</p>
            </div>
          </div>
          <p className="callout-body">
            Upload new PDF resumes or select an active job profile to re-evaluate and rank candidate suitability automatically.
          </p>
          <button
            type="button"
            className="primary-button full"
            onClick={() => setActivePage('Screen Resumes')}
          >
            <SearchCheck size={16} /> Screen New Resumes
          </button>
        </div>
      </section>

      {/* Top Ranked Candidates Grid */}
      <section className="dashboard-candidates-section">
        <div className="section-header-flex">
          <div>
            <h3>Top Matching Candidates</h3>
            <p>Highest ranked profiles based on required skills and criteria match</p>
          </div>
          <button
            type="button"
            className="text-button"
            onClick={() => setActivePage('Candidates')}
          >
            View All Candidates <ArrowRight size={14} />
          </button>
        </div>

        <div className="candidate-grid" style={{ marginTop: 16 }}>
          {topCandidates.map((candidate) => (
            <CandidateCard
              key={candidate.id}
              candidate={candidate}
              onViewDetails={onViewCandidate}
              onCompareToggle={onCompareToggle}
              isCompared={comparedIds.includes(candidate.id)}
            />
          ))}
        </div>
      </section>

      {/* Duplicate Alert Banner if duplicates exist */}
      {duplicateCount > 0 && (
        <section
          className="duplicate-summary-banner"
          onClick={() => setActivePage('Duplicate Detection')}
        >
          <div className="banner-icon-circle">
            <CopyCheck size={20} />
          </div>
          <div className="banner-text">
            <strong>{duplicateCount} Duplicate or Similar Profile(s) Flagged</strong>
            <p>Multiple submissions detected. Review and merge records to maintain a clean database.</p>
          </div>
          <button type="button" className="secondary-button compact">
            Review Duplicates <ArrowRight size={14} />
          </button>
        </section>
      )}
    </div>
  );
}
