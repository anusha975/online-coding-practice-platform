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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Executes Java code submissions in an isolated subprocess.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JavaExecutor implements CodeExecutor {

    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("(?:public\\s+)?class\\s+([A-Za-z0-9_]+)");
    private static final long COMPILE_TIMEOUT_MS = 10000; // 10 seconds compile limit

    private final ProcessRunner processRunner;
    private final OutputComparator outputComparator;

    @Override
    public boolean supports(Language language) {
        return language == Language.JAVA;
    }

    @Override
    public ExecutionResult execute(String sourceCode, Problem problem, List<TestCase> testCases) {
        String mainClassName = extractClassName(sourceCode);
        Path tempDir = null;

        try {
            tempDir = Files.createTempDirectory("oj-java-sub-");
            File workingDir = tempDir.toFile();

            // 1. Write source file (e.g., Solution.java)
            File sourceFile = new File(workingDir, mainClassName + ".java");
            Files.writeString(sourceFile.toPath(), sourceCode);

            // 2. Compile Java code: javac MainClass.java
            List<String> compileCommand = List.of(getJavacExecutable(), sourceFile.getName());
            ProcessRunResult compileResult = processRunner.run(compileCommand, workingDir, null, COMPILE_TIMEOUT_MS);

            if (compileResult.isTimedOut() || compileResult.getExitCode() != 0) {
                log.info("Java compilation failed for class {}: {}", mainClassName, compileResult.getStderr());
                String errorMsg = compileResult.isTimedOut()
                        ? "Compilation timed out after " + COMPILE_TIMEOUT_MS + " ms."
                        : sanitizeCompilerOutput(compileResult.getStderr(), workingDir.getAbsolutePath());

                return ExecutionResult.builder()
                        .status(SubmissionStatus.COMPILATION_ERROR)
                        .executionTimeMs(compileResult.getExecutionTimeMs())
                        .memoryUsedKb(0)
                        .passedTestCases(0)
                        .totalTestCases(testCases.size())
                        .errorMessage(errorMsg)
                        .build();
            }

            // 3. Execute compiled class against test cases
            int memoryLimitMb = (problem.getMemoryLimitMb() != null && problem.getMemoryLimitMb() > 0)
                    ? problem.getMemoryLimitMb() : 256;
            long timeLimitMs = (problem.getTimeLimitMs() != null && problem.getTimeLimitMs() > 0)
                    ? problem.getTimeLimitMs() : 2000;

            List<String> runCommand = List.of(
                    getJavaExecutable(),
                    "-Xmx" + memoryLimitMb + "m",
                    "-Xms32m",
                    "-cp",
                    ".",
                    mainClassName
            );

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
                    break; // Stop further test case evaluation on TLE
                } else if (runResult.getExitCode() != 0) {
                    tcResult.setStatus(SubmissionStatus.RUNTIME_ERROR);
                    tcResult.setErrorMessage(runResult.getStderr());
                    if (finalStatus == SubmissionStatus.ACCEPTED) {
                        finalStatus = SubmissionStatus.RUNTIME_ERROR;
                        overallError = runResult.getStderr();
                    }
                    testCaseResults.add(tcResult);
                    break; // Stop further test case evaluation on Runtime Error
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
                        break; // Fail fast on wrong answer
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
            log.error("Unhandled error during Java execution: {}", e.getMessage(), e);
            return ExecutionResult.builder()
                    .status(SubmissionStatus.RUNTIME_ERROR)
                    .executionTimeMs(0)
                    .memoryUsedKb(0)
                    .passedTestCases(0)
                    .totalTestCases(testCases.size())
                    .errorMessage("Execution failed: " + e.getMessage())
                    .build();
        } finally {
            // Clean up temporary workspace directory
            if (tempDir != null) {
                deleteDirectoryRecursively(tempDir.toFile());
            }
        }
    }

    /**
     * Extracts class name from Java source code, defaulting to "Solution".
     */
    private String extractClassName(String sourceCode) {
        if (sourceCode != null) {
            Matcher matcher = CLASS_NAME_PATTERN.matcher(sourceCode);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return "Solution";
    }

    /**
     * Strips absolute host paths from compiler error messages for security and clean display.
     */
    private String sanitizeCompilerOutput(String stderr, String absolutePath) {
        if (stderr == null) return "";
        return stderr.replace(absolutePath + File.separator, "")
                     .replace(absolutePath, "");
    }

    private String getJavacExecutable() {
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome == null || javaHome.isBlank()) {
            javaHome = System.getProperty("java.home");
        }
        if (javaHome != null) {
            File javacWin = new File(javaHome, "bin/javac.exe");
            if (javacWin.exists()) return javacWin.getAbsolutePath();
            File javacUnix = new File(javaHome, "bin/javac");
            if (javacUnix.exists()) return javacUnix.getAbsolutePath();
        }
        return "javac";
    }

    private String getJavaExecutable() {
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome == null || javaHome.isBlank()) {
            javaHome = System.getProperty("java.home");
        }
        if (javaHome != null) {
            File javaWin = new File(javaHome, "bin/java.exe");
            if (javaWin.exists()) return javaWin.getAbsolutePath();
            File javaUnix = new File(javaHome, "bin/java");
            if (javaUnix.exists()) return javaUnix.getAbsolutePath();
        }
        return "java";
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
