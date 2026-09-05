package com.oj.platform.judge.executor;

import com.oj.platform.enums.Language;
import com.oj.platform.entity.Problem;
import com.oj.platform.enums.SubmissionStatus;
import com.oj.platform.entity.TestCase;
import com.oj.platform.judge.comparator.OutputComparator;
import com.oj.platform.judge.model.ExecutionResult;
import com.oj.platform.judge.model.ProcessRunResult;
import com.oj.platform.judge.model.TestCaseResult;
import com.oj.platform.judge.runner.ProcessRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Executes Python code submissions in an isolated subprocess.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PythonExecutor implements CodeExecutor {

    private final ProcessRunner processRunner;
    private final OutputComparator outputComparator;

    @Override
    public boolean supports(Language language) {
        return language == Language.PYTHON;
    }

    @Override
    public ExecutionResult execute(String sourceCode, Problem problem, List<TestCase> testCases) {
        Path tempDir = null;

        try {
            tempDir = Files.createTempDirectory("oj-py-sub-");
            File workingDir = tempDir.toFile();

            // 1. Write source file: solution.py
            File sourceFile = new File(workingDir, "solution.py");
            Files.writeString(sourceFile.toPath(), sourceCode);

            long timeLimitMs = (problem.getTimeLimitMs() != null && problem.getTimeLimitMs() > 0)
                    ? problem.getTimeLimitMs() : 2000;

            String pythonExecutable = getPythonExecutable();
            List<String> runCommand = List.of(pythonExecutable, "-u", sourceFile.getName());

            List<TestCaseResult> testCaseResults = new ArrayList<>();
            int passedCount = 0;
            long maxExecutionTime = 0;
            SubmissionStatus finalStatus = SubmissionStatus.ACCEPTED;
            String overallError = null;

            for (TestCase testCase : testCases) {
                ProcessRunResult runResult = processRunner.run(runCommand, workingDir, testCase.getInput(), timeLimitMs);
                maxExecutionTime = Math.max(maxExecutionTime, runResult.getExecutionTimeMs());

                TestCaseResult tcResult = TestCaseResult.builder()
                        .testCaseId(testCase.getId())
                        .executionTimeMs(runResult.getExecutionTimeMs())
                        .memoryUsedKb(runResult.getMemoryUsedKb())
                        .actualOutput(runResult.getStdout())
                        .expectedOutput(testCase.getExpectedOutput())
                        .build();

                if (runResult.isTimedOut()) {
                    tcResult.setStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
                    tcResult.setErrorMessage("Time Limit Exceeded (" + timeLimitMs + " ms)");
                    if (finalStatus == SubmissionStatus.ACCEPTED) {
                        finalStatus = SubmissionStatus.TIME_LIMIT_EXCEEDED;
                        overallError = "Time Limit Exceeded on test case " + (testCaseResults.size() + 1);
                    }
                    testCaseResults.add(tcResult);
                    break;
                } else if (runResult.getExitCode() != 0) {
                    String stderr = sanitizePythonError(runResult.getStderr(), workingDir.getAbsolutePath());
                    boolean isSyntaxError = stderr.contains("SyntaxError") || stderr.contains("IndentationError") || stderr.contains("TabError");
                    SubmissionStatus errStatus = isSyntaxError ? SubmissionStatus.COMPILATION_ERROR : SubmissionStatus.RUNTIME_ERROR;

                    tcResult.setStatus(errStatus);
                    tcResult.setErrorMessage(stderr);
                    if (finalStatus == SubmissionStatus.ACCEPTED) {
                        finalStatus = errStatus;
                        overallError = stderr;
                    }
                    testCaseResults.add(tcResult);
                    break;
                } else {
                    boolean isMatch = outputComparator.compare(runResult.getStdout(), testCase.getExpectedOutput());
                    if (isMatch) {
                        tcResult.setStatus(SubmissionStatus.ACCEPTED);
                        passedCount++;
                    } else {
                        tcResult.setStatus(SubmissionStatus.WRONG_ANSWER);
                        if (finalStatus == SubmissionStatus.ACCEPTED) {
                            finalStatus = SubmissionStatus.WRONG_ANSWER;
                            overallError = "Wrong answer on test case " + (testCaseResults.size() + 1);
                        }
                    }
                    testCaseResults.add(tcResult);
                    if (!isMatch) {
                        break;
                    }
                }
            }

            return ExecutionResult.builder()
                    .status(finalStatus)
                    .executionTimeMs(maxExecutionTime)
                    .memoryUsedKb(0)
                    .passedTestCases(passedCount)
                    .totalTestCases(testCases.size())
                    .errorMessage(overallError)
                    .testCaseResults(testCaseResults)
                    .build();

        } catch (Exception e) {
            log.error("Unhandled error during Python execution: {}", e.getMessage(), e);
            return ExecutionResult.builder()
                    .status(SubmissionStatus.RUNTIME_ERROR)
                    .executionTimeMs(0)
                    .memoryUsedKb(0)
                    .passedTestCases(0)
                    .totalTestCases(testCases.size())
                    .errorMessage("Execution failed: " + e.getMessage())
                    .build();
        } finally {
            if (tempDir != null) {
                deleteDirectoryRecursively(tempDir.toFile());
            }
        }
    }

    private String sanitizePythonError(String stderr, String absolutePath) {
        if (stderr == null) return "";
        return stderr.replace(absolutePath + File.separator, "")
                     .replace(absolutePath, "");
    }

    private String getPythonExecutable() {
        // Try common Python command names across Windows and Linux
        String[] candidates = {"python3", "python", "py"};
        for (String candidate : candidates) {
            try {
                Process p = new ProcessBuilder(candidate, "--version").start();
                if (p.waitFor(2, java.util.concurrent.TimeUnit.SECONDS) && p.exitValue() == 0) {
                    return candidate;
                }
            } catch (Exception ignored) {
            }
        }
        return "python";
    }

    private void deleteDirectoryRecursively(File directory) {
        try {
            if (directory.exists()) {
                Files.walk(directory.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            log.warn("Failed to delete temp dir {}: {}", directory.getAbsolutePath(), e.getMessage());
        }
    }
}
