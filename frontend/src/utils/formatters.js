/**
 * Formatting and presentation utilities for ResumeX
 */

export function formatScore(score) {
  if (score === undefined || score === null) return '0.0%';
  const num = typeof score === 'number' ? score : parseFloat(score);
  return `${num.toFixed(1)}%`;
}

export function getScoreColor(score) {
  if (score >= 80) return '#10b981'; // Green
  if (score >= 65) return '#3b82f6'; // Blue
  if (score >= 50) return '#f59e0b'; // Amber
  return '#ef4444'; // Red
}

export function getScoreBadgeClass(score) {
  if (score >= 80) return 'score-high';
  if (score >= 65) return 'score-medium';
  if (score >= 50) return 'score-warning';
  return 'score-low';
}

export function getStatusFromScore(score) {
  if (score >= 70) return 'Shortlisted';
  if (score >= 55) return 'Under Review';
  return 'Rejected';
}

export function getRecommendationBadge(recommendation) {
  if (!recommendation) return { label: 'REVIEW', class: 'badge-review' };
  if (recommendation.includes('STRONG MATCH')) {
    return { label: 'STRONG MATCH', class: 'badge-strong' };
  }
  if (recommendation.includes('GOOD MATCH')) {
    return { label: 'GOOD MATCH', class: 'badge-good' };
  }
  if (recommendation.includes('MODERATE MATCH')) {
    return { label: 'MODERATE MATCH', class: 'badge-moderate' };
  }
  return { label: 'LOW MATCH', class: 'badge-low' };
}

export function truncate(text, length = 120) {
  if (!text) return '';
  if (text.length <= length) return text;
  return text.substring(0, length) + '...';
}
