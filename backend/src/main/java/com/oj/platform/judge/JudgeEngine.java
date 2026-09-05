package com.oj.platform.judge;

import com.oj.platform.enums.Language;
import com.oj.platform.entity.Problem;
import com.oj.platform.enums.SubmissionStatus;
import com.oj.platform.entity.TestCase;
import com.oj.platform.judge.executor.CodeExecutor;
import com.oj.platform.judge.model.ExecutionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Core dispatching engine for the code judge.
 *
 * Directs submissions to the appropriate language executor strategy.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JudgeEngine {

    private final List<CodeExecutor> executors;

    /**
     * Evaluates source code for a problem against its test cases.
     *
     * @param language   Programming language (JAVA, PYTHON, etc.)
     * @param sourceCode User-submitted source code
     * @param problem    Target problem entity
     * @param testCases  Test cases to execute against
     * @return ExecutionResult containing verdict, time, memory, and error details
     */
    public ExecutionResult evaluate(Language language, String sourceCode, Problem problem, List<TestCase> testCases) {
        log.info("Starting code evaluation: language={}, problemId={}, totalTestCases={}",
                language, problem.getId(), testCases.size());

        if (testCases == null || testCases.isEmpty()) {
            log.warn("Problem ID: {} has no test cases configured. Returning ACCEPTED by default.", problem.getId());
            return ExecutionResult.builder()
                    .status(SubmissionStatus.ACCEPTED)
                    .executionTimeMs(0)
                    .memoryUsedKb(0)
                    .passedTestCases(0)
                    .totalTestCases(0)
                    .build();
        }

        CodeExecutor executor = executors.stream()
                .filter(e -> e.supports(language))
                .findFirst()
                .orElse(null);

        if (executor == null) {
            log.error("No executor found for language: {}", language);
            return ExecutionResult.builder()
                    .status(SubmissionStatus.COMPILATION_ERROR)
                    .executionTimeMs(0)
                    .memoryUsedKb(0)
                    .passedTestCases(0)
                    .totalTestCases(testCases.size())
                    .errorMessage("Unsupported language execution: " + language)
                    .build();
        }

        return executor.execute(sourceCode, problem, testCases);
    }
}
