package com.oj.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Health check response DTO.
 *
 * Why DTOs are used:
 * - Decouples internal representation from API response contract.
 * - Guarantees exact JSON field names and types returned to the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthResponse {

    private String status;
    private String message;
}
