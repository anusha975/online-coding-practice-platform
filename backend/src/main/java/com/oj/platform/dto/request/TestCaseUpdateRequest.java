package com.oj.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a test case (ADMIN).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseUpdateRequest {

    @NotBlank(message = "Input data is required")
    private String input;

    @NotBlank(message = "Expected output data is required")
    private String expectedOutput;

    private boolean hidden;
}
