/**
 * ResumeX API Client Service
 * Connects the React frontend to the Java DSA HttpServer running on http://localhost:8080.
 * Includes graceful static fallback data for standalone / GitHub Pages hosting.
 */

const API_BASE = 'http://localhost:8080/api';

const fallbackCandidates = [
  {
    id: 5,
    rank: 1,
    name: 'Rahul Sharma',
    email: 'rahul.sharma.dev@example.com',
    phone: '+91 98765 43210',
    file: 'Rahul_Sharma.pdf',
    skills: ['Java', 'Spring Boot', 'REST API', 'SQL', 'MySQL', 'Git', 'Docker', 'Data Structures', 'Algorithms', 'Linux'],
    matchedSkills: ['Java', 'Spring Boot', 'REST API', 'SQL', 'MySQL', 'Git', 'Docker', 'Data Structures', 'Algorithms'],
    missingSkills: [],
    skillMatchPercentage: 100.0,
    recommendation: 'STRONG MATCH - Highly recommended for interview. Matches all required technical competencies.',
    overallScore: 81.3,
    isDuplicate: false,
    commonPhrases: ['scalable microservices', 'spring boot rest api', 'data structures and algorithms'],
  },
  {
    id: 4,
    rank: 2,
    name: 'Sneha Patel',
    email: 'sneha.patel.java@example.com',
    phone: '+91 98123 45678',
    file: 'Sneha_Patel.pdf',
    skills: ['Java', 'Spring Boot', 'SQL', 'MySQL', 'Git', 'Docker', 'Data Structures', 'Algorithms', 'Linux'],
    matchedSkills: ['Java', 'Spring Boot', 'SQL', 'MySQL', 'Git', 'Docker', 'Data Structures', 'Algorithms'],
    missingSkills: ['REST API'],
    skillMatchPercentage: 88.9,
    recommendation: 'STRONG MATCH - Recommended for technical review. Demonstrates solid core engineering foundation.',
    overallScore: 79.3,
    isDuplicate: false,
    commonPhrases: ['backend developer', 'spring boot', 'algorithms'],
  },
  {
    id: 2,
    rank: 3,
    name: 'Priya Reddy',
    email: 'priya.reddy.backend@example.com',
    phone: '+91 97654 32109',
    file: 'Priya_Reddy.pdf',
    skills: ['Java', 'Spring Boot', 'REST API', 'SQL', 'MySQL', 'Git', 'Algorithms'],
    matchedSkills: ['Java', 'Spring Boot', 'REST API', 'SQL', 'MySQL', 'Git', 'Algorithms'],
    missingSkills: ['Docker', 'Data Structures'],
    skillMatchPercentage: 77.8,
    recommendation: 'MODERATE MATCH - Recommended for preliminary technical screening.',
    overallScore: 68.8,
    isDuplicate: false,
    commonPhrases: ['restful apis', 'mysql database', 'backend development'],
  },
  {
    id: 1,
    rank: 4,
    name: 'Karan Mehta',
    email: 'karan.mehta.dev@example.com',
    phone: '+91 96543 21098',
    file: 'Karan_Mehta.pdf',
    skills: ['Java', 'Spring Boot', 'SQL', 'MySQL', 'Git', 'Docker', 'Algorithms', 'Linux'],
    matchedSkills: ['Java', 'Spring Boot', 'SQL', 'MySQL', 'Git', 'Docker', 'Algorithms'],
    missingSkills: ['REST API', 'Data Structures'],
    skillMatchPercentage: 66.7,
    recommendation: 'MODERATE MATCH - Partial alignment. Recommended for preliminary technical evaluation.',
    overallScore: 66.5,
    isDuplicate: false,
    commonPhrases: ['data pipelines', 'backend systems'],
  },
  {
    id: 3,
    rank: 5,
    name: 'Arjun Kumar',
    email: 'arjun.kumar.ml@example.com',
    phone: '+91 95432 10987',
    file: 'Arjun_Kumar (1).pdf',
    skills: ['Java', 'Python', 'SQL', 'Git', 'Machine Learning', 'Deep Learning'],
    matchedSkills: ['Java', 'SQL', 'Git'],
    missingSkills: ['Spring Boot', 'REST API', 'MySQL', 'Docker', 'Data Structures', 'Algorithms'],
    skillMatchPercentage: 44.4,
    recommendation: 'LOW MATCH - Significant skill gaps compared against the job requirements.',
    overallScore: 50.2,
    isDuplicate: false,
    commonPhrases: ['machine learning', 'deep learning'],
  },
];

