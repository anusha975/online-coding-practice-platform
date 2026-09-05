package com.oj.platform.dto.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for RAG-based AI Coding Mentor questions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiMentorRequest {

    @NotBlank(message = "Question cannot be blank")
    private String question;

    /**
     * Optional filter: DATA_STRUCTURES, ALGORITHMS, JAVA_CORE, SQL_DATABASES, CODING_PATTERNS, DEBUGGING_GUIDE, SYSTEM_DESIGN, or ALL.
     */
    private String topic;

    /**
     * Optional filter: BEGINNER, INTERMEDIATE, ADVANCED, or ALL.
     */
    private String difficulty;

    /**
     * Optional filter: Java, Python, C++, SQL, General, or ALL.
     */
    private String language;

    /**
     * Number of top relevant chunks to retrieve (default: 4, min: 1, max: 10).
     */
    @Builder.Default
    private Integer topK = 4;

    /**
     * Optional problem context if asked within a problem solving session.
     */
    private Long problemId;
    private String problemTitle;
    private String problemDescription;

    /**
     * Optional user's current code.
     */
    private String userCode;

    /**
     * Optional previous conversation turns.
     */
    private List<String> conversationHistory;
}
