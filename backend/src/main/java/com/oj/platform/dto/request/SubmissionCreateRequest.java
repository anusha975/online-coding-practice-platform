package com.oj.platform.dto.request;

import com.oj.platform.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for submitting code to a problem (POST /api/submissions).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionCreateRequest {

    @NotNull(message = "Problem ID is required")
    private Long problemId;

    @NotBlank(message = "Source code cannot be empty")
    @Size(max = 65536, message = "Source code cannot exceed 64KB")
    private String sourceCode;

    @NotNull(message = "Language is required (JAVA, PYTHON)")
    private Language language;
}
