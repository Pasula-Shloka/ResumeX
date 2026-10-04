import React from 'react';
import { Check, Loader2 } from 'lucide-react';

const pipelineSteps = [
  "PDF Uploaded",
  "PDFBox Text Extraction",
  "Normalization & Tokenization",
  "KMP Skill Matching",
  "KMP Keyword Matching",
  "Levenshtein Edit Distance",
  "Needleman-Wunsch Alignment",
  "Suffix Array Construction",
  "Kasai LCP Prefix Computation",
  "SHA-256 Text Hashing",
  "Greedy Approximation",
  "Weighted Score Calculation",
  "Resume Ranking & Duplicate Audit",
];

export default function PipelineVisualizer({ currentStep = 13, isRunning = false }) {
  return (
    <div className="pipeline-container">
      <div className="pipeline-list">
        {pipelineSteps.map((step, index) => {
          const isDone = index < currentStep;
          const isCurrent = isRunning && index === currentStep;

          return (
            <div
              key={step}
              className={`pipeline-step ${isDone ? 'done' : ''} ${isCurrent ? 'active' : ''}`}
            >
              <div className="step-indicator">
                {isDone ? (
                  <Check size={14} />
                ) : isCurrent ? (
                  <Loader2 size={14} className="spin" />
                ) : (
                  <span>{index + 1}</span>
                )}
              </div>
              <div className="step-info">
                <strong>{step}</strong>
                <small>{getStepSubtext(index)}</small>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

function getStepSubtext(index) {
  switch (index) {
    case 0: return "Multi-file staging";
    case 1: return "Apache PDFBox Loader.loadPDF()";
    case 2: return "Whitespace & case normalization";
    case 3: return "LPS array search for required skills";
    case 4: return "Domain keyword frequency matching";
    case 5: return "Dynamic programming edit matrix";
    case 6: return "Global skill sequence alignment";
    case 7: return "Lexicographically sorted suffix indices";
    case 8: return "Kasai O(n) longest common prefix";
    case 9: return "256-bit cryptographic fingerprinting";
    case 10: return "Bipartite fuzzy matching optimization";
    case 11: return "Weighted composite algorithm formula";
    case 12: return "QuickSort ranking & duplicate detector";
    default: return "DSA pipeline stage";
  }
}
