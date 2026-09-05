import React, { useState, useEffect } from 'react'
import { aiApi } from '../../api/aiApi'
import Button from '../common/Button'
import Badge from '../common/Badge'
import Alert from '../common/Alert'
import LoadingSpinner from '../common/LoadingSpinner'

/**
 * Interactive Progressive Hint Component for CodeForge.
 *
 * Implements progressive pedagogical hinting (Levels 1 to 4) and error/mistake diagnosis
 * without prematurely giving away full solutions. Tracks hints used per session.
 */
export default function ProgressiveHintSection({
  problem,
  userCode,
  language,
  lastVerdict,
  errorMessage,
}) {
  const problemId = problem?.id || 'default'
  const storageKey = `codeforge_hints_${problemId}`

  // State
  const [hints, setHints] = useState([])
  const [currentLevel, setCurrentLevel] = useState(0)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [showSolutionConfirm, setShowSolutionConfirm] = useState(false)

  // Load session hints from localStorage if available
  useEffect(() => {
    try {
      const saved = localStorage.getItem(storageKey)
      if (saved) {
        const parsed = JSON.parse(saved)
        if (Array.isArray(parsed) && parsed.length > 0) {
          setHints(parsed)
          const maxLvl = Math.max(...parsed.map((h) => h.hintLevel || 1))
          setCurrentLevel(maxLvl)
        }
      }
    } catch (e) {
      console.error('Failed to parse cached hints:', e)
    }
  }, [problemId])

  // Save hints to local session storage
  const saveHints = (newHints) => {
    setHints(newHints)
    try {
      localStorage.setItem(storageKey, JSON.stringify(newHints))
    } catch (e) {
      console.error('Failed to cache hints:', e)
    }
  }

  // Request a hint for a specific level or mode
  const fetchHint = async (level, mode = 'HINT') => {
    setLoading(true)
    setError(null)

    try {
      const previousHintContents = hints.map((h) => `${h.title}: ${h.content}`)

      const payload = {
        problemId: problem?.id,
        problemTitle: problem?.title,
        problemCategory: problem?.category,
        problemDifficulty: problem?.difficulty,
        problemDescription: problem?.description || 'Algorithmic practice problem',
        userCode: userCode || '',
        programmingLanguage: language || 'JAVA',
        previousHints: previousHintContents,
        requestedHintLevel: level,
        mode: mode,
        verdict: lastVerdict || '',
        errorMessage: errorMessage || '',
      }

      const res = await aiApi.getHint(payload)
      if (res.data && res.data.success && res.data.data) {
        const hintData = res.data.data
        const updated = [...hints, hintData]
        saveHints(updated)
        setCurrentLevel(level)
      } else {
        setError('Unable to generate hint at this time. Please try again.')
      }
    } catch (err) {
      console.error('Hint generation error:', err)
      const msg =
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Failed to connect to the AI Mentor. Please check backend connection.'
      setError(msg)
    } finally {
      setLoading(false)
      setShowSolutionConfirm(false)
    }
  }

  // Handle Get Initial Hint or Next Hint
  const handleGetHint = () => {
    const nextLvl = currentLevel === 0 ? 1 : Math.min(currentLevel + 1, 3)
    fetchHint(nextLvl, 'HINT')
  }

  // Handle Explain My Mistake
  const handleExplainMistake = () => {
    fetchHint(2, 'MISTAKE')
  }

  // Handle Show Solution (Level 4)
  const handleShowSolution = () => {
    fetchHint(4, 'SOLUTION')
  }

  // Reset hints for this problem
  const handleResetSession = () => {
    setHints([])
    setCurrentLevel(0)
    setError(null)
    localStorage.removeItem(storageKey)
  }

  // Render markdown text with bolding, code blocks, lists
  const renderFormattedContent = (content) => {
    if (!content) return null

    // Split by code blocks ```
    const parts = content.split(/(```[\s\S]*?```)/g)

    return (
      <div style={{ lineHeight: 1.6, fontSize: '0.92rem' }}>
        {parts.map((part, idx) => {
          if (part.startsWith('```')) {
            const firstLineBreak = part.indexOf('\n')
            const langLabel = part.substring(3, firstLineBreak).trim()
            const codeBody = part.substring(firstLineBreak + 1, part.length - 3)

            return (
              <div
                key={idx}
                style={{
                  background: '#090d13',
                  border: '1px solid var(--border-color)',
                  borderRadius: 'var(--radius-md)',
                  margin: '0.75rem 0',
                  overflow: 'hidden',
                }}
              >
                {langLabel && (
                  <div
                    style={{
                      background: '#131b26',
                      padding: '0.25rem 0.75rem',
                      fontSize: '0.75rem',
                      color: 'var(--text-muted)',
                      borderBottom: '1px solid var(--border-color)',
                      fontFamily: 'Fira Code, monospace',
                      textTransform: 'uppercase',
                    }}
                  >
                    {langLabel}
                  </div>
                )}
                <pre
                  style={{
                    padding: '0.875rem',
                    margin: 0,
                    overflowX: 'auto',
                    fontFamily: 'Fira Code, monospace',
                    fontSize: '0.85rem',
                    color: '#e2e8f0',
                  }}
                >
                  {codeBody}
                </pre>
              </div>
            )
          }

          // Plain text sections with markdown formatting
          const paragraphs = part.split('\n\n')
          return paragraphs.map((p, pIdx) => {
            if (!p.trim()) return null
            if (p.startsWith('### ')) {
              return (
                <h4 key={`${idx}-${pIdx}`} style={{ color: '#60a5fa', margin: '0.75rem 0 0.4rem', fontWeight: 600 }}>
                  {p.replace('### ', '')}
                </h4>
              )
            }
            if (p.startsWith('#### ')) {
              return (
                <h5 key={`${idx}-${pIdx}`} style={{ color: '#93c5fd', margin: '0.6rem 0 0.3rem', fontWeight: 600 }}>
                  {p.replace('#### ', '')}
                </h5>
              )
            }
            if (p.startsWith('> ')) {
              return (
                <div
                  key={`${idx}-${pIdx}`}
                  style={{
                    borderLeft: '3px solid #3b82f6',
                    padding: '0.5rem 0.75rem',
                    background: 'rgba(59, 130, 246, 0.08)',
                    borderRadius: '0 var(--radius-sm) var(--radius-sm) 0',
                    margin: '0.5rem 0',
                    color: '#bfdbfe',
                    fontStyle: 'italic',
                  }}
                >
                  {p.replace('> ', '')}
                </div>
              )
            }
            return (
              <p key={`${idx}-${pIdx}`} style={{ margin: '0.4rem 0', color: 'var(--text-primary)' }}>
                {p}
              </p>
            )
          })
        })}
      </div>
    )
  }

  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        height: '100%',
        background: 'var(--bg-secondary)',
        borderRadius: 'var(--radius-lg)',
        border: '1px solid var(--border-color)',
        overflow: 'hidden',
      }}
    >
      {/* Header with Hint Stepper & Session Tracker */}
      <div
        style={{
          padding: '1rem',
          background: 'linear-gradient(135deg, rgba(30, 41, 59, 0.8), rgba(15, 23, 42, 0.95))',
          borderBottom: '1px solid var(--border-color)',
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <span style={{ fontSize: '1.25rem' }}>💡</span>
            <div>
              <h3 style={{ margin: 0, fontSize: '1rem', fontWeight: 600, color: '#f8fafc' }}>
                Progressive Hint System
              </h3>
              <p style={{ margin: 0, fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
                Solve step-by-step without spoiling the solution
              </p>
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <span
              style={{
                fontSize: '0.75rem',
                padding: '0.2rem 0.6rem',
                borderRadius: '999px',
                background: hints.length > 0 ? 'rgba(59, 130, 246, 0.15)' : 'rgba(148, 163, 184, 0.1)',
                color: hints.length > 0 ? '#60a5fa' : 'var(--text-muted)',
                fontWeight: 600,
                border: '1px solid rgba(59, 130, 246, 0.3)',
              }}
            >
              Hints Used: {hints.length}/3
            </span>
            {hints.length > 0 && (
              <Button size="sm" variant="ghost" onClick={handleResetSession} title="Reset Hint Session">
                🔄 Reset
              </Button>
            )}
          </div>
        </div>

        {/* Stepper Indicator */}
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(4, 1fr)',
            gap: '0.5rem',
            background: 'rgba(0, 0, 0, 0.25)',
            padding: '0.5rem',
            borderRadius: 'var(--radius-md)',
          }}
        >
          {[
            { lvl: 1, label: '1. Concept', desc: 'Pattern & Idea' },
            { lvl: 2, label: '2. Logic', desc: 'Targeted Flaw' },
            { lvl: 3, label: '3. Steps', desc: 'Pseudocode' },
            { lvl: 4, label: '4. Solution', desc: 'Full Code' },
          ].map((step) => {
            const isUnlocked = hints.some((h) => h.hintLevel === step.lvl)
            const isCurrent = currentLevel === step.lvl

            return (
              <div
                key={step.lvl}
                style={{
                  padding: '0.4rem 0.5rem',
                  borderRadius: 'var(--radius-sm)',
                  textAlign: 'center',
                  background: isCurrent
                    ? 'rgba(59, 130, 246, 0.25)'
                    : isUnlocked
                    ? 'rgba(16, 185, 129, 0.15)'
                    : 'transparent',
                  border: isCurrent
                    ? '1px solid #3b82f6'
                    : isUnlocked
                    ? '1px solid rgba(16, 185, 129, 0.4)'
                    : '1px solid transparent',
                  transition: 'all 0.2s ease',
                }}
              >
                <div
                  style={{
                    fontSize: '0.75rem',
                    fontWeight: 600,
                    color: isCurrent ? '#93c5fd' : isUnlocked ? '#6ee7b7' : 'var(--text-muted)',
                  }}
                >
                  {isUnlocked ? '✓ ' : ''}
                  {step.label}
                </div>
                <div style={{ fontSize: '0.65rem', color: 'var(--text-muted)' }}>{step.desc}</div>
              </div>
            )
          })}
        </div>
      </div>

      {/* Main Content Area */}
      <div
        style={{
          flex: 1,
          overflowY: 'auto',
          padding: '1rem',
          display: 'flex',
          flexDirection: 'column',
          gap: '1rem',
        }}
      >
        {error && <Alert type="error" message={error} onClose={() => setError(null)} />}

        {/* Empty State when no hints have been requested yet */}
        {hints.length === 0 && !loading && (
          <div
            style={{
              textAlign: 'center',
              padding: '2.5rem 1rem',
              color: 'var(--text-secondary)',
              background: 'rgba(255, 255, 255, 0.02)',
              borderRadius: 'var(--radius-md)',
              border: '1px dashed var(--border-color)',
            }}
          >
            <div style={{ fontSize: '2.5rem', marginBottom: '0.75rem' }}>🎯</div>
            <h4 style={{ color: '#f8fafc', marginBottom: '0.5rem', fontSize: '1.05rem' }}>
              Stuck on this problem?
            </h4>
            <p style={{ fontSize: '0.875rem', maxWidth: '420px', margin: '0 auto 1.5rem', lineHeight: 1.5 }}>
              The AI Mentor provides progressive hints starting from high-level algorithmic concepts to targeted logic
              checkpoints, encouraging you to reach the solution on your own.
            </p>

            <div style={{ display: 'flex', justifyContent: 'center', gap: '0.75rem', flexWrap: 'wrap' }}>
              <Button variant="primary" onClick={handleGetHint} icon="💡">
                Get Hint (Level 1)
              </Button>
              <Button variant="outline" onClick={handleExplainMistake} icon="🔍">
                Explain My Mistake
              </Button>
            </div>
          </div>
        )}

        {/* Loading Spinner */}
        {loading && (
          <div style={{ padding: '2rem', textAlign: 'center' }}>
            <LoadingSpinner size="md" message="AI Mentor is analyzing problem constraints and your code..." />
          </div>
        )}

        {/* Display Current & Unlocked Hints List */}
        {hints.map((hint, index) => (
          <div
            key={index}
            style={{
              background: '#0d131f',
              border: '1px solid var(--border-color)',
              borderRadius: 'var(--radius-md)',
              padding: '1rem',
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.2)',
            }}
          >
            {/* Hint Title & Level Badge */}
            <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                marginBottom: '0.75rem',
                paddingBottom: '0.5rem',
                borderBottom: '1px solid rgba(255, 255, 255, 0.06)',
              }}
            >
              <span style={{ fontWeight: 600, color: '#60a5fa', fontSize: '0.95rem' }}>
                {hint.title || `Level ${hint.hintLevel || index + 1} Guidance`}
              </span>
              <Badge
                text={
                  hint.hintLevel === 4
                    ? 'Solution'
                    : hint.hintLevel === 3
                    ? 'Level 3'
                    : hint.hintLevel === 2
                    ? 'Level 2'
                    : 'Level 1'
                }
                variant={hint.hintLevel === 4 ? 'success' : 'info'}
              />
            </div>

            {/* Hint Content */}
            <div style={{ marginBottom: '0.75rem' }}>{renderFormattedContent(hint.content)}</div>

            {/* Pedagogical "Why This Helps" callout */}
            {hint.whyThisHelps && (
              <div
                style={{
                  background: 'rgba(16, 185, 129, 0.08)',
                  border: '1px solid rgba(16, 185, 129, 0.25)',
                  borderRadius: 'var(--radius-sm)',
                  padding: '0.5rem 0.75rem',
                  fontSize: '0.8rem',
                  color: '#a7f3d0',
                  marginTop: '0.5rem',
                  display: 'flex',
                  alignItems: 'flex-start',
                  gap: '0.4rem',
                }}
              >
                <span>🧠</span>
                <div>
                  <strong>Why this helps:</strong> {hint.whyThisHelps}
                </div>
              </div>
            )}
          </div>
        ))}
      </div>

      {/* Action Footer Bar */}
      <div
        style={{
          padding: '0.875rem 1rem',
          background: 'var(--bg-tertiary)',
          borderTop: '1px solid var(--border-color)',
          display: 'flex',
          flexWrap: 'wrap',
          gap: '0.5rem',
          alignItems: 'center',
          justifyContent: 'space-between',
        }}
      >
        <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
          {/* Next Hint Button */}
          {currentLevel < 3 && (
            <Button
              variant="primary"
              size="sm"
              onClick={handleGetHint}
              disabled={loading}
              icon="➡️"
            >
              {currentLevel === 0 ? 'Get Hint' : `Next Hint (Level ${currentLevel + 1})`}
            </Button>
          )}

          {/* Explain My Mistake Button */}
          <Button
            variant="outline"
            size="sm"
            onClick={handleExplainMistake}
            disabled={loading}
            icon="🔍"
          >
            Explain My Mistake
          </Button>

          {/* Show Solution Explanation Button */}
          {!showSolutionConfirm && !hints.some((h) => h.hintLevel === 4) && (
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setShowSolutionConfirm(true)}
              disabled={loading}
              icon="📖"
              style={{ color: '#f59e0b' }}
            >
              Show Solution Explanation
            </Button>
          )}
        </div>

        {/* Confirmation prompt for revealing full solution */}
        {showSolutionConfirm && (
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              background: 'rgba(245, 158, 11, 0.1)',
              border: '1px solid rgba(245, 158, 11, 0.3)',
              borderRadius: 'var(--radius-sm)',
              padding: '0.35rem 0.6rem',
              fontSize: '0.8rem',
              color: '#fde68a',
            }}
          >
            <span>Reveal full solution?</span>
            <Button size="sm" variant="warning" onClick={handleShowSolution} disabled={loading}>
              Yes, Reveal
            </Button>
            <Button size="sm" variant="ghost" onClick={() => setShowSolutionConfirm(false)}>
              Cancel
            </Button>
          </div>
        )}
      </div>
    </div>
  )
}
