package com.oj.platform.service;

import com.oj.platform.dto.ai.AiChatRequest;
import com.oj.platform.dto.ai.AiChatResponse;
import com.oj.platform.dto.ai.AiHintRequest;
import com.oj.platform.dto.ai.AiHintResponse;

/**
 * Provider-agnostic abstraction for AI-powered coding mentorship and progressive hints.
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
}
