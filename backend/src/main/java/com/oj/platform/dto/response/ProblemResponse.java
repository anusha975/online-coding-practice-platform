package com.oj.platform.dto.response;

import com.oj.platform.entity.Problem;
import com.oj.platform.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for Problem details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemResponse {

    private Long id;
    private String title;
    private String description;
    private Difficulty difficulty;
    private String category;
    private String constraints;
    private String inputFormat;
    private String outputFormat;
    private String sampleInput;
    private String sampleOutput;
    private Integer timeLimitMs;
    private Integer memoryLimitMb;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProblemResponse fromEntity(Problem problem) {
        if (problem == null) {
            return null;
        }
        return ProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .description(problem.getDescription())
                .difficulty(problem.getDifficulty())
                .category(problem.getCategory())
                .constraints(problem.getConstraints())
                .inputFormat(problem.getInputFormat())
                .outputFormat(problem.getOutputFormat())
                .sampleInput(problem.getSampleInput())
                .sampleOutput(problem.getSampleOutput())
                .timeLimitMs(problem.getTimeLimitMs())
                .memoryLimitMb(problem.getMemoryLimitMb())
                .createdAt(problem.getCreatedAt())
                .updatedAt(problem.getUpdatedAt())
                .build();
    }
}
