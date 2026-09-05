import React, { useState, useEffect } from 'react'
import { aiApi } from '../../api/aiApi'
import Button from '../common/Button'
import Badge from '../common/Badge'
import Alert from '../common/Alert'
import LoadingSpinner from '../common/LoadingSpinner'

/**
 * AI Code Review and Debugging Assistant Component.
 *
 * Provides deep, structured code review with severity classification (Confirmed Issue,
 * Possible Issue, Suggestion), edge case matrix, time/space complexity, and verdict diagnostics.
 */
export default function AiCodeReviewPanel({
  problem,
  userCode,
  language,
  lastVerdict,
  errorMessage,
  executionTime,
  memoryUsed,
}) {
  const [review, setReview] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  // Automatically fetch code review if not yet reviewed or when user requests
  const handleRunReview = async () => {
    if (!userCode || !userCode.trim()) {
      setError('Please write or submit some code in the editor before requesting a code review.')
      return
    }

    setLoading(true)
    setError(null)

    try {
      const payload = {
        problemId: problem?.id,
        problemTitle: problem?.title,
        problemCategory: problem?.category,
        problemDifficulty: problem?.difficulty,
        problemDescription: problem?.description || '',
        sourceCode: userCode,
        programmingLanguage: language || 'JAVA',
        verdict: lastVerdict || 'PENDING',
        errorMessage: errorMessage || '',
        executionTime: executionTime,
        memoryUsed: memoryUsed,
      }

      const res = await aiApi.reviewCode(payload)
      if (res.data && res.data.success && res.data.data) {
        setReview(res.data.data)
      } else {
        setError('Unable to complete code review at this time.')
      }
    } catch (err) {
      console.error('Code review failed:', err)
      const msg =
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Failed to connect to the AI Code Review engine.'
      setError(msg)
    } finally {
      setLoading(false)
    }
  }

  // Helper for Severity Badges
  const renderSeverityBadge = (severity) => {
    switch (severity) {
      case 'CONFIRMED_ISSUE':
        return (
          <span
            style={{
              fontSize: '0.72rem',
              fontWeight: 700,
              padding: '2px 8px',
              borderRadius: '999px',
              background: 'rgba(239, 68, 68, 0.2)',
              color: '#f87171',
              border: '1px solid rgba(239, 68, 68, 0.4)',
              display: 'inline-flex',
              alignItems: 'center',
              gap: '4px',
            }}
          >
            🔴 Confirmed Issue
          </span>
        )
      case 'POSSIBLE_ISSUE':
        return (
          <span
            style={{
              fontSize: '0.72rem',
              fontWeight: 700,
              padding: '2px 8px',
              borderRadius: '999px',
              background: 'rgba(245, 158, 11, 0.2)',
              color: '#fbbf24',
              border: '1px solid rgba(245, 158, 11, 0.4)',
              display: 'inline-flex',
              alignItems: 'center',
              gap: '4px',
            }}
          >
            🟡 Possible Issue
          </span>
        )
      default:
        return (
          <span
            style={{
              fontSize: '0.72rem',
              fontWeight: 700,
              padding: '2px 8px',
              borderRadius: '999px',
              background: 'rgba(16, 185, 129, 0.2)',
              color: '#34d399',
              border: '1px solid rgba(16, 185, 129, 0.4)',
              display: 'inline-flex',
              alignItems: 'center',
              gap: '4px',
            }}
          >
            🟢 Suggestion
          </span>
        )
    }
  }

  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        height: '100%',
        backgroundColor: 'var(--color-bg-surface, #1e293b)',
        overflow: 'hidden',
      }}
    >
      {/* Top Header Bar */}
      <div
        style={{
          padding: '12px 16px',
          background: 'linear-gradient(135deg, rgba(15, 23, 42, 0.9), rgba(30, 41, 59, 0.8))',
          borderBottom: '1px solid var(--color-border, #334155)',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{ fontSize: '1.25rem' }}>📝</span>
          <div>
            <h3 style={{ margin: 0, fontSize: '0.95rem', fontWeight: 600, color: '#f8fafc' }}>
              AI Code Review & Diagnostics
            </h3>
            <p style={{ margin: 0, fontSize: '0.75rem', color: '#94a3b8' }}>
              Educational feedback on correctness, complexity, bugs, and edge cases
            </p>
          </div>
        </div>

        <Button
          size="sm"
          variant="primary"
          onClick={handleRunReview}
          disabled={loading || !userCode}
          icon={loading ? '⏳' : '⚡'}
          style={{ background: 'linear-gradient(135deg, #6366f1, #3b82f6)' }}
        >
          {loading ? 'Analyzing...' : review ? 'Re-Analyze Code' : 'Review My Code'}
        </Button>
      </div>

      {/* Main Review Content Scroll Area */}
      <div
        style={{
          flex: 1,
          overflowY: 'auto',
          padding: '16px',
          display: 'flex',
          flexDirection: 'column',
          gap: '16px',
        }}
      >
        {error && <Alert type="error" message={error} onClose={() => setError(null)} />}

        {/* Empty State before running review */}
        {!review && !loading && (
          <div
            style={{
              textAlign: 'center',
              padding: '3rem 1.5rem',
              background: 'rgba(255, 255, 255, 0.02)',
              borderRadius: '8px',
              border: '1px dashed var(--color-border, #334155)',
              color: '#94a3b8',
            }}
          >
            <div style={{ fontSize: '2.5rem', marginBottom: '12px' }}>🔍</div>
            <h4 style={{ color: '#f8fafc', marginBottom: '8px', fontSize: '1.05rem' }}>
              Ready to Review Your Solution
            </h4>
            <p style={{ fontSize: '0.875rem', maxWidth: '440px', margin: '0 auto 1.5rem', lineHeight: 1.5 }}>
              Click <strong>"Review My Code"</strong> to analyze your implementation for potential bugs, time & space
              complexity ($O(N)$ vs $O(N^2)$), edge cases, and code quality recommendations.
            </p>
            <Button
              variant="primary"
              onClick={handleRunReview}
              disabled={!userCode}
              icon="⚡"
            >
              Analyze Active Editor Code
            </Button>
          </div>
        )}

        {/* Loading State */}
        {loading && (
          <div style={{ padding: '3rem', textAlign: 'center' }}>
            <LoadingSpinner size="md" message="AI Principal Reviewer is evaluating code correctness and complexity..." />
          </div>
        )}

        {/* Review Results Display */}
        {review && !loading && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
            {/* 1. Summary & Score Banner */}
            <div
              style={{
                background: 'linear-gradient(135deg, rgba(30, 41, 59, 0.9), rgba(15, 23, 42, 0.9))',
                border: '1px solid var(--color-border, #334155)',
                borderRadius: '8px',
                padding: '14px 16px',
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                <span style={{ fontSize: '0.8rem', textTransform: 'uppercase', fontWeight: 700, color: '#60a5fa' }}>
                  Executive Summary
                </span>
                {review.readabilityScore && (
                  <span
                    style={{
                      fontSize: '0.75rem',
                      fontWeight: 700,
                      padding: '2px 8px',
                      borderRadius: '4px',
                      background: 'rgba(59, 130, 246, 0.15)',
                      color: '#93c5fd',
                      border: '1px solid rgba(59, 130, 246, 0.3)',
                    }}
                  >
                    Quality Score: {review.readabilityScore}
                  </span>
                )}
              </div>
              <p style={{ margin: 0, color: '#f8fafc', fontSize: '0.9rem', lineHeight: 1.5 }}>
                {review.summary}
              </p>

              {/* Verdict Specific Diagnostic Note */}
              {review.verdictAnalysis && (
                <div
                  style={{
                    marginTop: '10px',
                    padding: '8px 12px',
                    borderRadius: '6px',
                    background:
                      lastVerdict === 'ACCEPTED'
                        ? 'rgba(16, 185, 129, 0.1)'
                        : 'rgba(239, 68, 68, 0.1)',
                    border:
                      lastVerdict === 'ACCEPTED'
                        ? '1px solid rgba(16, 185, 129, 0.3)'
                        : '1px solid rgba(239, 68, 68, 0.3)',
                    fontSize: '0.85rem',
                    color: lastVerdict === 'ACCEPTED' ? '#6ee7b7' : '#fca5a5',
                    display: 'flex',
                    alignItems: 'flex-start',
                    gap: '6px',
                  }}
                >
                  <span>{lastVerdict === 'ACCEPTED' ? '✅' : '⚠️'}</span>
                  <div>
                    <strong>Verdict Analysis:</strong> {review.verdictAnalysis}
                  </div>
                </div>
              )}
            </div>

            {/* 2. Bugs & Issues Section (with Severity Classification) */}
            <div>
              <h4 style={{ color: '#94a3b8', fontSize: '0.8rem', textTransform: 'uppercase', fontWeight: 700, marginBottom: '8px' }}>
                🐛 Bugs & Logic Issues ({review.bugs?.length || 0})
              </h4>
              {(!review.bugs || review.bugs.length === 0) ? (
                <div style={{ padding: '10px', background: 'rgba(16, 185, 129, 0.05)', border: '1px solid rgba(16, 185, 129, 0.2)', borderRadius: '6px', fontSize: '0.85rem', color: '#a7f3d0' }}>
                  ✓ No severe bugs or syntax errors detected in the reviewed code.
                </div>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  {review.bugs.map((bug, bIdx) => (
                    <div
                      key={bIdx}
                      style={{
                        background: '#090d13',
                        border: '1px solid var(--color-border, #334155)',
                        borderRadius: '6px',
                        padding: '10px 12px',
                      }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                          {renderSeverityBadge(bug.severity)}
                          <span style={{ fontWeight: 600, color: '#f8fafc', fontSize: '0.875rem' }}>
                            {bug.title}
                          </span>
                        </div>
                        {bug.lineReference && (
                          <span style={{ fontSize: '0.75rem', color: '#94a3b8', fontFamily: 'Fira Code, monospace' }}>
                            {bug.lineReference}
                          </span>
                        )}
                      </div>
                      <p style={{ margin: 0, color: '#cbd5e1', fontSize: '0.85rem', lineHeight: 1.5 }}>
                        {bug.description}
                      </p>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* 3. Complexity Breakdown Section */}
            <div>
              <h4 style={{ color: '#94a3b8', fontSize: '0.8rem', textTransform: 'uppercase', fontWeight: 700, marginBottom: '8px' }}>
                ⏱️ Algorithmic Complexity Analysis
              </h4>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
                {/* Time Complexity Card */}
                <div
                  style={{
                    background: '#090d13',
                    border: '1px solid var(--color-border, #334155)',
                    borderRadius: '6px',
                    padding: '10px 12px',
                  }}
                >
                  <div style={{ fontSize: '0.75rem', color: '#94a3b8', marginBottom: '4px' }}>Time Complexity</div>
                  <div style={{ fontSize: '1.1rem', fontWeight: 700, color: '#60a5fa', fontFamily: 'Fira Code, monospace' }}>
                    {review.timeComplexity || 'O(N)'}
                  </div>
                  <div style={{ fontSize: '0.8rem', color: '#94a3b8', marginTop: '4px', lineHeight: 1.4 }}>
                    {review.timeComplexityExplanation || 'Derived from loop nesting and operations.'}
                  </div>
                </div>

                {/* Space Complexity Card */}
                <div
                  style={{
                    background: '#090d13',
                    border: '1px solid var(--color-border, #334155)',
                    borderRadius: '6px',
                    padding: '10px 12px',
                  }}
                >
                  <div style={{ fontSize: '0.75rem', color: '#94a3b8', marginBottom: '4px' }}>Auxiliary Space</div>
                  <div style={{ fontSize: '1.1rem', fontWeight: 700, color: '#a855f7', fontFamily: 'Fira Code, monospace' }}>
                    {review.spaceComplexity || 'O(1)'}
                  </div>
                  <div style={{ fontSize: '0.8rem', color: '#94a3b8', marginTop: '4px', lineHeight: 1.4 }}>
                    {review.spaceComplexityExplanation || 'Memory used for auxiliary data structures.'}
                  </div>
                </div>
              </div>
            </div>

            {/* 4. Edge Cases Safety Matrix */}
            <div>
              <h4 style={{ color: '#94a3b8', fontSize: '0.8rem', textTransform: 'uppercase', fontWeight: 700, marginBottom: '8px' }}>
                🛡️ Edge Case Safety Check
              </h4>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                {review.edgeCases?.map((ec, ecIdx) => (
                  <div
                    key={ecIdx}
                    style={{
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center',
                      background: '#090d13',
                      border: '1px solid var(--color-border, #334155)',
                      borderRadius: '6px',
                      padding: '8px 12px',
                    }}
                  >
                    <div>
                      <div style={{ fontSize: '0.85rem', fontWeight: 600, color: '#f8fafc' }}>
                        {ec.caseDescription}
                      </div>
                      <div style={{ fontSize: '0.75rem', color: '#94a3b8' }}>{ec.impact}</div>
                    </div>
                    <div>
                      <span
                        style={{
                          fontSize: '0.75rem',
                          fontWeight: 600,
                          padding: '2px 8px',
                          borderRadius: '4px',
                          background: ec.isHandled ? 'rgba(16, 185, 129, 0.15)' : 'rgba(239, 68, 68, 0.15)',
                          color: ec.isHandled ? '#6ee7b7' : '#fca5a5',
                          border: ec.isHandled ? '1px solid rgba(16, 185, 129, 0.3)' : '1px solid rgba(239, 68, 68, 0.3)',
                        }}
                      >
                        {ec.isHandled ? '✓ Handled' : '⚠️ Risk'}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* 5. Improvement Suggestions */}
            {review.suggestions && review.suggestions.length > 0 && (
              <div>
                <h4 style={{ color: '#94a3b8', fontSize: '0.8rem', textTransform: 'uppercase', fontWeight: 700, marginBottom: '8px' }}>
                  💡 Actionable Improvement Suggestions
                </h4>
                <ul
                  style={{
                    margin: 0,
                    paddingLeft: '1.2rem',
                    background: '#090d13',
                    border: '1px solid var(--color-border, #334155)',
                    borderRadius: '6px',
                    padding: '10px 14px 10px 28px',
                    color: '#cbd5e1',
                    fontSize: '0.85rem',
                    lineHeight: 1.6,
                  }}
                >
                  {review.suggestions.map((sug, sIdx) => (
                    <li key={sIdx} style={{ marginBottom: '4px' }}>
                      {sug}
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  )
}
