import React from 'react';
import { Activity, BarChart3, TrendingUp, CheckCircle, PieChart } from 'lucide-react';
import ScoreCard from '../components/ScoreCard';
import { formatScore } from '../utils/formatters';

export default function Analytics({ analytics = {}, candidates = [] }) {
  const total = candidates.length || 5;
  const shortlisted = candidates.filter(c => c.overallScore >= 70).length;
  const review = candidates.filter(c => c.overallScore >= 55 && c.overallScore < 70).length;
  const rejected = candidates.filter(c => c.overallScore < 55).length;

  const avgSkill = candidates.reduce((acc, c) => acc + (c.algorithms?.skillMatch || 0), 0) / (total || 1);
  const avgString = candidates.reduce((acc, c) => acc + (c.algorithms?.stringMatch || 0), 0) / (total || 1);
  const avgEdit = candidates.reduce((acc, c) => acc + (c.algorithms?.editDistance || 0), 0) / (total || 1);
  const avgSeq = candidates.reduce((acc, c) => acc + (c.algorithms?.sequenceAlignment || 0), 0) / (total || 1);

  return (
    <div className="analytics-page">
      <section className="stats-grid">
        <ScoreCard title="Shortlist Rate" value={`${Math.round((shortlisted / total) * 100)}%`} delta={`${shortlisted} candidates passed`} icon={TrendingUp} color="#10b981" />
        <ScoreCard title="Average Skill Match" value={formatScore(avgSkill)} delta="KMP verification" icon={CheckCircle} color="#3b82f6" />
        <ScoreCard title="Average String Match" value={formatScore(avgString)} delta="Keyword frequency" icon={BarChart3} color="#8b5cf6" />
        <ScoreCard title="Avg Sequence Alignment" value={formatScore(avgSeq)} delta="Needleman-Wunsch DP" icon={Activity} color="#06b6d4" />
      </section>

      <section className="content-grid two-one" style={{ marginTop: 24 }}>
        <div className="panel">
          <div className="panel-header">
            <div>
              <h3>Candidate Qualification Funnel</h3>
              <p>Conversion across screening thresholds</p>
            </div>
          </div>
          <div className="funnel-container">
            <div className="funnel-bar" style={{ width: '100%', backgroundColor: '#3b82f6' }}>
              <span>Total Uploaded Resumes: {total}</span>
            </div>
            <div className="funnel-bar" style={{ width: `${Math.round(((shortlisted + review) / total) * 100)}%`, backgroundColor: '#8b5cf6' }}>
              <span>Screened for Review: {shortlisted + review}</span>
            </div>
            <div className="funnel-bar" style={{ width: `${Math.round((shortlisted / total) * 100)}%`, backgroundColor: '#10b981' }}>
              <span>Shortlisted Candidates: {shortlisted}</span>
            </div>
          </div>
        </div>

        <div className="panel">
          <div className="panel-header">
            <div>
              <h3>Algorithm Contribution Health</h3>
              <p>Average score contribution</p>
            </div>
          </div>
          <div className="distribution-bars">
            <div className="distribution-row">
              <span className="dist-label">Skill Match (KMP)</span>
              <div className="dist-track"><div className="dist-fill" style={{ width: `${avgSkill}%`, backgroundColor: '#10b981' }} /></div>
              <span className="dist-count">{avgSkill.toFixed(0)}%</span>
            </div>
            <div className="distribution-row">
              <span className="dist-label">Sequence Alignment</span>
              <div className="dist-track"><div className="dist-fill" style={{ width: `${avgSeq}%`, backgroundColor: '#3b82f6' }} /></div>
              <span className="dist-count">{avgSeq.toFixed(0)}%</span>
            </div>
            <div className="distribution-row">
              <span className="dist-label">Edit Distance (Fuzzy)</span>
              <div className="dist-track"><div className="dist-fill" style={{ width: `${avgEdit}%`, backgroundColor: '#f59e0b' }} /></div>
              <span className="dist-count">{avgEdit.toFixed(0)}%</span>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
