package com.oj.platform.judge.model;

import com.oj.platform.enums.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregated result of executing a code submission against all problem test cases.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutionResult {

    private SubmissionStatus status;
    private long executionTimeMs;
    private long memoryUsedKb;
    private int passedTestCases;
    private int totalTestCases;
    private String errorMessage;

    @Builder.Default
    private List<TestCaseResult> testCaseResults = new ArrayList<>();
}
