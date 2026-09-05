package com.oj.platform.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Individual bug or logic issue detected during AI code review.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeReviewBugItem {

    /**
     * Severity categorization:
     * - "CONFIRMED_ISSUE": Proven compilation, runtime, or logical failure.
     * - "POSSIBLE_ISSUE": Potential edge-case or unproven vulnerability.
     * - "SUGGESTION": Stylistic or design recommendation.
     */
    private String severity;

    /**
     * Short descriptive title of the issue.
     */
    private String title;

    /**
     * Detailed educational description explaining WHY the issue occurs and how to think about fixing it.
     */
    private String description;

    /**
     * Optional line or code symbol reference (e.g. "Line 12" or "Inner for-loop").
     */
    private String lineReference;
}
