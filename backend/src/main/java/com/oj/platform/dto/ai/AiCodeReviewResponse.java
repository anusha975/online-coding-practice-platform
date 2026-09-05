package com.oj.platform.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Structured response DTO containing comprehensive AI code review results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCodeReviewResponse {

    /**
     * High-level executive summary of code quality and correctness.
     */
    private String summary;

    /**
     * Detailed analysis focused on the execution verdict (e.g. why WRONG_ANSWER or COMPILATION_ERROR happened).
     */
    private String verdictAnalysis;

    /**
     * Identified bugs and logic issues, categorized by severity (CONFIRMED_ISSUE, POSSIBLE_ISSUE, SUGGESTION).
     */
    @Builder.Default
    private List<CodeReviewBugItem> bugs = new ArrayList<>();

    /**
     * Edge cases evaluation.
     */
    @Builder.Default
    private List<CodeReviewEdgeCase> edgeCases = new ArrayList<>();

    /**
     * Time complexity notation (e.g. "O(N)" or "O(N^2)").
     */
    private String timeComplexity;

    /**
     * Explanation of how time complexity was derived.
     */
    private String timeComplexityExplanation;

    /**
     * Space complexity notation (e.g. "O(1)" or "O(N)").
     */
    private String spaceComplexity;

    /**
     * Explanation of space complexity.
     */
    private String spaceComplexityExplanation;

    /**
     * Code readability score or rating (e.g. "8/10").
     */
    private String readabilityScore;

    /**
     * Readability & maintainability notes.
     */
    private String readabilityNotes;

    /**
     * Actionable suggestions for optimization or clean code improvement.
     */
    @Builder.Default
    private List<String> suggestions = new ArrayList<>();

    /**
     * Underlying model or review engine.
     */
    private String model;

    /**
     * Review generation timestamp.
     */
    private LocalDateTime timestamp;
}
