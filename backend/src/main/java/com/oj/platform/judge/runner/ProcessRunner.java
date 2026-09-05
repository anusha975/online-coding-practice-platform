package com.oj.platform.judge.runner;

import com.oj.platform.judge.model.ProcessRunResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Low-level utility for running external OS processes in isolation.
 *
 * Implements:
 * - Asynchronous stdout and stderr consumption to prevent OS pipe buffer deadlocks.
 * - Strict timeout watchdog with forceful process destruction.
 * - Maximum output buffer capping (1 MB) to prevent out-of-memory attacks from infinite prints.
 * - Wall-clock execution time measurement.
 */
@Slf4j
@Component
public class ProcessRunner {

    private static final int MAX_OUTPUT_BYTES = 1024 * 1024; // 1 MB max output buffer cap

    /**
     * Executes an OS process within a working directory with specified timeout and input.
     *
     * @param command          Command and arguments list
     * @param workingDirectory Working directory for execution
     * @param inputData        Data to stream to process standard input (stdin)
     * @param timeoutMs        Maximum allowed execution time in milliseconds
     * @return ProcessRunResult containing exit code, stdout, stderr, time, and status
     */
    public ProcessRunResult run(List<String> command, File workingDirectory, String inputData, long timeoutMs) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        if (workingDirectory != null) {
            processBuilder.directory(workingDirectory);
        }

        long startTime = System.currentTimeMillis();
        Process process = null;

        try {
            process = processBuilder.start();

            // 1. Write input data to stdin in background
            if (inputData != null) {
                try (OutputStream os = process.getOutputStream()) {
                    os.write(inputData.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                } catch (Exception e) {
                    // Process may have exited before/during reading stdin (e.g. early crash)
                    log.debug("Stdin write completed or pipe closed early: {}", e.getMessage());
                }
            } else {
                try {
                    process.getOutputStream().close();
                } catch (Exception ignored) {
                }
            }

            // 2. Consume stdout and stderr asynchronously to avoid deadlock
            CompletableFuture<String> stdoutFuture = readStreamAsync(process.getInputStream());
            CompletableFuture<String> stderrFuture = readStreamAsync(process.getErrorStream());

            // 3. Wait for process completion with strict timeout watchdog
            boolean completed = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
            long executionTime = System.currentTimeMillis() - startTime;

            if (!completed) {
                log.warn("Process timed out after {} ms. Forcefully terminating process tree.", timeoutMs);
                destroyProcessTree(process);
                return ProcessRunResult.builder()
                        .exitCode(-1)
                        .stdout("")
                        .stderr("Time Limit Exceeded")
                        .executionTimeMs(executionTime)
                        .memoryUsedKb(0)
                        .timedOut(true)
                        .build();
            }

            int exitCode = process.exitValue();
            String stdout = stdoutFuture.get(1, TimeUnit.SECONDS);
            String stderr = stderrFuture.get(1, TimeUnit.SECONDS);

            return ProcessRunResult.builder()
                    .exitCode(exitCode)
                    .stdout(stdout)
                    .stderr(stderr)
                    .executionTimeMs(executionTime)
                    .memoryUsedKb(0)
                    .timedOut(false)
                    .build();

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Process execution failed or interrupted: {}", e.getMessage());
            if (process != null) {
                destroyProcessTree(process);
            }
            return ProcessRunResult.builder()
                    .exitCode(-1)
                    .stdout("")
                    .stderr(e.getMessage() != null ? e.getMessage() : "Execution exception")
                    .executionTimeMs(executionTime)
                    .memoryUsedKb(0)
                    .timedOut(false)
                    .build();
        }
    }

    /**
     * Reads an input stream asynchronously up to MAX_OUTPUT_BYTES to prevent memory exhaustion.
     */
    private CompletableFuture<String> readStreamAsync(InputStream inputStream) {
        return CompletableFuture.supplyAsync(() -> {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                char[] buffer = new char[1024];
                int read;
                int totalBytes = 0;

                while ((read = reader.read(buffer, 0, buffer.length)) != -1) {
                    if (totalBytes + read > MAX_OUTPUT_BYTES) {
                        int remaining = MAX_OUTPUT_BYTES - totalBytes;
                        if (remaining > 0) {
                            sb.append(buffer, 0, remaining);
                        }
                        sb.append("\n[OUTPUT TRUNCATED: Exceeded 1MB limit]");
                        break;
                    }
                    sb.append(buffer, 0, read);
                    totalBytes += read;
                }
            } catch (Exception e) {
                log.debug("Stream reading interrupted or closed: {}", e.getMessage());
            }
            return sb.toString();
        });
    }

    /**
     * Forcefully destroys a process and all its child sub-processes.
     */
    private void destroyProcessTree(Process process) {
        try {
            process.descendants().forEach(ProcessHandle::destroyForcibly);
            process.destroyForcibly();
            process.waitFor(500, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.debug("Error while destroying process tree: {}", e.getMessage());
        }
    }
}
