package com.oj.platform.dto.request;

import com.oj.platform.enums.Difficulty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing problem (PUT /api/problems/{id} - ADMIN).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemUpdateRequest {

    @NotBlank(message = "Problem title is required")
    @Size(min = 3, max = 200, message = "Problem title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "Problem description is required")
    private String description;

    @NotNull(message = "Difficulty is required (EASY, MEDIUM, HARD)")
    private Difficulty difficulty;

    @NotBlank(message = "Category is required (e.g., Arrays, Strings, Trees, Dynamic Programming)")
    private String category;

    private String constraints;

    private String inputFormat;

    private String outputFormat;

    private String sampleInput;

    private String sampleOutput;

    @Min(value = 100, message = "Time limit must be at least 100 ms")
    @Max(value = 10000, message = "Time limit cannot exceed 10000 ms")
    private Integer timeLimitMs;

    @Min(value = 16, message = "Memory limit must be at least 16 MB")
    @Max(value = 1024, message = "Memory limit cannot exceed 1024 MB")
    private Integer memoryLimitMb;
}
