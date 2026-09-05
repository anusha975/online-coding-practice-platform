package com.oj.platform.dto.dataset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCaseSeedDto {
    private String input;
    private String expectedOutput;
    @Builder.Default
    private boolean hidden = false;
}
