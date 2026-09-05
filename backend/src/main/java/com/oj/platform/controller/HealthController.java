package com.oj.platform.controller;

import com.oj.platform.dto.response.HealthResponse;
import com.oj.platform.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health Check & Root Welcome Controller.
 *
 * Exposes GET / and GET /api/health to verify that the backend application is live.
 */
@RestController
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    /**
     * Root endpoint returning platform service details and API guide.
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getRootStatus() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "CodePulse Online Coding Practice Platform API",
                "version", "1.0.0",
                "endpoints", Map.of(
                        "problems", "/api/problems",
                        "auth", "/api/auth/login",
                        "aiMentor", "/api/ai/mentor",
                        "health", "/api/health"
                )
        ));
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/api/health")
    public ResponseEntity<HealthResponse> getHealth() {
        HealthResponse response = healthService.getHealthStatus();
        return ResponseEntity.ok(response);
    }
}

