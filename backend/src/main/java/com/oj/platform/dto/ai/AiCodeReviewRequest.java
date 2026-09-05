package com.oj.platform.dto.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for AI-powered code review and debugging feedback.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCodeReviewRequest {

    /**
     * Optional Problem ID.
     */
    private Long problemId;

    /**
     * Problem title.
     */
    private String problemTitle;

    /**
     * Problem category (e.g. Arrays, Trees, Dynamic Programming).
     */
    private String problemCategory;

    /**
     * Problem difficulty (EASY, MEDIUM, HARD).
     */
    private String problemDifficulty;

    /**
     * Platform problem description (Source of truth).
     */
    private String problemDescription;

    /**
     * User's submitted or active source code to review.
     */
    @NotBlank(message = "Source code is required for code review.")
    private String sourceCode;

    /**
     * Programming language (JAVA, PYTHON, CPP, C, JAVASCRIPT).
     */
    private String programmingLanguage;

    /**
     * Optional execution verdict (e.g. ACCEPTED, WRONG_ANSWER, COMPILATION_ERROR, RUNTIME_ERROR, TIME_LIMIT_EXCEEDED).
     */
    private String verdict;

    /**
     * Optional compiler/runtime error message or test failure output.
     */
    private String errorMessage;

    /**
     * Optional execution time in ms.
     */
    private Integer executionTime;

    /**
     * Optional memory used in KB.
     */
    private Long memoryUsed;
}
