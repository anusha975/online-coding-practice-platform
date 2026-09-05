import React from 'react';
import { Badge } from '../common/Badge';

/**
 * Component to display verdict results from judge execution.
 */
export const ExecutionVerdict = ({ result, error, isRunning, onDismiss }) => {
  if (!result && !error && !isRunning) {
    return null;
  }

  if (error) {
    return (
      <div
        className="verdict-box"
        style={{
          background: 'rgba(239, 68, 68, 0.1)',
          borderColor: 'var(--danger-border)',
          marginTop: '1rem',
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
            <span style={{ fontSize: '1.25rem' }}>❌</span>
            <strong style={{ color: 'var(--danger)' }}>Submission Error</strong>
          </div>
          {onDismiss && (
            <button
              onClick={onDismiss}
              style={{ background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}
            >
              ✕
            </button>
          )}
        </div>
        <p style={{ marginTop: '0.5rem', color: 'var(--text-secondary)', fontSize: '0.9rem' }}>{error}</p>
      </div>
    );
  }

  if (isRunning || result?.status === 'PENDING' || result?.status === 'RUNNING') {
    return (
      <div
        className="verdict-box"
        style={{
          background: 'rgba(56, 189, 248, 0.1)',
          borderColor: 'var(--info-border)',
          marginTop: '1rem',
          display: 'flex',
          alignItems: 'center',
          gap: '1rem',
        }}
      >
        <div className="spinner" style={{ width: '24px', height: '24px', borderWidth: '3px' }} />
        <div>
          <h4 style={{ color: 'var(--info)', fontSize: '0.95rem', fontWeight: 600 }}>
            {result?.status === 'RUNNING' ? 'Running in Isolated Sandbox...' : 'Waiting in Execution Queue...'}
          </h4>
          <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
            Code is being compiled and tested against all test cases.
          </p>
        </div>
      </div>
    );
  }

  const isAccepted = result.status === 'ACCEPTED';
  const isWrongAnswer = result.status === 'WRONG_ANSWER';
  const isCompilationError = result.status === 'COMPILATION_ERROR';
  const isRuntimeError = result.status === 'RUNTIME_ERROR';
  const isTLE = result.status === 'TIME_LIMIT_EXCEEDED';

  const boxBg = isAccepted
    ? 'rgba(16, 185, 129, 0.12)'
    : isTLE
    ? 'rgba(245, 158, 11, 0.12)'
    : 'rgba(239, 68, 68, 0.12)';

  const boxBorder = isAccepted
    ? 'var(--success-border)'
    : isTLE
    ? 'var(--warning-border)'
    : 'var(--danger-border)';

  const headerTitle = isAccepted
    ? 'Accepted — All Test Cases Passed! 🎉'
    : isWrongAnswer
    ? 'Wrong Answer'
    : isCompilationError
    ? 'Compilation Error'
    : isRuntimeError
    ? 'Runtime Error'
    : isTLE
    ? 'Time Limit Exceeded'
    : result.status;

  return (
    <div
      className="verdict-box"
      style={{
        background: boxBg,
        borderColor: boxBorder,
        marginTop: '1rem',
      }}
    >
      {/* Verdict Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '0.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <Badge text={result.status} />
          <h3
            style={{
              fontSize: '1.05rem',
              fontWeight: 700,
              color: isAccepted ? 'var(--success)' : isTLE ? 'var(--warning)' : 'var(--danger)',
            }}
          >
            {headerTitle}
          </h3>
        </div>

        {onDismiss && (
          <button
            onClick={onDismiss}
            style={{ background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer', fontSize: '1rem' }}
          >
            ✕
          </button>
        )}
      </div>

      {/* Execution Metrics Grid */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(130px, 1fr))',
          gap: '0.75rem',
          marginTop: '1rem',
          padding: '0.75rem 1rem',
          background: 'rgba(0, 0, 0, 0.25)',
          borderRadius: 'var(--radius-md)',
          border: '1px solid rgba(255, 255, 255, 0.05)',
        }}
      >
        {/* Test Cases Counter */}
        <div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: 600 }}>
            Test Cases
          </div>
          <div style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--text-primary)' }}>
            {result.totalTestCases != null ? `${result.passedTestCases || 0} / ${result.totalTestCases}` : '—'}
          </div>
        </div>

        {/* Runtime */}
        <div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: 600 }}>
            Execution Time
          </div>
          <div style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--text-primary)' }}>
            {result.executionTime != null ? `${result.executionTime} ms` : '—'}
          </div>
        </div>

        {/* Memory */}
        <div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: 600 }}>
            Memory Used
          </div>
          <div style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--text-primary)' }}>
            {result.memoryUsed != null
              ? result.memoryUsed > 1024
                ? `${(result.memoryUsed / 1024).toFixed(1)} MB`
                : `${result.memoryUsed} KB`
              : '—'}
          </div>
        </div>

        {/* Language */}
        <div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: 600 }}>
            Language
          </div>
          <div style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--text-primary)' }}>
            {result.language || '—'}
          </div>
        </div>
      </div>

      {/* Compiler / Runtime Log Terminal */}
      {result.errorMessage && (
        <div style={{ marginTop: '1rem' }}>
          <div style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--danger)', marginBottom: '0.35rem' }}>
            Compiler / Runtime Log:
          </div>
          <pre
            style={{
              background: '#090d13',
              color: '#f87171',
              padding: '0.85rem 1rem',
              borderRadius: 'var(--radius-md)',
              border: '1px solid #30363d',
              fontFamily: 'Fira Code, monospace',
              fontSize: '0.825rem',
              lineHeight: 1.5,
              whiteSpace: 'pre-wrap',
              wordBreak: 'break-all',
              maxHeight: '220px',
              overflowY: 'auto',
            }}
          >
            {result.errorMessage}
          </pre>
        </div>
      )}
    </div>
  );
};

export default ExecutionVerdict;
