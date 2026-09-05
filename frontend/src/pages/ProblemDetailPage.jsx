import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { problemApi } from '../api/problemApi';
import { submissionApi } from '../api/submissionApi';
import { useAuth } from '../context/AuthContext';
import { Badge } from '../components/common/Badge';
import { Button } from '../components/common/Button';
import { Alert } from '../components/common/Alert';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { Modal } from '../components/common/Modal';
import { CodeEditor, DEFAULT_JAVA_BOILERPLATE, DEFAULT_PYTHON_BOILERPLATE } from '../components/editor/CodeEditor';
import { ExecutionVerdict } from '../components/editor/ExecutionVerdict';

export const ProblemDetailPage = () => {
  const { id } = useParams();
  const { isAuthenticated } = useAuth();

  const [problem, setProblem] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Left Panel Tabs: 'description' | 'submissions'
  const [activeTab, setActiveTab] = useState('description');
  const [problemSubmissions, setProblemSubmissions] = useState([]);
  const [loadingSubmissions, setLoadingSubmissions] = useState(false);

  // Code Editor State
  const [language, setLanguage] = useState('JAVA');
  const [sourceCode, setSourceCode] = useState(DEFAULT_JAVA_BOILERPLATE);

  // Submission / Execution State
  const [submitting, setSubmitting] = useState(false);
  const [runningSample, setRunningSample] = useState(false);
  const [submissionResult, setSubmissionResult] = useState(null);
  const [submitError, setSubmitError] = useState(null);

  // Modal for Viewing Past Code
  const [selectedSubmission, setSelectedSubmission] = useState(null);

  // 1. Fetch Problem Details
  useEffect(() => {
    const fetchProblem = async () => {
      try {
        setLoading(true);
        setError(null);
        const res = await problemApi.getProblemById(id);
        if (res && res.success && res.data) {
          setProblem(res.data);
        }
      } catch (err) {
        setError(err.message || 'Failed to load problem statement.');
      } finally {
        setLoading(false);
      }
    };

    fetchProblem();
  }, [id]);

  // 2. Load cached code from localStorage when problem ID or language changes
  useEffect(() => {
    if (!id) return;
    const storageKey = `oj_code_${id}_${language}`;
    const cached = localStorage.getItem(storageKey);

    if (cached !== null) {
      setSourceCode(cached);
    } else {
      setSourceCode(language === 'JAVA' ? DEFAULT_JAVA_BOILERPLATE : DEFAULT_PYTHON_BOILERPLATE);
    }
  }, [id, language]);

  // 3. Save code to localStorage on modification
  const handleCodeChange = (newCode) => {
    setSourceCode(newCode);
    if (id) {
      localStorage.setItem(`oj_code_${id}_${language}`, newCode);
    }
  };

  // 4. Handle Language Switch
  const handleLanguageChange = (newLang) => {
    setLanguage(newLang);
    const storageKey = `oj_code_${id}_${newLang}`;
    const cached = localStorage.getItem(storageKey);
    if (cached !== null) {
      setSourceCode(cached);
    } else {
      setSourceCode(newLang === 'JAVA' ? DEFAULT_JAVA_BOILERPLATE : DEFAULT_PYTHON_BOILERPLATE);
    }
  };

  // 5. Fetch User Submissions for this problem
  const fetchSubmissionsForProblem = useCallback(async () => {
    if (!isAuthenticated || !id) return;
    try {
      setLoadingSubmissions(true);
      const res = await submissionApi.getProblemSubmissions(id, { page: 0, size: 20 });
      if (res && res.success && res.data) {
        setProblemSubmissions(res.data.content || []);
      }
    } catch (err) {
      console.error('Failed to load past submissions', err);
    } finally {
      setLoadingSubmissions(false);
    }
  }, [isAuthenticated, id]);

  useEffect(() => {
    if (activeTab === 'submissions') {
      fetchSubmissionsForProblem();
    }
  }, [activeTab, fetchSubmissionsForProblem]);

  // 6. Submit Code to Full Judge Engine
  const handleSubmitCode = async () => {
    if (!isAuthenticated) {
      setSubmitError('Please sign in to submit your solution.');
      return;
    }

    if (!sourceCode.trim()) {
      setSubmitError('Source code cannot be empty.');
      return;
    }

    try {
      setSubmitting(true);
      setSubmitError(null);
      setSubmissionResult({ status: 'PENDING', message: 'Submitted! Waiting in execution queue...' });

      const createRes = await submissionApi.submitCode({
        problemId: Number(id),
        language,
        sourceCode,
      });

      if (createRes && createRes.success && createRes.data) {
        const submissionId = createRes.data.id;

        // Poll every 800ms for terminal status
        let attempts = 0;
        const maxAttempts = 25;

        const pollInterval = setInterval(async () => {
          attempts++;
          try {
            const pollRes = await submissionApi.getSubmissionById(submissionId);
            if (pollRes && pollRes.success && pollRes.data) {
              const currentStatus = pollRes.data.status;
              setSubmissionResult(pollRes.data);

              if (currentStatus !== 'PENDING' && currentStatus !== 'RUNNING') {
                clearInterval(pollInterval);
                setSubmitting(false);
                if (activeTab === 'submissions') {
                  fetchSubmissionsForProblem();
                }
              }
            }
          } catch (pollErr) {
            console.error('Polling error', pollErr);
          }

          if (attempts >= maxAttempts) {
            clearInterval(pollInterval);
            setSubmitting(false);
          }
        }, 800);
      }
    } catch (err) {
      setSubmitError(err.message || 'Submission failed.');
      setSubmitting(false);
    }
  };

  // 7. Run Sample Test Case
  const handleRunSample = async () => {
    // Executes code and displays feedback
    await handleSubmitCode();
  };

  if (loading) {
    return <LoadingSpinner message="Loading problem workspace..." />;
  }

  if (error || !problem) {
    return (
      <div style={{ maxWidth: '600px', margin: '3rem auto' }}>
        <Alert type="error" message={error || 'Problem not found.'} />
        <Link to="/problems">
          <Button variant="primary">&larr; Back to Problems</Button>
        </Link>
      </div>
    );
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
      {/* Top Header Bar */}
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          background: 'var(--bg-card)',
          padding: '0.85rem 1.25rem',
          borderRadius: 'var(--radius-lg)',
          border: '1px solid var(--border-color)',
          flexWrap: 'wrap',
          gap: '0.75rem',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', flexWrap: 'wrap' }}>
          <Link to="/problems" style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', fontWeight: 600 }}>
            &larr; Problems
          </Link>
          <h2 style={{ fontSize: '1.25rem', fontWeight: 700, margin: 0 }}>
            {problem.id}. {problem.title}
          </h2>
          <Badge text={problem.difficulty} />
          <span
            style={{
              background: 'var(--bg-tertiary)',
              padding: '0.2rem 0.6rem',
              borderRadius: 'var(--radius-sm)',
              color: 'var(--purple)',
              fontSize: '0.8rem',
              fontWeight: 600,
            }}
          >
            {problem.category}
          </span>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem', color: 'var(--text-secondary)', fontSize: '0.85rem' }}>
          <span>⏱️ Time: <strong>{problem.timeLimitMs}ms</strong></span>
          <span>💾 Memory: <strong>{problem.memoryLimitMb}MB</strong></span>
        </div>
      </div>

      {/* Split Workspace */}
      <div className="workspace-grid">
        {/* LEFT PANEL: Statement & Submissions Tabs */}
        <div className="workspace-panel">
          <div className="workspace-panel-header">
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <Button
                size="sm"
                variant={activeTab === 'description' ? 'primary' : 'outline'}
                onClick={() => setActiveTab('description')}
              >
                Description
              </Button>
              <Button
                size="sm"
                variant={activeTab === 'submissions' ? 'primary' : 'outline'}
                onClick={() => setActiveTab('submissions')}
              >
                Submissions {problemSubmissions.length > 0 ? `(${problemSubmissions.length})` : ''}
              </Button>
            </div>
          </div>

          <div className="workspace-panel-body">
            {activeTab === 'description' ? (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
                {/* Problem Statement */}
                <div>
                  <h4 style={{ color: 'var(--text-secondary)', fontSize: '0.8rem', textTransform: 'uppercase', marginBottom: '0.5rem', fontWeight: 700 }}>
                    Problem Statement
                  </h4>
                  <div style={{ whiteSpace: 'pre-wrap', lineHeight: 1.7, fontSize: '0.95rem' }}>
                    {problem.description}
                  </div>
                </div>

                {/* Input Format */}
                {problem.inputFormat && (
                  <div>
                    <h4 style={{ color: 'var(--text-secondary)', fontSize: '0.8rem', textTransform: 'uppercase', marginBottom: '0.4rem', fontWeight: 700 }}>
                      Input Format
                    </h4>
                    <div style={{ whiteSpace: 'pre-wrap', color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                      {problem.inputFormat}
                    </div>
                  </div>
                )}

                {/* Output Format */}
                {problem.outputFormat && (
                  <div>
                    <h4 style={{ color: 'var(--text-secondary)', fontSize: '0.8rem', textTransform: 'uppercase', marginBottom: '0.4rem', fontWeight: 700 }}>
                      Output Format
                    </h4>
                    <div style={{ whiteSpace: 'pre-wrap', color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                      {problem.outputFormat}
                    </div>
                  </div>
                )}

                {/* Constraints */}
                {problem.constraints && (
                  <div>
                    <h4 style={{ color: 'var(--text-secondary)', fontSize: '0.8rem', textTransform: 'uppercase', marginBottom: '0.4rem', fontWeight: 700 }}>
                      Constraints
                    </h4>
                    <div
                      style={{
                        background: 'var(--bg-tertiary)',
                        padding: '0.75rem 1rem',
                        borderRadius: 'var(--radius-md)',
                        fontSize: '0.875rem',
                        fontFamily: 'Fira Code, monospace',
                        whiteSpace: 'pre-wrap',
                        border: '1px solid var(--border-subtle)',
                      }}
                    >
                      {problem.constraints}
                    </div>
                  </div>
                )}

                {/* Sample Test Case */}
                {(problem.sampleInput || problem.sampleOutput) && (
                  <div>
                    <h4 style={{ color: 'var(--text-secondary)', fontSize: '0.8rem', textTransform: 'uppercase', marginBottom: '0.5rem', fontWeight: 700 }}>
                      Sample Test Case
                    </h4>
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
                      <div>
                        <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Sample Input</span>
                        <pre
                          style={{
                            background: '#090d13',
                            padding: '0.75rem',
                            borderRadius: 'var(--radius-md)',
                            border: '1px solid var(--border-color)',
                            fontSize: '0.85rem',
                            overflowX: 'auto',
                            marginTop: '0.25rem',
                            fontFamily: 'Fira Code, monospace',
                          }}
                        >
                          {problem.sampleInput || 'No input'}
                        </pre>
                      </div>
                      <div>
                        <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Sample Output</span>
                        <pre
                          style={{
                            background: '#090d13',
                            padding: '0.75rem',
                            borderRadius: 'var(--radius-md)',
                            border: '1px solid var(--border-color)',
                            fontSize: '0.85rem',
                            overflowX: 'auto',
                            marginTop: '0.25rem',
                            fontFamily: 'Fira Code, monospace',
                          }}
                        >
                          {problem.sampleOutput || 'No output'}
                        </pre>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            ) : (
              /* Submissions Tab */
              <div>
                {!isAuthenticated ? (
                  <Alert type="info" message="Sign in to view your submission history for this problem." />
                ) : loadingSubmissions ? (
                  <LoadingSpinner size="sm" message="Loading past submissions..." />
                ) : problemSubmissions.length === 0 ? (
                  <p style={{ color: 'var(--text-secondary)', textAlign: 'center', padding: '2rem' }}>
                    You haven't submitted any solutions for this problem yet.
                  </p>
                ) : (
                  <table className="custom-table">
                    <thead>
                      <tr>
                        <th>Status</th>
                        <th>Language</th>
                        <th>Time</th>
                        <th>Submitted</th>
                        <th>Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      {problemSubmissions.map((sub) => (
                        <tr key={sub.id}>
                          <td>
                            <Badge text={sub.status} />
                          </td>
                          <td>
                            <Badge text={sub.language} />
                          </td>
                          <td>{sub.executionTime != null ? `${sub.executionTime}ms` : '—'}</td>
                          <td style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                            {new Date(sub.submittedAt).toLocaleTimeString()}
                          </td>
                          <td>
                            <Button size="sm" variant="outline" onClick={() => setSelectedSubmission(sub)}>
                              View Code
                            </Button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}
              </div>
            )}
          </div>
        </div>

        {/* RIGHT PANEL: Modular Code Editor & Execution Results */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <CodeEditor
            problemId={id}
            language={language}
            onLanguageChange={handleLanguageChange}
            code={sourceCode}
            onChange={handleCodeChange}
            onSubmit={handleSubmitCode}
            onRun={handleRunSample}
            isSubmitting={submitting}
            isRunning={runningSample}
          />

          {/* Execution Verdict / Status Display */}
          <ExecutionVerdict
            result={submissionResult}
            error={submitError}
            isRunning={submitting || runningSample}
            onDismiss={() => {
              setSubmissionResult(null);
              setSubmitError(null);
            }}
          />
        </div>
      </div>

      {/* Code Viewer Modal */}
      <Modal
        isOpen={!!selectedSubmission}
        onClose={() => setSelectedSubmission(null)}
        title={`Submission #${selectedSubmission?.id} (${selectedSubmission?.language})`}
        size="lg"
      >
        {selectedSubmission && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
              <Badge text={selectedSubmission.status} />
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.85rem' }}>
                Passed: {selectedSubmission.passedTestCases}/{selectedSubmission.totalTestCases}
              </span>
              {selectedSubmission.executionTime != null && (
                <span style={{ color: 'var(--text-secondary)', fontSize: '0.85rem' }}>
                  Execution: {selectedSubmission.executionTime}ms
                </span>
              )}
            </div>

            <pre
              style={{
                background: '#090d13',
                padding: '1rem',
                borderRadius: 'var(--radius-md)',
                fontFamily: 'Fira Code, monospace',
                fontSize: '0.85rem',
                overflowX: 'auto',
                color: '#e6edf3',
                lineHeight: 1.5,
                border: '1px solid var(--border-color)',
              }}
            >
              {selectedSubmission.sourceCode}
            </pre>

            {selectedSubmission.errorMessage && (
              <div>
                <h5 style={{ color: 'var(--danger)', marginBottom: '0.35rem' }}>Compiler / Runtime Error Log:</h5>
                <pre
                  style={{
                    background: '#1a1117',
                    padding: '0.75rem',
                    borderRadius: 'var(--radius-md)',
                    color: '#f87171',
                    fontSize: '0.8rem',
                    whiteSpace: 'pre-wrap',
                    fontFamily: 'Fira Code, monospace',
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

export default ProblemDetailPage;
