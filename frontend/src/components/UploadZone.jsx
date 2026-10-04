import React, { useRef, useState } from 'react';
import { UploadCloud, FileText, X, CheckCircle2, AlertCircle } from 'lucide-react';

export default function UploadZone({ onFilesSelected, uploadedFiles = [], uploading = false }) {
  const [dragOver, setDragOver] = useState(false);
  const fileInputRef = useRef(null);

  const handleDragOver = (e) => {
    e.preventDefault();
    setDragOver(true);
  };

  const handleDragLeave = (e) => {
    e.preventDefault();
    setDragOver(false);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setDragOver(false);
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      filterAndAddFiles(Array.from(e.dataTransfer.files));
    }
  };

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files.length > 0) {
      filterAndAddFiles(Array.from(e.target.files));
    }
  };

  const filterAndAddFiles = (newFiles) => {
    const pdfs = newFiles.filter(file => file.name.toLowerCase().endsWith('.pdf'));
    if (pdfs.length < newFiles.length) {
      alert("Only PDF resume files (.pdf) are supported.");
    }
    if (pdfs.length > 0) {
      onFilesSelected(pdfs);
    }
  };

  const formatFileSize = (bytes) => {
    if (!bytes) return '0 KB';
    const kb = bytes / 1024;
    return kb > 1024 ? `${(kb / 1024).toFixed(1)} MB` : `${Math.round(kb)} KB`;
  };

  return (
    <div className="upload-container">
      <div
        className={`upload-dropzone ${dragOver ? 'dragover' : ''} ${uploading ? 'uploading' : ''}`}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
        onClick={() => fileInputRef.current?.click()}
      >
        <input
          ref={fileInputRef}
          type="file"
          multiple
          accept=".pdf"
          style={{ display: 'none' }}
          onChange={handleFileChange}
        />
        <div className="upload-icon-circle">
          <UploadCloud size={28} />
        </div>
        <div className="upload-text">
          <h3>Choose PDF Resumes or Drag & Drop Here</h3>
          <p>Supports multiple PDF uploads at runtime for live text extraction by Apache PDFBox.</p>
        </div>
        <button
          type="button"
          className="secondary-button"
          disabled={uploading}
          onClick={(e) => {
            e.stopPropagation();
            fileInputRef.current?.click();
          }}
        >
          Select PDF Files
        </button>
      </div>

      {uploadedFiles.length > 0 && (
        <div className="selected-files-list">
          <div className="selected-files-header">
            <h4>Uploaded Resumes ({uploadedFiles.length})</h4>
            <small>Extracted & ready for Java DSA matching</small>
          </div>
          <div className="files-scroll-list">
            {uploadedFiles.map((file, idx) => (
              <div className="file-item-card" key={idx}>
                <div className="file-item-left">
                  <FileText size={18} className="file-icon" />
                  <div>
                    <strong>{file.name || file.fileName || file.file}</strong>
                    <span>{file.size ? formatFileSize(file.size) : 'PDF Document'}</span>
                  </div>
                </div>
                <div className="file-item-right">
                  <span className="file-status-badge success">
                    <CheckCircle2 size={13} /> Extracted
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
