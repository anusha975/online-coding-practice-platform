package com.oj.platform.controller;

import com.oj.platform.dto.response.ApiResponse;
import com.oj.platform.dto.response.UserStatsResponse;
import com.oj.platform.security.UserPrincipal;
import com.oj.platform.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for User Statistics and Problem Solving Analytics.
 *
 * Base Path: /api/users
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserStatsController {

    private final UserStatsService userStatsService;

    /**
     * Retrieves problem solving statistics for the logged-in user.
     */
    @GetMapping("/me/stats")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getMyStats(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            throw new org.springframework.security.authentication.BadCredentialsException("Authentication required.");
        }
        UserStatsResponse response = userStatsService.getMyStats(userPrincipal);
        return ResponseEntity.ok(ApiResponse.success("Current user statistics retrieved successfully", response));
    }

    /**
     * Retrieves public profile statistics for any user by numeric user ID.
     */
    @GetMapping("/{id:\\d+}/stats")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getUserStats(
            @PathVariable Long id) {
        UserStatsResponse response = userStatsService.getUserStats(id);
        return ResponseEntity.ok(ApiResponse.success("User statistics retrieved successfully", response));
    }
}
