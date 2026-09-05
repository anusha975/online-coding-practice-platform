import React, { useState, useEffect, useRef } from 'react'
import aiApi from '../../api/aiApi'
import ProgressiveHintSection from './ProgressiveHintSection'
import AiCodeReviewPanel from './AiCodeReviewPanel'
import Button from '../common/Button'
import Badge from '../common/Badge'

export const AiAssistantPanel = ({
  problem,
  userCode,
  language,
  lastVerdict,
  errorMessage,
  executionTime,
  memoryUsed,
  initialMode = 'hints',
  isOpen,
  onClose,
}) => {
  const [activeMode, setActiveMode] = useState(initialMode) // 'hints', 'review', or 'chat'
  const [messages, setMessages] = useState([
    {
      id: 'welcome',
      sender: 'ai',
      text: `Hello! 👋 I'm your **CodeForge AI Mentor**.\n\nI can help you understand the problem statement, provide **Progressive Hints (Levels 1-4)**, perform deep **Code Reviews**, or answer any debugging questions!\n\nHow can I help you with **${problem?.title || 'this challenge'}**?`,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      action: 'GENERAL_GUIDANCE',
    },
  ])
  const [inputQuery, setInputQuery] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const messagesEndRef = useRef(null)

  useEffect(() => {
    if (initialMode) {
      setActiveMode(initialMode)
    }
  }, [initialMode])

  useEffect(() => {
    if (activeMode === 'chat') {
      scrollToBottom()
    }
  }, [messages, loading, activeMode])

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  const handleSendMessage = async (queryText) => {
    const textToSend = queryText || inputQuery
    if (!textToSend || !textToSend.trim() || loading) return

    const userMessage = {
      id: Date.now().toString(),
      sender: 'user',
      text: textToSend.trim(),
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    }

    setMessages((prev) => [...prev, userMessage])
    setInputQuery('')
    setError(null)
    setLoading(true)

    try {
      const payload = {
        question: textToSend.trim(),
        problemId: problem?.id,
        problemTitle: problem?.title,
        problemDescription: problem?.description,
        problemDifficulty: problem?.difficulty,
        userCode: userCode,
        language: language,
        verdict: lastVerdict,
        errorMessage: errorMessage,
      }

      const res = await aiApi.chat(payload)
      const data = res.data?.data || res.data

      const aiMessage = {
        id: (Date.now() + 1).toString(),
        sender: 'ai',
        text: data.answer || 'I could not generate a response. Please try asking in a different way.',
        suggestedAction: data.suggestedAction || 'HINT',
        model: data.model,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      }

      setMessages((prev) => [...prev, aiMessage])
    } catch (err) {
      console.error('AI chat failed:', err)
      setError(err.response?.data?.message || 'Failed to communicate with the AI Mentor. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const handleQuickPrompt = (promptText) => {
    handleSendMessage(promptText)
  }

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleSendMessage()
    }
  }

  const clearChat = () => {
    setMessages([
      {
        id: Date.now().toString(),
        sender: 'ai',
        text: `Chat cleared. Feel free to ask any question about **${problem?.title || 'this challenge'}**!`,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      },
    ])
  }

  if (!isOpen) return null

  return (
    <div
      className="ai-assistant-panel"
      style={{
        display: 'flex',
        flexDirection: 'column',
        height: '100%',
        backgroundColor: 'var(--color-bg-surface, #1e293b)',
        borderLeft: '1px solid var(--color-border, #334155)',
        borderRadius: '8px',
        overflow: 'hidden',
      }}
    >
      {/* Panel Top Navigation Bar */}
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          padding: '8px 12px',
          backgroundColor: 'rgba(15, 23, 42, 0.9)',
          borderBottom: '1px solid var(--color-border, #334155)',
        }}
      >
        {/* Toggle Mode: Progressive Hints vs Code Review vs Chat */}
        <div style={{ display: 'flex', gap: '4px', background: 'rgba(0, 0, 0, 0.3)', padding: '3px', borderRadius: '6px' }}>
          <button
            onClick={() => setActiveMode('hints')}
            style={{
              padding: '4px 8px',
              fontSize: '0.78rem',
              fontWeight: 600,
              borderRadius: '4px',
              border: 'none',
              cursor: 'pointer',
              background: activeMode === 'hints' ? '#3b82f6' : 'transparent',
              color: activeMode === 'hints' ? '#ffffff' : '#94a3b8',
              transition: 'all 0.15s ease',
            }}
          >
            💡 Hints
          </button>
          <button
            onClick={() => setActiveMode('review')}
            style={{
              padding: '4px 8px',
              fontSize: '0.78rem',
              fontWeight: 600,
              borderRadius: '4px',
              border: 'none',
              cursor: 'pointer',
              background: activeMode === 'review' ? '#6366f1' : 'transparent',
              color: activeMode === 'review' ? '#ffffff' : '#94a3b8',
              transition: 'all 0.15s ease',
            }}
          >
            📝 Code Review
          </button>
          <button
            onClick={() => setActiveMode('chat')}
            style={{
              padding: '4px 8px',
              fontSize: '0.78rem',
              fontWeight: 600,
              borderRadius: '4px',
              border: 'none',
              cursor: 'pointer',
              background: activeMode === 'chat' ? '#3b82f6' : 'transparent',
              color: activeMode === 'chat' ? '#ffffff' : '#94a3b8',
              transition: 'all 0.15s ease',
            }}
          >
            💬 Chat
          </button>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
          {activeMode === 'chat' && (
            <button
              onClick={clearChat}
              title="Clear Chat History"
              style={{
                background: 'none',
                border: 'none',
                color: '#94a3b8',
                cursor: 'pointer',
                fontSize: '11px',
                padding: '3px 6px',
                borderRadius: '4px',
              }}
              onMouseOver={(e) => (e.currentTarget.style.color = '#f8fafc')}
              onMouseOut={(e) => (e.currentTarget.style.color = '#94a3b8')}
            >
              Clear
            </button>
          )}
          {onClose && (
            <button
              onClick={onClose}
              style={{
                background: 'none',
                border: 'none',
                color: '#94a3b8',
                cursor: 'pointer',
                fontSize: '16px',
                padding: '4px',
              }}
            >
              ✕
            </button>
          )}
        </div>
      </div>

      {/* Mode 1: Progressive Hint Section */}
      {activeMode === 'hints' && (
        <div style={{ flex: 1, minHeight: 0, height: '100%' }}>
          <ProgressiveHintSection
            problem={problem}
            userCode={userCode}
            language={language}
            lastVerdict={lastVerdict}
            errorMessage={errorMessage}
          />
        </div>
      )}

      {/* Mode 2: AI Code Review Section */}
      {activeMode === 'review' && (
        <div style={{ flex: 1, minHeight: 0, height: '100%' }}>
          <AiCodeReviewPanel
            problem={problem}
            userCode={userCode}
            language={language}
            lastVerdict={lastVerdict}
            errorMessage={errorMessage}
            executionTime={executionTime}
            memoryUsed={memoryUsed}
          />
        </div>
      )}

      {/* Mode 2: Free AI Mentor Chat */}
      {activeMode === 'chat' && (
        <div style={{ display: 'flex', flexDirection: 'column', flex: 1, minHeight: 0, height: '100%' }}>
          {/* Quick Prompts Bar */}
          <div
            style={{
              padding: '8px 12px',
              backgroundColor: 'rgba(30, 41, 59, 0.5)',
              borderBottom: '1px solid var(--color-border, #334155)',
              display: 'flex',
              gap: '6px',
              overflowX: 'auto',
              whiteSpace: 'nowrap',
            }}
          >
            <button
              onClick={() => handleQuickPrompt('Give me a conceptual hint without code')}
              disabled={loading}
              style={{
                fontSize: '0.75rem',
                padding: '4px 8px',
                backgroundColor: 'rgba(99, 102, 241, 0.15)',
                border: '1px solid rgba(99, 102, 241, 0.3)',
                borderRadius: '12px',
                color: '#c7d2fe',
                cursor: 'pointer',
              }}
            >
              💡 Hint
            </button>
            <button
              onClick={() => handleQuickPrompt('Why is my code failing or giving an error?')}
              disabled={loading}
              style={{
                fontSize: '0.75rem',
                padding: '4px 8px',
                backgroundColor: 'rgba(239, 68, 68, 0.15)',
                border: '1px solid rgba(239, 68, 68, 0.3)',
                borderRadius: '12px',
                color: '#fca5a5',
                cursor: 'pointer',
              }}
            >
              🐛 Debug Error
            </button>
            <button
              onClick={() => handleQuickPrompt('What is the optimal time and space complexity?')}
              disabled={loading}
              style={{
                fontSize: '0.75rem',
                padding: '4px 8px',
                backgroundColor: 'rgba(59, 130, 246, 0.15)',
                border: '1px solid rgba(59, 130, 246, 0.3)',
                borderRadius: '12px',
                color: '#93c5fd',
                cursor: 'pointer',
              }}
            >
              ⏱️ Complexity
            </button>
            <button
              onClick={() => handleQuickPrompt('What tricky edge cases should I test?')}
              disabled={loading}
              style={{
                fontSize: '0.75rem',
                padding: '4px 8px',
                backgroundColor: 'rgba(16, 185, 129, 0.15)',
                border: '1px solid rgba(16, 185, 129, 0.3)',
                borderRadius: '12px',
                color: '#6ee7b7',
                cursor: 'pointer',
              }}
            >
              🛡️ Edge Cases
            </button>
          </div>

          {/* Messages Scroll Area */}
          <div
            style={{
              flex: 1,
              overflowY: 'auto',
              padding: '16px',
              display: 'flex',
              flexDirection: 'column',
              gap: '12px',
            }}
          >
            {messages.map((msg) => (
              <div
                key={msg.id}
                style={{
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: msg.sender === 'user' ? 'flex-end' : 'flex-start',
                  maxWidth: '100%',
                }}
              >
                <div
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px',
                    marginBottom: '4px',
                    fontSize: '0.75rem',
                    color: '#94a3b8',
                  }}
                >
                  <span>{msg.sender === 'user' ? 'You' : 'AI Mentor'}</span>
                  <span>•</span>
                  <span>{msg.timestamp}</span>
                </div>

                <div
                  style={{
                    backgroundColor:
                      msg.sender === 'user'
                        ? 'var(--color-primary, #3b82f6)'
                        : 'rgba(15, 23, 42, 0.65)',
                    color: '#f8fafc',
                    padding: '10px 14px',
                    borderRadius:
                      msg.sender === 'user'
                        ? '16px 16px 4px 16px'
                        : '16px 16px 16px 4px',
                    fontSize: '0.875rem',
                    lineHeight: '1.5',
                    maxWidth: '90%',
                    wordBreak: 'break-word',
                    whiteSpace: 'pre-wrap',
                    border:
                      msg.sender === 'ai'
                        ? '1px solid var(--color-border, #334155)'
                        : 'none',
                  }}
                >
                  {msg.text}
                </div>
              </div>
            ))}

            {loading && (
              <div
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  color: '#94a3b8',
                  fontSize: '0.85rem',
                  padding: '8px 12px',
                }}
              >
                <div
                  className="ai-typing-indicator"
                  style={{ display: 'flex', gap: '4px' }}
                >
                  <span style={{ animation: 'pulse 1s infinite' }}>●</span>
                  <span style={{ animation: 'pulse 1s infinite 0.2s' }}>●</span>
                  <span style={{ animation: 'pulse 1s infinite 0.4s' }}>●</span>
                </div>
                <span>Mentor is thinking...</span>
              </div>
            )}

            {error && (
              <div
                style={{
                  backgroundColor: 'rgba(239, 68, 68, 0.1)',
                  border: '1px solid rgba(239, 68, 68, 0.3)',
                  color: '#fca5a5',
                  padding: '8px 12px',
                  borderRadius: '6px',
                  fontSize: '0.8rem',
                }}
              >
                {error}
              </div>
            )}

            <div ref={messagesEndRef} />
          </div>

          {/* Input Form */}
          <div
            style={{
              padding: '12px',
              backgroundColor: 'rgba(15, 23, 42, 0.75)',
              borderTop: '1px solid var(--color-border, #334155)',
            }}
          >
            <div style={{ display: 'flex', gap: '8px' }}>
              <textarea
                value={inputQuery}
                onChange={(e) => setInputQuery(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder="Ask about logic, complexity, debugging, or hints..."
                rows={2}
                disabled={loading}
                style={{
                  flex: 1,
                  backgroundColor: 'var(--color-bg-input, #0f172a)',
                  border: '1px solid var(--color-border, #334155)',
                  borderRadius: '6px',
                  color: '#f8fafc',
                  padding: '8px 10px',
                  fontSize: '0.85rem',
                  resize: 'none',
                  outline: 'none',
                  fontFamily: 'inherit',
                }}
              />
              <button
                onClick={() => handleSendMessage()}
                disabled={loading || !inputQuery.trim()}
                style={{
                  backgroundColor: 'var(--color-primary, #3b82f6)',
                  color: '#ffffff',
                  border: 'none',
                  borderRadius: '6px',
                  padding: '0 16px',
                  cursor: loading || !inputQuery.trim() ? 'not-allowed' : 'pointer',
                  opacity: loading || !inputQuery.trim() ? 0.6 : 1,
                  fontWeight: 600,
                  fontSize: '0.85rem',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              >
                Send
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default AiAssistantPanel
