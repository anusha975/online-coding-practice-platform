package com.oj.platform.service.impl;

import com.oj.platform.dto.response.HealthResponse;
import com.oj.platform.service.HealthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of HealthService.
 */
@Slf4j
@Service
public class HealthServiceImpl implements HealthService {

    @Override
    public HealthResponse getHealthStatus() {
        log.info("Health check endpoint accessed.");
        return HealthResponse.builder()
                .status("UP")
                .message("Coding platform backend is running")
                .build();
    }
}
