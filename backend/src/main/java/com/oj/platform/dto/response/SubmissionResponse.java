package com.oj.platform.dto.response;

import com.oj.platform.entity.Submission;
import com.oj.platform.enums.Language;
import com.oj.platform.enums.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for Submission details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {

    private Long id;
    private Long problemId;
    private String problemTitle;
    private Long userId;
    private String username;
    private String sourceCode;
    private Language language;
    private SubmissionStatus status;
    private Long executionTime;
    private Long memoryUsed;
    private Integer passedTestCases;
    private Integer totalTestCases;
    private String errorMessage;
    private LocalDateTime submittedAt;

    public static SubmissionResponse fromEntity(Submission submission) {
        if (submission == null) return null;
        return SubmissionResponse.builder()
                .id(submission.getId())
                .problemId(submission.getProblem().getId())
                .problemTitle(submission.getProblem().getTitle())
                .userId(submission.getUser().getId())
                .username(submission.getUser().getUsername())
                .sourceCode(submission.getSourceCode())
                .language(submission.getLanguage())
                .status(submission.getStatus())
                .executionTime(submission.getExecutionTime())
                .memoryUsed(submission.getMemoryUsed())
                .passedTestCases(submission.getPassedTestCases())
                .totalTestCases(submission.getTotalTestCases())
                .errorMessage(submission.getErrorMessage())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }
}
