import React from 'react';
import {
  LayoutDashboard,
  Briefcase,
  SearchCheck,
  Users,
  Kanban,
  Scale,
  CopyCheck,
} from 'lucide-react';

const navItems = [
  { label: 'Dashboard', icon: LayoutDashboard },
  { label: 'Job Openings', icon: Briefcase },
  { label: 'Screen Resumes', icon: SearchCheck },
  { label: 'Candidates', icon: Users },
  { label: 'Hiring Pipeline', icon: Kanban },
  { label: 'Compare Resumes', icon: Scale },
  { label: 'Duplicate Detection', icon: CopyCheck },
];

export default function Sidebar({ activePage, setActivePage, open, onClose, comparedCount = 0 }) {
  return (
    <aside className={`sidebar ${open ? 'open' : ''}`}>
      <div
        className="sidebar-brand"
        onClick={() => {
          setActivePage('Dashboard');
          if (onClose) onClose();
        }}
      >
        <div className="brand-logo">RX</div>
        <div className="brand-text">
          <h2>ResumeX</h2>
          <span>AI Resume Screening</span>
        </div>
      </div>

      <nav className="sidebar-nav">
        {navItems.map(({ label, icon: Icon }) => {
          const isActive = activePage === label;
          return (
            <button
              key={label}
              className={`sidebar-nav-item ${isActive ? 'active' : ''}`}
              onClick={() => {
                setActivePage(label);
                if (onClose) onClose();
              }}
            >
              <Icon size={19} />
              <span>{label}</span>
              {label === 'Compare Resumes' && comparedCount > 0 && (
                <span className="sidebar-count-badge">{comparedCount}</span>
              )}
            </button>
          );
        })}
      </nav>

      <div className="sidebar-footer">
        <div className="engine-status-pill">
          <span className="status-dot-active" />
          <span>Screening Engine Active</span>
        </div>
      </div>
    </aside>
  );
}
