import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { submissionApi } from '../api/submissionApi';
import { Badge } from '../components/common/Badge';
import { Button } from '../components/common/Button';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { Alert } from '../components/common/Alert';
import { Modal } from '../components/common/Modal';

export const SubmissionHistoryPage = () => {
  const [submissions, setSubmissions] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize] = useState(10);

  // Filters
  const [statusFilter, setStatusFilter] = useState('');
  const [languageFilter, setLanguageFilter] = useState('');

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Code inspection modal
  const [selectedSubmission, setSelectedSubmission] = useState(null);

  const fetchSubmissions = async () => {
    try {
      setLoading(true);
      setError(null);

      const params = {
        page: currentPage,
        size: pageSize,
        status: statusFilter || undefined,
        language: languageFilter || undefined,
        sortBy: 'submittedAt',
        sortDir: 'desc',
      };

      const res = await submissionApi.getMySubmissions(params);
      if (res && res.success && res.data) {
        setSubmissions(res.data.content || []);
        setTotalPages(res.data.totalPages || 0);
        setTotalElements(res.data.totalElements || 0);
      }
    } catch (err) {
      setError(err.message || 'Failed to load submission history.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSubmissions();
  }, [currentPage, statusFilter, languageFilter]);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      {/* Header */}
      <div>
        <h1 style={{ fontSize: '2rem', fontWeight: 800, letterSpacing: '-0.025em', marginBottom: '0.5rem' }}>
          My Submission History
        </h1>
        <p style={{ color: 'var(--text-secondary)' }}>
          Review all your past code evaluations, execution runtimes, and verdict logs.
        </p>
      </div>

      {/* Filter Bar */}
      <div
        style={{
          display: 'flex',
          flexWrap: 'wrap',
          gap: '1rem',
          alignItems: 'center',
          background: 'var(--bg-card)',
          padding: '1rem 1.25rem',
          borderRadius: 'var(--radius-lg)',
          border: '1px solid var(--border-color)',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <label style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>
            Status:
          </label>
          <select
            className="form-select"
            style={{ width: 'auto', padding: '0.35rem 0.75rem' }}
            value={statusFilter}
            onChange={(e) => {
              setStatusFilter(e.target.value);
              setCurrentPage(0);
            }}
          >
            <option value="">All Statuses</option>
            <option value="ACCEPTED">Accepted</option>
            <option value="WRONG_ANSWER">Wrong Answer</option>
            <option value="COMPILATION_ERROR">Compilation Error</option>
            <option value="RUNTIME_ERROR">Runtime Error</option>
            <option value="TIME_LIMIT_EXCEEDED">Time Limit Exceeded</option>
          </select>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <label style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>
            Language:
          </label>
          <select
            className="form-select"
            style={{ width: 'auto', padding: '0.35rem 0.75rem' }}
            value={languageFilter}
            onChange={(e) => {
              setLanguageFilter(e.target.value);
              setCurrentPage(0);
            }}
          >
            <option value="">All Languages</option>
            <option value="JAVA">Java</option>
            <option value="PYTHON">Python</option>
          </select>
        </div>
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError(null)} />}

      {/* Submissions Table */}
      {loading ? (
        <LoadingSpinner message="Loading submission records..." />
      ) : submissions.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem', marginBottom: '1rem' }}>
            No submissions found matching your filters.
          </p>
          <Link to="/problems">
            <Button variant="primary">Explore Problems to Solve &rarr;</Button>
          </Link>
        </div>
      ) : (
        <>
          <div className="table-container">
            <table className="custom-table">
              <thead>
                <tr>
                  <th style={{ width: '80px' }}># ID</th>
                  <th>Problem</th>
                  <th>Status</th>
                  <th>Language</th>
                  <th>Runtime</th>
                  <th>Passed Cases</th>
                  <th>Submitted At</th>
                  <th style={{ textAlign: 'right' }}>Action</th>
                </tr>
              </thead>
              <tbody>
                {submissions.map((sub) => (
                  <tr key={sub.id}>
                    <td style={{ color: 'var(--text-muted)', fontWeight: 600 }}>#{sub.id}</td>
                    <td>
                      <Link to={`/problems/${sub.problemId}`} style={{ fontWeight: 600, color: 'var(--text-primary)' }}>
                        {sub.problemTitle || `Problem #${sub.problemId}`}
                      </Link>
                    </td>
                    <td>
                      <Badge text={sub.status} />
                    </td>
                    <td>
                      <Badge text={sub.language} />
                    </td>
                    <td>{sub.executionTime != null ? `${sub.executionTime} ms` : '—'}</td>
                    <td>
                      {sub.totalTestCases > 0 ? (
                        <span style={{ fontWeight: 600 }}>
                          {sub.passedTestCases} / {sub.totalTestCases}
                        </span>
                      ) : (
                        '—'
                      )}
                    </td>
                    <td style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                      {new Date(sub.submittedAt).toLocaleString()}
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <Button size="sm" variant="outline" onClick={() => setSelectedSubmission(sub)}>
                        View Code
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: '0.5rem' }}>
            <span style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
              Showing {submissions.length} of {totalElements} submissions
            </span>

            <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
              <Button
                size="sm"
                variant="outline"
                disabled={currentPage === 0}
                onClick={() => setCurrentPage((p) => Math.max(0, p - 1))}
              >
                &larr; Previous
              </Button>
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', padding: '0 0.5rem' }}>
                Page {currentPage + 1} of {Math.max(1, totalPages)}
              </span>
              <Button
                size="sm"
                variant="outline"
                disabled={currentPage >= totalPages - 1}
                onClick={() => setCurrentPage((p) => p + 1)}
              >
                Next &rarr;
              </Button>
            </div>
          </div>
        </>
      )}

      {/* Code Inspection Modal */}
      <Modal
        isOpen={!!selectedSubmission}
        onClose={() => setSelectedSubmission(null)}
        title={`Submission #${selectedSubmission?.id} — ${selectedSubmission?.problemTitle || 'Problem'}`}
        size="lg"
      >
        {selectedSubmission && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
              <Badge text={selectedSubmission.status} />
              <Badge text={selectedSubmission.language} />
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                Passed: {selectedSubmission.passedTestCases} / {selectedSubmission.totalTestCases}
              </span>
              {selectedSubmission.executionTime && (
                <span style={{ color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                  Runtime: {selectedSubmission.executionTime} ms
                </span>
              )}
            </div>

            <pre
              style={{
                background: '#0d1117',
                padding: '1.25rem',
                borderRadius: 'var(--radius-md)',
                fontFamily: 'Fira Code, monospace',
                fontSize: '0.875rem',
                overflowX: 'auto',
                color: '#e6edf3',
                lineHeight: 1.5,
              }}
            >
              {selectedSubmission.sourceCode}
            </pre>

            {selectedSubmission.errorMessage && (
              <div>
                <h5 style={{ color: 'var(--danger)', marginBottom: '0.35rem' }}>Error Diagnostic Log:</h5>
                <pre
                  style={{
                    background: '#1a1117',
                    padding: '0.75rem',
                    borderRadius: 'var(--radius-md)',
                    color: '#f87171',
                    fontSize: '0.8rem',
                    whiteSpace: 'pre-wrap',
                  }}
                >
                  {selectedSubmission.errorMessage}
                </pre>
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
};

export default SubmissionHistoryPage;
