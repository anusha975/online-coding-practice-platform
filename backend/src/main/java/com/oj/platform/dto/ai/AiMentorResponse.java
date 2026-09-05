package com.oj.platform.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Grounded response DTO returned by the RAG AI Coding Mentor.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiMentorResponse {

    /**
     * Comprehensive, educational markdown answer grounded in verified knowledge base chunks.
     */
    private String answer;

    /**
     * List of retrieved source document chunks used as factual grounding context.
     */
    private List<RetrievedSourceItem> retrievedSources;

    /**
     * Flag indicating whether the answer was grounded in verified platform knowledge chunks.
     */
    private boolean groundedInContext;

    /**
     * Flag indicating whether sufficient knowledge was available in the platform documentation.
     */
    private boolean isSufficientKnowledgeAvailable;

    /**
     * Detected or resolved topic classification.
     */
    private String topic;

    /**
     * Primary algorithmic or conceptual subject detected.
     */
    private String primaryConcept;

    /**
     * List of 2 to 4 recommended follow-up questions to deepen conceptual understanding.
     */
    private List<String> suggestedFollowUps;

    /**
     * Actionable recommendation (e.g., "Practice Problem: Two Sum", "Review HashMap Collisions").
     */
    private String suggestedAction;

    /**
     * Model or engine used (e.g. "rag-pipeline-semantic-v1", "gpt-4o-mini").
     */
    private String model;
}
