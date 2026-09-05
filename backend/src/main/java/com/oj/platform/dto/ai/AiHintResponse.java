package com.oj.platform.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO containing progressive hint content and pedagogical guidance.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiHintResponse {

    /**
     * The hint level delivered (1, 2, 3, or 4).
     */
    private Integer hintLevel;

    /**
     * Clear human-friendly title (e.g. "Level 1: Conceptual Direction").
     */
    private String title;

    /**
     * Markdown-formatted hint content.
     */
    private String content;

    /**
     * Pedagogical rationale explaining WHY this hint is useful.
     */
    private String whyThisHelps;

    /**
     * Suggested next action (e.g. "NEXT_HINT", "TRY_CODING", "SHOW_SOLUTION", "COMPLETED").
     */
    private String nextAction;

    /**
     * Total number of hints unlocked in the current session.
     */
    private Integer hintsUsedCount;

    /**
     * Maximum available hint levels (usually 3 for progressive hints, 4 for solution).
     */
    private Integer maxLevels;

    /**
     * Underlying model or engine providing the response.
     */
    private String model;

    /**
     * Generation timestamp.
     */
    private LocalDateTime timestamp;
}