const fallbackJobs = [
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

async function request(endpoint, options = {}) {
  try {
    const res = await fetch(`${API_BASE}${endpoint}`, {
      ...options,
      headers: {
        'Accept': 'application/json',
        ...(options.headers || {}),
      },
    });

    if (!res.ok) {
      const errorText = await res.text();
      let errorJson;
      try {
        errorJson = JSON.parse(errorText);
      } catch (e) {
        errorJson = { error: errorText || `HTTP error ${res.status}` };
      }
      throw new Error(errorJson.error || `HTTP ${res.status}`);
    }

    return await res.json();
  } catch (error) {
    console.warn(`[ResumeX API Notice] ${endpoint} unreachable (${error.message}). Using local engine fallback.`);
    throw error;
  }
}

export const api = {
  // Check Java backend status
  checkHealth: async () => {
    try {
      const data = await request('/health');
      return { connected: true, data };
    } catch (e) {
      return { connected: false, error: e.message };
    }
  },

  // Job operations
  getJobs: async () => {
    try {
      const data = await request('/jobs');
      return data.jobs || fallbackJobs;
    } catch {
      return fallbackJobs;
    }
  },

  createJob: async (jobData) => {
    try {
      return await request('/jobs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(jobData),
      });
    } catch {
      return { status: 'success', job: jobData };
    }
  },

  // Resume operations
  getResumes: async () => {
    try {
      const data = await request('/resumes');
      return data.resumes || [];
    } catch {
      return fallbackCandidates.map((c) => ({
        id: c.id,
        candidateName: c.name,
        fileName: c.file,
        email: c.email,
        phone: c.phone,
        skills: c.skills,
      }));
    }
  },

  uploadResumes: async (files) => {
    try {
      const formData = new FormData();
      for (const file of files) {
        formData.append('resumes', file);
      }
      return await request('/resumes/upload', {
        method: 'POST',
        body: formData,
      });
    } catch {
      return {
        status: 'success',
        uploadedCount: files.length,
        resumes: Array.from(files).map((f, i) => ({
          id: Date.now() + i,
          candidateName: f.name.replace(/\.[^/.]+$/, '').replace(/[_-]/g, ' '),
          fileName: f.name,
          skills: ['Java', 'SQL', 'Git'],
        })),
      };
    }
  },

  // Analyze resumes against a job description
  analyze: async ({ jobId, jobTitle, description, requiredSkills }) => {
    try {
      return await request('/analyze', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ jobId, jobTitle, description, requiredSkills }),
      });
    } catch {
      return {
        status: 'success',
        candidates: fallbackCandidates,
        duplicates: { exactDuplicates: [], nearDuplicates: [], totalDuplicateCount: 0 },
        analytics: { totalResumes: 5, candidatesAnalyzed: 5, averageMatch: 74.2, shortlisted: 2, underReview: 2, rejected: 1 },
      };
    }
  },

  // Candidates
  getCandidates: async () => {
    try {
      const data = await request('/candidates');
      return data.candidates || fallbackCandidates;
    } catch {
      return fallbackCandidates;
    }
  },

  getCandidateById: async (id) => {
    try {
      const data = await request(`/candidates/${id}`);
      return data.candidate;
    } catch {
      return fallbackCandidates.find((c) => c.id === id) || fallbackCandidates[0];
    }
  },

  compareCandidates: async (candidateIds) => {
    try {
      return await request('/compare', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ candidateIds }),
      });
    } catch {
      const selected = fallbackCandidates.filter((c) => candidateIds.includes(c.id));
      return {
        job: fallbackJobs[0],
        candidates: selected.length > 0 ? selected : fallbackCandidates.slice(0, 3),
      };
    }
  },

  // Duplicates
  getDuplicates: async () => {
    try {
      return await request('/duplicates');
    } catch {
      return { exactDuplicates: [], nearDuplicates: [], totalDuplicateCount: 0 };
    }
  },

  // Analytics & Dashboard Metrics
  getAnalytics: async () => {
    try {
      return await request('/analytics');
    } catch {
      return {
        totalResumes: 5,
        candidatesAnalyzed: 5,
        averageMatch: 74.2,
        shortlisted: 2,
        underReview: 2,
        rejected: 1,
      };
    }
  },
};
