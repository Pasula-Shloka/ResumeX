/**
 * ResumeX API Client Service
 * Connects the React frontend to the Java DSA HttpServer running on http://localhost:8080.
 */

const API_BASE = 'http://localhost:8080/api';

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
    console.warn(`[ResumeX API Error] ${endpoint}:`, error.message);
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
    const data = await request('/jobs');
    return data.jobs || [];
  },

  createJob: async (jobData) => {
    return await request('/jobs', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(jobData),
    });
  },

  // Resume operations
  getResumes: async () => {
    const data = await request('/resumes');
    return data.resumes || [];
  },

  uploadResumes: async (files) => {
    // files is an array of File objects from <input type="file">
    const formData = new FormData();
    for (const file of files) {
      formData.append('resumes', file);
    }

    return await request('/resumes/upload', {
      method: 'POST',
      body: formData,
    });
  },

  // Analyze resumes against a job description
  analyze: async ({ jobId, jobTitle, description, requiredSkills }) => {
    return await request('/analyze', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ jobId, jobTitle, description, requiredSkills }),
    });
  },

  // Get full screening report
  getScreenings: async () => {
    return await request('/screenings');
  },

  // Candidates
  getCandidates: async () => {
    const data = await request('/candidates');
    return data.candidates || [];
  },

  getCandidateById: async (id) => {
    const data = await request(`/candidates/${id}`);
    return data.candidate;
  },

  compareCandidates: async (candidateIds) => {
    return await request('/compare', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ candidateIds }),
    });
  },

  // Duplicates
  getDuplicates: async () => {
    return await request('/duplicates');
  },

  // Analytics & Dashboard Metrics
  getAnalytics: async () => {
    return await request('/analytics');
  },

  // Algorithm Insights (Educational Viva Content)
  getAlgorithmExplanations: async () => {
    const data = await request('/algorithms/explain');
    return data.algorithms || [];
  },
};
