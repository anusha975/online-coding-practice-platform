package com.oj.platform.judge.model;

import com.oj.platform.enums.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Result of executing a submission against a single test case.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCaseResult {

    private Long testCaseId;
    private SubmissionStatus status;
    private long executionTimeMs;
    private long memoryUsedKb;
    private String actualOutput;
    private String expectedOutput;
    private String errorMessage;
}
