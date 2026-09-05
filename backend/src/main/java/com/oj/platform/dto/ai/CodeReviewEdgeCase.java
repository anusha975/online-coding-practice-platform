package com.oj.platform.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Edge case analysis item for Code Review.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeReviewEdgeCase {

    /**
     * Description of the specific edge scenario (e.g. "Empty array", "Target not found", "Negative integers").
     */
    private String caseDescription;

    /**
     * Expected behavior or potential failure impact if not handled.
     */
    private String impact;

    /**
     * Whether the user's current code safely handles this scenario.
     */
    private Boolean isHandled;
}
