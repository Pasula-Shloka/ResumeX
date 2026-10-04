import React from 'react';
import { Settings as SettingsIcon, Server, ShieldCheck, CheckCircle2, Sliders, RefreshCw } from 'lucide-react';

export default function Settings({ backendConnected, onRecheckHealth }) {
  return (
    <div className="settings-page">
      <div className="panel">
        <div className="panel-header">
          <div>
            <h3>System Settings & Integration Details</h3>
            <p>Connection configuration between VS Code React frontend and Eclipse Java backend</p>
          </div>
        </div>

        <div className="settings-list" style={{ marginTop: 20 }}>
          <div className="setting-card">
            <div className="setting-info">
              <Server size={20} className="text-primary" />
              <div>
                <strong>Java DSA API Server</strong>
                <p>Embedded HttpServer listening on port 8080. Exposes /api/analyze, /api/candidates, etc.</p>
              </div>
            </div>
            <div className="setting-action">
              <span className={`backend-badge ${backendConnected ? 'connected' : 'disconnected'}`}>
                {backendConnected ? "Connected (Port 8080)" : "Offline"}
              </span>
              <button className="secondary-button compact" onClick={onRecheckHealth} style={{ marginLeft: 8 }}>
                <RefreshCw size={14} /> Ping
              </button>
            </div>
          </div>

          <div className="setting-card">
            <div className="setting-info">
              <Sliders size={20} className="text-primary" />
              <div>
                <strong>Shortlist Recommendation Threshold</strong>
                <p>Candidates scoring above this threshold receive a "STRONG MATCH / RECOMMENDED" badge.</p>
              </div>
            </div>
            <div className="setting-action">
              <code>70.0%</code>
            </div>
          </div>

          <div className="setting-card">
            <div className="setting-info">
              <ShieldCheck size={20} className="text-primary" />
              <div>
                <strong>Duplicate Detection Threshold</strong>
                <p>Resumes with Levenshtein text similarity equal to or exceeding this value are flagged as near-duplicates.</p>
              </div>
            </div>
            <div className="setting-action">
              <code>80.0%</code>
            </div>
          </div>

          <div className="setting-card">
            <div className="setting-info">
              <CheckCircle2 size={20} className="text-primary" />
              <div>
                <strong>Algorithm Weights Formula (MatchResult.java)</strong>
                <p>Composite scoring weights defined in the Java backend core model.</p>
              </div>
            </div>
            <div className="setting-action">
              <small>Skill 30% | String 20% | Edit 15% | Seq 15% | Suffix 10% | Approx 10%</small>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
