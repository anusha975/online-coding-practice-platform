package com.oj.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing detailed problem-solving statistics for a user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatsResponse {

    private Long userId;
    private String username;

    // Submissions
    private long totalSubmissions;
    private long acceptedSubmissions;
    private double acceptanceRate; // e.g., 75.50 (%)

    // Problems Solved by Difficulty
    private long totalSolved;
    private long easySolved;
    private long mediumSolved;
    private long hardSolved;

    // Total Platform Problems
    private long totalProblems;
    private long totalEasyProblems;
    private long totalMediumProblems;
    private long totalHardProblems;
}
