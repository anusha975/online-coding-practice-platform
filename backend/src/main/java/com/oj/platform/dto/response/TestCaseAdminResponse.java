package com.oj.platform.dto.response;

import com.oj.platform.entity.TestCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO returned to ADMINs showing full test case details including hidden status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseAdminResponse {

    private Long id;
    private Long problemId;
    private String input;
    private String expectedOutput;
    private boolean hidden;
    private LocalDateTime createdAt;

    public static TestCaseAdminResponse fromEntity(TestCase testCase) {
        if (testCase == null) return null;
        return TestCaseAdminResponse.builder()
                .id(testCase.getId())
                .problemId(testCase.getProblem().getId())
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .hidden(testCase.isHidden())
                .createdAt(testCase.getCreatedAt())
                .build();
    }
}
