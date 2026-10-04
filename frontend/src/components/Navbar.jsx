import React from 'react';
import { Moon, Sun, Menu, Search, User } from 'lucide-react';

export default function Navbar({
  activePage,
  isDark,
  setIsDark,
  onMenu,
  searchQuery,
  setSearchQuery,
}) {
  return (
    <header className="topbar">
      <div className="topbar-left">
        <button className="mobile-menu" onClick={onMenu} aria-label="Open navigation menu">
          <Menu size={20} />
        </button>

        <div className="topbar-page-header">
          <h2>{activePage}</h2>
          <p>{getPageDescription(activePage)}</p>
        </div>
      </div>

      <div className="topbar-right">
        <div className="topbar-search">
          <Search size={16} />
          <input
            placeholder="Search candidates, skills, files..."
            value={searchQuery || ''}
            onChange={(e) => setSearchQuery && setSearchQuery(e.target.value)}
          />
        </div>

        <button
          className="theme-toggle-btn"
          onClick={() => setIsDark(!isDark)}
          aria-label="Toggle theme"
          title={isDark ? "Switch to Light Mode" : "Switch to Dark Mode"}
        >
          {isDark ? <Sun size={18} /> : <Moon size={18} />}
        </button>

        <div className="recruiter-badge">
          <div className="recruiter-avatar">
            <User size={15} />
          </div>
          <span className="recruiter-name">Recruiter</span>
        </div>
      </div>
    </header>
  );
}

function getPageDescription(page) {
  switch (page) {
    case 'Dashboard':
      return 'Overview of screened resumes, match distributions, and top talent.';
    case 'Screen Resumes':
      return 'Upload PDF resumes, set job criteria, and run automated candidate screening.';
    case 'Candidates':
      return 'Ranked list of evaluated candidates with score breakdowns and skill matching.';
    case 'Compare Resumes':
      return 'Side-by-side comparison of selected candidate profiles and qualifications.';
    case 'Duplicate Detection':
      return 'Review potential duplicate submissions and high-similarity resumes.';
    default:
      return 'Intelligent candidate evaluation and resume screening.';
  }
}
