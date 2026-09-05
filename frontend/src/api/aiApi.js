import axiosClient from './axiosClient'

/**
 * AI Coding Mentor & Progressive Hint System API Client
 */
export const aiApi = {
  /**
   * Send a question to the AI coding assistant with context
   * @param {Object} data { question, problemId, problemTitle, problemDescription, problemDifficulty, userCode, language, errorMessage, verdict }
   * @returns {Promise} response with AI answer and suggestedAction
   */
  chat: (data) => axiosClient.post('/ai/chat', data),

  /**
   * Request progressive hints (Levels 1-4) or mistake diagnosis
   * @param {Object} data { problemId, problemTitle, problemCategory, problemDifficulty, problemDescription, userCode, programmingLanguage, previousHints, requestedHintLevel, mode, verdict, errorMessage }
   * @returns {Promise} response with hintLevel, title, content, whyThisHelps, nextAction, hintsUsedCount
   */
  getHint: (data) => axiosClient.post('/ai/hint', data),

  /**
   * Request error and logic mistake diagnosis
   * @param {Object} data problem metadata, code, error diagnostics
   * @returns {Promise}
   */
  explainMistake: (data) =>
    axiosClient.post('/ai/hint', {
      ...data,
      mode: 'MISTAKE',
      requestedHintLevel: 2,
    }),

  /**
   * Request comprehensive educational code review with severity classification
   * @param {Object} data { problemId, problemTitle, problemCategory, problemDifficulty, problemDescription, sourceCode, programmingLanguage, verdict, errorMessage, executionTime, memoryUsed }
   * @returns {Promise}
   */
  reviewCode: (data) => axiosClient.post('/ai/code-review', data),

  /**
   * Ask RAG-grounded AI Coding Mentor questions with platform knowledge citations
   * @param {Object} data { question, topic, difficulty, language, topK, problemId, problemTitle, problemDescription, userCode, conversationHistory }
   * @returns {Promise} response with answer, retrievedSources, groundedInContext, suggestedFollowUps, suggestedAction
   */
  mentor: (data) => axiosClient.post('/ai/mentor', data),
}

export default aiApi

