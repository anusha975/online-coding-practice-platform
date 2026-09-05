package com.oj.platform.dto.response;

import com.oj.platform.entity.TestCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Public Response DTO for Sample Test Cases.
 *
 * Security Note:
 * Only returned for non-hidden test cases (hidden = false).
 * Hidden test cases must never be mapped or returned through this DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseSampleResponse {

    private Long id;
    private String input;
    private String expectedOutput;

    public static TestCaseSampleResponse fromEntity(TestCase testCase) {
        if (testCase == null) return null;
        return TestCaseSampleResponse.builder()
                .id(testCase.getId())
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .build();
    }
}
