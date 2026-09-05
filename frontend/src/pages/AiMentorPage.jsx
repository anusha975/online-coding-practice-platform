import React, { useState } from 'react'
import aiApi from '../api/aiApi'
import Badge from '../components/common/Badge'
import Button from '../components/common/Button'

export const AiMentorPage = () => {
  const [question, setQuestion] = useState('')
  const [selectedTopic, setSelectedTopic] = useState('ALL')
  const [selectedDifficulty, setSelectedDifficulty] = useState('ALL')
  const [selectedLanguage, setSelectedLanguage] = useState('ALL')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [mentorHistory, setMentorHistory] = useState([
    {
      id: 'welcome',
      question: 'Welcome to the AI Coding Mentor!',
      topic: 'ALL',
      answer: `### 👋 Welcome to the CodeForge RAG-Powered AI Coding Mentor!
      
I am grounded in our platform's **verified educational knowledge base** spanning:
- **Data Structures**: Arrays, HashMaps, Trees, Graphs, Heaps, Monotonic Stacks, Tries
- **Algorithms**: Binary Search, Two Pointers, Sliding Window, Dynamic Programming, BFS/DFS, Dijkstra
- **Java Core**: JVM Memory Model (Stack vs Heap), Garbage Collection, Collections, Concurrency
- **SQL & Databases**: B+Tree Indexing, Leftmost Prefix, Joins, ACID Transactions & Isolation Levels
- **Debugging & Complexity**: StackOverflow prevention, Big-O Analysis, 32-bit Integer Overflow

*Ask any conceptual question or click one of the quick prompts below to get started!*`,
      retrievedSources: [],
      groundedInContext: true,
      isSufficientKnowledgeAvailable: true,
      suggestedFollowUps: [
        'Explain binary search and how to avoid integer overflow',
        'When should I use a HashMap vs TreeMap?',
        'Why does deep recursion throw StackOverflowError?',
        'How does B+Tree indexing work in SQL databases?',
      ],
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    },
  ])

  const [expandedSourcesId, setExpandedSourcesId] = useState(null)
  const [copiedId, setCopiedId] = useState(null)

  const topics = [
    { id: 'ALL', label: '🌐 All Topics', color: '#94a3b8' },
    { id: 'DATA_STRUCTURES', label: '📦 Data Structures', color: '#3b82f6' },
    { id: 'ALGORITHMS', label: '⚡ Algorithms', color: '#8b5cf6' },
    { id: 'JAVA_CORE', label: '☕ Java Core', color: '#f59e0b' },
    { id: 'SQL_DATABASES', label: '🗄️ SQL & Databases', color: '#10b981' },
    { id: 'CODING_PATTERNS', label: '🧩 Patterns', color: '#ec4899' },
    { id: 'DEBUGGING_GUIDE', label: '🛡️ Debugging & Big-O', color: '#ef4444' },
  ]

  const quickPrompts = [
    {
      title: 'Binary Search Mechanics',
      prompt: 'Explain binary search and how to avoid integer overflow',
      topic: 'ALGORITHMS',
      badge: 'Algorithm',
    },
    {
      title: 'HashMap vs TreeMap',
      prompt: 'When should I use a HashMap vs TreeMap vs LinkedHashMap?',
      topic: 'DATA_STRUCTURES',
      badge: 'Data Structures',
    },
    {
      title: 'Recursion & Call Stack Limits',
      prompt: 'Why is my recursion slow or throwing StackOverflowError?',
      topic: 'DEBUGGING_GUIDE',
      badge: 'Debugging',
    },
    {
      title: 'Database B+Tree Indexing',
      prompt: 'How does database indexing work and what is the leftmost prefix rule?',
      topic: 'SQL_DATABASES',
      badge: 'SQL',
    },
    {
      title: 'Two Pointers vs Sliding Window',
      prompt: 'How does the Two Pointers technique differ from Sliding Window?',
      topic: 'CODING_PATTERNS',
      badge: 'Patterns',
    },
    {
      title: 'Java JVM Stack vs Heap',
      prompt: 'Explain Java Stack vs Heap memory and how Garbage Collection works',
      topic: 'JAVA_CORE',
      badge: 'Java',
    },
  ]

  const handleAsk = async (queryText) => {
    const textToSend = queryText || question
    if (!textToSend || !textToSend.trim() || loading) return

    setLoading(true)
    setError(null)

    try {
      const payload = {
        question: textToSend.trim(),
        topic: selectedTopic !== 'ALL' ? selectedTopic : null,
        difficulty: selectedDifficulty !== 'ALL' ? selectedDifficulty : null,
        language: selectedLanguage !== 'ALL' ? selectedLanguage : null,
        topK: 4,
      }

      const res = await aiApi.mentor(payload)
      const data = res.data?.data || res.data

      const newEntry = {
        id: Date.now().toString(),
        question: textToSend.trim(),
        topic: data.topic || selectedTopic,
        answer: data.answer || 'No response generated.',
        retrievedSources: data.retrievedSources || [],
        groundedInContext: data.groundedInContext,
        isSufficientKnowledgeAvailable: data.isSufficientKnowledgeAvailable,
        primaryConcept: data.primaryConcept,
        suggestedFollowUps: data.suggestedFollowUps || [],
        suggestedAction: data.suggestedAction,
        model: data.model,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      }

      setMentorHistory((prev) => [newEntry, ...prev])
      setQuestion('')
      if (newEntry.retrievedSources.length > 0) {
        setExpandedSourcesId(newEntry.id)
      }
    } catch (err) {
      console.error('AI Mentor query failed:', err)
      setError(err.response?.data?.message || 'Failed to retrieve knowledge. Please check connection and try again.')
    } finally {
      setLoading(false)
    }
  }

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleAsk()
    }
  }

  const copyAnswer = (id, text) => {
    navigator.clipboard.writeText(text)
    setCopiedId(id)
    setTimeout(() => setCopiedId(null), 2000)
  }

  return (
    <div
      className="ai-mentor-page"
      style={{
        maxWidth: '1200px',
        margin: '0 auto',
        padding: '2rem 1.5rem',
        color: '#f8fafc',
      }}
    >
      {/* Hero Header */}
      <div
        style={{
          background: 'linear-gradient(135deg, rgba(30, 41, 59, 0.8) 0%, rgba(15, 23, 42, 0.95) 100%)',
          border: '1px solid #334155',
          borderRadius: '16px',
          padding: '2rem',
          marginBottom: '2rem',
          boxShadow: '0 8px 32px rgba(0,0,0,0.3)',
          display: 'flex',
          flexDirection: 'column',
          gap: '12px',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <div
              style={{
                width: '48px',
                height: '48px',
                borderRadius: '12px',
                background: 'linear-gradient(135deg, #6366f1 0%, #3b82f6 100%)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '24px',
                boxShadow: '0 4px 16px rgba(99, 102, 241, 0.4)',
              }}
            >
              🧠
            </div>
            <div>
              <h1 style={{ fontSize: '1.75rem', fontWeight: 700, margin: 0 }}>
                RAG AI Coding Mentor
              </h1>
              <p style={{ margin: 0, color: '#94a3b8', fontSize: '0.9rem' }}>
                Retrieval-Augmented Generation grounded in verified DSA, System, Java & SQL Knowledge Bases
              </p>
            </div>
          </div>

          <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
            <Badge text="RAG Pipeline Active" type="success" />
            <Badge text="Cosine Similarity Index" type="info" />
          </div>
        </div>

        {/* Topic Filter Tabs */}
        <div
          style={{
            display: 'flex',
            gap: '8px',
            flexWrap: 'wrap',
            marginTop: '1rem',
            borderTop: '1px solid #334155',
            paddingTop: '1rem',
          }}
        >
          {topics.map((t) => (
            <button
              key={t.id}
              onClick={() => setSelectedTopic(t.id)}
              style={{
                padding: '6px 14px',
                borderRadius: '20px',
                fontSize: '0.82rem',
                fontWeight: 600,
                border: '1px solid',
                borderColor: selectedTopic === t.id ? t.color : 'rgba(51, 65, 85, 0.8)',
                backgroundColor: selectedTopic === t.id ? 'rgba(59, 130, 246, 0.15)' : 'rgba(15, 23, 42, 0.6)',
                color: selectedTopic === t.id ? '#ffffff' : '#94a3b8',
                cursor: 'pointer',
                transition: 'all 0.2s ease',
              }}
            >
              {t.label}
            </button>
          ))}
        </div>
      </div>

      {/* Main Search & Query Box */}
      <div
        style={{
          backgroundColor: '#1e293b',
          border: '1px solid #334155',
          borderRadius: '12px',
          padding: '1.25rem',
          marginBottom: '2rem',
        }}
      >
        <div style={{ display: 'flex', gap: '12px', marginBottom: '12px' }}>
          <textarea
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="Ask anything (e.g. 'Explain Binary Search', 'When should I use a HashMap?', 'Why is my recursion slow?')..."
            rows={3}
            disabled={loading}
            style={{
              flex: 1,
              backgroundColor: '#0f172a',
              border: '1px solid #334155',
              borderRadius: '8px',
              color: '#f8fafc',
              padding: '12px 14px',
              fontSize: '0.95rem',
              resize: 'none',
              outline: 'none',
              fontFamily: 'inherit',
            }}
          />
          <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
            <Button
              variant="primary"
              onClick={() => handleAsk()}
              disabled={loading || !question.trim()}
              style={{
                height: '100%',
                padding: '0 24px',
                fontSize: '1rem',
                fontWeight: 600,
              }}
            >
              {loading ? 'Searching...' : 'Ask Mentor 🚀'}
            </Button>
          </div>
        </div>

        {/* Query Filter Selectors */}
        <div style={{ display: 'flex', gap: '16px', alignItems: 'center', flexWrap: 'wrap', fontSize: '0.82rem', color: '#94a3b8' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <span>Target Difficulty:</span>
            <select
              value={selectedDifficulty}
              onChange={(e) => setSelectedDifficulty(e.target.value)}
              style={{
                backgroundColor: '#0f172a',
                border: '1px solid #334155',
                color: '#f8fafc',
                borderRadius: '6px',
                padding: '4px 8px',
                fontSize: '0.8rem',
              }}
            >
              <option value="ALL">All Levels</option>
              <option value="BEGINNER">Beginner</option>
              <option value="INTERMEDIATE">Intermediate</option>
              <option value="ADVANCED">Advanced</option>
            </select>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <span>Language:</span>
            <select
              value={selectedLanguage}
              onChange={(e) => setSelectedLanguage(e.target.value)}
              style={{
                backgroundColor: '#0f172a',
                border: '1px solid #334155',
                color: '#f8fafc',
                borderRadius: '6px',
                padding: '4px 8px',
                fontSize: '0.8rem',
              }}
            >
              <option value="ALL">All Languages</option>
              <option value="Java">Java</option>
              <option value="Python">Python</option>
              <option value="C++">C++</option>
              <option value="SQL">SQL</option>
            </select>
          </div>
        </div>

        {error && (
          <div
            style={{
              marginTop: '12px',
              backgroundColor: 'rgba(239, 68, 68, 0.1)',
              border: '1px solid rgba(239, 68, 68, 0.3)',
              color: '#fca5a5',
              padding: '8px 12px',
              borderRadius: '6px',
              fontSize: '0.85rem',
            }}
          >
            {error}
          </div>
        )}
      </div>

      {/* Quick Prompts Carousel */}
      <div style={{ marginBottom: '2rem' }}>
        <h3 style={{ fontSize: '1rem', fontWeight: 600, color: '#94a3b8', marginBottom: '10px' }}>
          💡 Popular Knowledge Base Questions
        </h3>
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
            gap: '12px',
          }}
        >
          {quickPrompts.map((qp, index) => (
            <div
              key={index}
              onClick={() => handleAsk(qp.prompt)}
              style={{
                backgroundColor: 'rgba(30, 41, 59, 0.6)',
                border: '1px solid #334155',
                borderRadius: '10px',
                padding: '12px 14px',
                cursor: 'pointer',
                transition: 'all 0.2s ease',
              }}
              onMouseOver={(e) => {
                e.currentTarget.style.borderColor = '#3b82f6'
                e.currentTarget.style.backgroundColor = 'rgba(59, 130, 246, 0.08)'
              }}
              onMouseOut={(e) => {
                e.currentTarget.style.borderColor = '#334155'
                e.currentTarget.style.backgroundColor = 'rgba(30, 41, 59, 0.6)'
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
                <span style={{ fontWeight: 600, fontSize: '0.88rem', color: '#f8fafc' }}>{qp.title}</span>
                <span
                  style={{
                    fontSize: '0.7rem',
                    padding: '2px 8px',
                    borderRadius: '10px',
                    backgroundColor: 'rgba(99, 102, 241, 0.2)',
                    color: '#a5b4fc',
                  }}
                >
                  {qp.badge}
                </span>
              </div>
              <p style={{ margin: 0, fontSize: '0.8rem', color: '#94a3b8', lineHeight: 1.4 }}>
                "{qp.prompt}"
              </p>
            </div>
          ))}
        </div>
      </div>

      {/* Q&A Results Stream */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
        {mentorHistory.map((item) => (
          <div
            key={item.id}
            style={{
              backgroundColor: '#1e293b',
              border: '1px solid #334155',
              borderRadius: '14px',
              padding: '1.5rem',
              boxShadow: '0 4px 16px rgba(0,0,0,0.2)',
            }}
          >
            {/* Question Bar */}
            <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                flexWrap: 'wrap',
                gap: '8px',
                borderBottom: '1px solid #334155',
                paddingBottom: '12px',
                marginBottom: '16px',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <span style={{ fontSize: '1.1rem' }}>❓</span>
                <h2 style={{ fontSize: '1.15rem', fontWeight: 600, margin: 0, color: '#f8fafc' }}>
                  {item.question}
                </h2>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                {item.groundedInContext && (
                  <span
                    style={{
                      fontSize: '0.75rem',
                      padding: '3px 8px',
                      borderRadius: '12px',
                      backgroundColor: 'rgba(16, 185, 129, 0.15)',
                      color: '#6ee7b7',
                      border: '1px solid rgba(16, 185, 129, 0.3)',
                    }}
                  >
                    ✓ Grounded in Verified Docs
                  </span>
                )}
                <span style={{ fontSize: '0.75rem', color: '#94a3b8' }}>{item.timestamp}</span>
                <button
                  onClick={() => copyAnswer(item.id, item.answer)}
                  style={{
                    backgroundColor: 'rgba(51, 65, 85, 0.6)',
                    border: '1px solid #475569',
                    borderRadius: '6px',
                    color: '#cbd5e1',
                    fontSize: '0.75rem',
                    padding: '4px 8px',
                    cursor: 'pointer',
                  }}
                >
                  {copiedId === item.id ? 'Copied! ✓' : '📋 Copy'}
                </button>
              </div>
            </div>

            {/* Answer Content */}
            <div
              style={{
                fontSize: '0.92rem',
                lineHeight: '1.65',
                color: '#e2e8f0',
                whiteSpace: 'pre-wrap',
                wordBreak: 'break-word',
              }}
            >
              {item.answer}
            </div>

            {/* Retrieved Citations Section (RAG Source Inspection) */}
            {item.retrievedSources && item.retrievedSources.length > 0 && (
              <div
                style={{
                  marginTop: '1.5rem',
                  borderTop: '1px dashed #334155',
                  paddingTop: '1rem',
                }}
              >
                <div
                  onClick={() => setExpandedSourcesId(expandedSourcesId === item.id ? null : item.id)}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    cursor: 'pointer',
                    userSelect: 'none',
                    padding: '6px 10px',
                    backgroundColor: 'rgba(15, 23, 42, 0.6)',
                    borderRadius: '8px',
                    border: '1px solid rgba(51, 65, 85, 0.6)',
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <span>📚</span>
                    <span style={{ fontSize: '0.85rem', fontWeight: 600, color: '#93c5fd' }}>
                      Retrieved Knowledge Citations ({item.retrievedSources.length} Chunks Matched)
                    </span>
                  </div>
                  <span style={{ fontSize: '0.8rem', color: '#94a3b8' }}>
                    {expandedSourcesId === item.id ? '▲ Hide' : '▼ Expand'}
                  </span>
                </div>

                {expandedSourcesId === item.id && (
                  <div
                    style={{
                      display: 'grid',
                      gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))',
                      gap: '12px',
                      marginTop: '12px',
                    }}
                  >
                    {item.retrievedSources.map((src, sIdx) => (
                      <div
                        key={sIdx}
                        style={{
                          backgroundColor: 'rgba(15, 23, 42, 0.85)',
                          border: '1px solid #334155',
                          borderRadius: '8px',
                          padding: '12px',
                          fontSize: '0.8rem',
                        }}
                      >
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
                          <span style={{ fontWeight: 600, color: '#f8fafc' }}>{src.title}</span>
                          <span
                            style={{
                              fontSize: '0.72rem',
                              padding: '2px 6px',
                              borderRadius: '4px',
                              backgroundColor: 'rgba(59, 130, 246, 0.2)',
                              color: '#93c5fd',
                              fontWeight: 600,
                            }}
                          >
                            {Math.round(src.similarityScore * 100)}% Match
                          </span>
                        </div>
                        <div style={{ display: 'flex', gap: '6px', marginBottom: '8px', flexWrap: 'wrap' }}>
                          <span style={{ fontSize: '0.7rem', color: '#94a3b8' }}>Concept: <b>{src.concept}</b></span>
                          <span style={{ fontSize: '0.7rem', color: '#94a3b8' }}>•</span>
                          <span style={{ fontSize: '0.7rem', color: '#94a3b8' }}>Source: <i>{src.source}</i></span>
                        </div>
                        <p
                          style={{
                            margin: 0,
                            color: '#cbd5e1',
                            lineHeight: 1.4,
                            backgroundColor: 'rgba(0,0,0,0.3)',
                            padding: '8px',
                            borderRadius: '6px',
                            fontFamily: 'monospace',
                            fontSize: '0.75rem',
                          }}
                        >
                          {src.snippet}
                        </p>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* Suggested Follow-ups */}
            {item.suggestedFollowUps && item.suggestedFollowUps.length > 0 && (
              <div
                style={{
                  marginTop: '1.25rem',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  flexWrap: 'wrap',
                }}
              >
                <span style={{ fontSize: '0.78rem', color: '#94a3b8', fontWeight: 600 }}>Suggested Follow-ups:</span>
                {item.suggestedFollowUps.map((fu, fuIdx) => (
                  <button
                    key={fuIdx}
                    onClick={() => handleAsk(fu)}
                    style={{
                      backgroundColor: 'rgba(99, 102, 241, 0.1)',
                      border: '1px solid rgba(99, 102, 241, 0.3)',
                      borderRadius: '16px',
                      color: '#c7d2fe',
                      padding: '4px 10px',
                      fontSize: '0.75rem',
                      cursor: 'pointer',
                      transition: 'all 0.15s ease',
                    }}
                    onMouseOver={(e) => {
                      e.currentTarget.style.backgroundColor = 'rgba(99, 102, 241, 0.25)'
                      e.currentTarget.style.borderColor = '#818cf8'
                    }}
                    onMouseOut={(e) => {
                      e.currentTarget.style.backgroundColor = 'rgba(99, 102, 241, 0.1)'
                      e.currentTarget.style.borderColor = 'rgba(99, 102, 241, 0.3)'
                    }}
                  >
                    💬 {fu}
                  </button>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}

export default AiMentorPage
