package com.oj.platform.service.impl;

import com.oj.platform.dto.response.UserStatsResponse;
import com.oj.platform.entity.User;
import com.oj.platform.enums.Difficulty;
import com.oj.platform.enums.SubmissionStatus;
import com.oj.platform.exception.ResourceNotFoundException;
import com.oj.platform.repository.ProblemRepository;
import com.oj.platform.repository.SubmissionRepository;
import com.oj.platform.repository.UserRepository;
import com.oj.platform.security.UserPrincipal;
import com.oj.platform.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of UserStatsService.
 *
 * Employs direct database-level aggregation to compute user metrics efficiently
 * without loading individual entity records into application memory.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserStatsServiceImpl implements UserStatsService {

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;

    @Override
    @Transactional(readOnly = true)
    public UserStatsResponse getMyStats(UserPrincipal userPrincipal) {
        return getUserStats(userPrincipal.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatsResponse getUserStats(Long userId) {
        log.info("Calculating user statistics for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // 1. Submissions Counts
        long totalSubmissions = submissionRepository.countByUserId(userId);
        long acceptedSubmissions = submissionRepository.countByUserIdAndStatus(userId, SubmissionStatus.ACCEPTED);

        // 2. Acceptance Rate (% rounded to 2 decimal places)
        double acceptanceRate = 0.0;
        if (totalSubmissions > 0) {
            double rate = ((double) acceptedSubmissions / totalSubmissions) * 100.0;
            acceptanceRate = Math.round(rate * 100.0) / 100.0;
        }

        // 3. Distinct Problems Solved Counts
        long totalSolved = submissionRepository.countDistinctProblemsByUserIdAndStatus(userId, SubmissionStatus.ACCEPTED);
        long easySolved = submissionRepository.countDistinctProblemsByUserIdAndStatusAndDifficulty(userId, SubmissionStatus.ACCEPTED, Difficulty.EASY);
        long mediumSolved = submissionRepository.countDistinctProblemsByUserIdAndStatusAndDifficulty(userId, SubmissionStatus.ACCEPTED, Difficulty.MEDIUM);
        long hardSolved = submissionRepository.countDistinctProblemsByUserIdAndStatusAndDifficulty(userId, SubmissionStatus.ACCEPTED, Difficulty.HARD);

        // 4. Platform Problem Totals
        long totalProblems = problemRepository.count();
        long totalEasy = problemRepository.countByDifficulty(Difficulty.EASY);
        long totalMedium = problemRepository.countByDifficulty(Difficulty.MEDIUM);
        long totalHard = problemRepository.countByDifficulty(Difficulty.HARD);

        return UserStatsResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .totalSubmissions(totalSubmissions)
                .acceptedSubmissions(acceptedSubmissions)
                .acceptanceRate(acceptanceRate)
                .totalSolved(totalSolved)
                .easySolved(easySolved)
                .mediumSolved(mediumSolved)
                .hardSolved(hardSolved)
                .totalProblems(totalProblems)
                .totalEasyProblems(totalEasy)
                .totalMediumProblems(totalMedium)
                .totalHardProblems(totalHard)
                .build();
    }
}
