package com.oj.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing global platform statistics for administrators.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatsResponse {

    private long totalUsers;
    private long totalProblems;
    private long totalSubmissions;
    private long totalTestCases;

    // Problem Difficulty Breakdown
    private long easyProblems;
    private long mediumProblems;
    private long hardProblems;

    // Submission Status Breakdown
    private long acceptedSubmissions;
    private long wrongAnswerSubmissions;
    private long compilationErrorSubmissions;
    private long runtimeErrorSubmissions;
    private long timeLimitExceededSubmissions;
}
