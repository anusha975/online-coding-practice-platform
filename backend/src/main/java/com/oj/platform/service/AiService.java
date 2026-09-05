package com.oj.platform.service;

import com.oj.platform.dto.ai.AiChatRequest;
import com.oj.platform.dto.ai.AiChatResponse;
import com.oj.platform.dto.ai.AiCodeReviewRequest;
import com.oj.platform.dto.ai.AiCodeReviewResponse;
import com.oj.platform.dto.ai.AiHintRequest;
import com.oj.platform.dto.ai.AiHintResponse;
import com.oj.platform.dto.ai.AiMentorRequest;
import com.oj.platform.dto.ai.AiMentorResponse;

/**
 * Provider-agnostic abstraction for AI-powered coding mentorship, progressive hints, code review, and RAG knowledge retrieval.
 */
public interface AiService {

    /**
     * Process user question in the context of the active problem and user code.
     *
     * @param request chat prompt, problem metadata, user code, error diagnostics
     * @param userId authenticated user ID
     * @return AI mentor explanation and suggested action
     */
    AiChatResponse chat(AiChatRequest request, Long userId);

    /**
     * Generate progressive hints (Levels 1-4) or mistake diagnostics for a problem.
     *
     * @param request hint level, problem metadata, user code, error diagnostics, previous hints
     * @param userId authenticated user ID
     * @return AI hint response with level details, content, and pedagogical rationale
     */
    AiHintResponse generateHint(AiHintRequest request, Long userId);

    /**
     * Perform comprehensive code review analyzing correctness, bugs with severity, edge cases, complexity, and suggestions.
     *
     * @param request source code, problem metadata, execution verdict, errors
     * @param userId authenticated user ID
     * @return structured code review analysis
     */
    AiCodeReviewResponse reviewCode(AiCodeReviewRequest request, Long userId);

    /**
     * Answer questions using Retrieval-Augmented Generation (RAG) grounded in platform educational knowledge materials.
     *
     * @param request user question, optional topic/difficulty/language filters, problem context
     * @param userId authenticated user ID
     * @return grounded mentor answer with source document citations and follow-up prompts
     */
    AiMentorResponse mentor(AiMentorRequest request, Long userId);
}
