package com.oj.platform.service;

import com.oj.platform.dto.response.UserStatsResponse;
import com.oj.platform.security.UserPrincipal;

/**
 * Service contract for calculating user problem solving statistics.
 */
public interface UserStatsService {

    /**
     * Calculates statistics for the currently authenticated user.
     */
    UserStatsResponse getMyStats(UserPrincipal userPrincipal);

    /**
     * Calculates public profile statistics for any user by user ID.
     */
    UserStatsResponse getUserStats(Long userId);
}
