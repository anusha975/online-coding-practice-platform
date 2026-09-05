package com.oj.platform.dto.dataset;

import com.oj.platform.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemSeedDto {
    private String title;
    private String description;
    private Difficulty difficulty;
    private String category;
    private String constraints;
    private String inputFormat;
    private String outputFormat;
    private String sampleInput;
    private String sampleOutput;
    @Builder.Default
    private Integer timeLimitMs = 2000;
    @Builder.Default
    private Integer memoryLimitMb = 256;
    @Builder.Default
    private List<TestCaseSeedDto> testCases = new ArrayList<>();
}
