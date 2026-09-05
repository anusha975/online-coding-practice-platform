package com.oj.platform.service;

import com.oj.platform.dto.response.HealthResponse;

/**
 * Service interface for platform health status operations.
 *
 * Why Interfaces in Service Layer:
 * - Loose coupling between Controller and Service implementation.
 * - Adheres to Dependency Inversion Principle (DIP).
 * - Simplifies mocking and unit testing.
 */
public interface HealthService {

    /**
     * Checks backend service and database health.
     *
     * @return HealthResponse containing status and descriptive message.
     */
    HealthResponse getHealthStatus();
}
