package com.oj.platform.controller;

import com.oj.platform.dto.response.HealthResponse;
import com.oj.platform.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Check REST Controller.
 *
 * Exposes GET /api/health to verify that the backend application and its web context are active.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    /**
     * Health check endpoint.
     *
     * @return 200 OK with HealthResponse JSON payload.
     */
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> getHealth() {
        HealthResponse response = healthService.getHealthStatus();
        return ResponseEntity.ok(response);
    }
}
