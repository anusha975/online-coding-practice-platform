package com.oj.platform.dto.ai;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Request DTO for progressive algorithmic hints and mistake diagnosis.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiHintRequest {

    /**
     * Problem identifier.
     */
    private Long problemId;

    /**
     * Problem title.
     */
    private String problemTitle;

    /**
     * Category (e.g. Arrays, Dynamic Programming, Graphs).
     */
    private String problemCategory;

    /**
     * Problem difficulty level (EASY, MEDIUM, HARD).
     */
    private String problemDifficulty;

    /**
     * Full problem statement description. Treated as the immutable source of truth.
     */
    @NotBlank(message = "Problem description is required to ground the AI mentor.")
    private String problemDescription;

    /**
     * User's current source code in the editor.
     */
    private String userCode;

    /**
     * Programming language (JAVA, PYTHON, CPP, C, JAVASCRIPT).
     */
    private String programmingLanguage;

    /**
     * History of previous hint contents already given in this session.
     */
    @Builder.Default
    private List<String> previousHints = new ArrayList<>();

    /**
     * Requested hint level:
     * 1 = Conceptual Direction (No code, algorithmic pattern)
     * 2 = Problematic Logic / Targeted Flaw
     * 3 = Step-by-Step Logic Guidance / Pseudocode
     * 4 = Full Solution Walkthrough (Only on explicit request)
     */
    @Min(value = 1, message = "Hint level must be at least 1.")
    @Max(value = 4, message = "Hint level cannot exceed 4.")
    @Builder.Default
    private Integer requestedHintLevel = 1;

    /**
     * Operation mode:
     * "HINT" - standard progressive hint
     * "MISTAKE" - analyze specific error / wrong answer
     * "SOLUTION" - detailed solution explanation
     */
    @Builder.Default
    private String mode = "HINT";

    /**
     * Optional execution verdict (e.g. WRONG_ANSWER, TIME_LIMIT_EXCEEDED, COMPILATION_ERROR).
     */
    private String verdict;

    /**
     * Optional compiler/runtime error message or failed test case details.
     */
    private String errorMessage;
}
