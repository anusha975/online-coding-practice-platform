package com.oj.platform.judge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Result of executing an individual OS process.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessRunResult {

    private int exitCode;
    private String stdout;
    private String stderr;
    private long executionTimeMs;
    private long memoryUsedKb;
    private boolean timedOut;
}
