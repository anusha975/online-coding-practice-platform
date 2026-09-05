package com.oj.platform.judge.comparator;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Normalizes and compares execution output with expected output.
 *
 * Handles cross-platform line endings (\r\n vs \n) and trailing whitespace differences.
 */
@Component
public class OutputComparator {

    /**
     * Compares the actual program output with expected test case output.
     *
     * @param actualOutput   Raw standard output produced by the submission
     * @param expectedOutput Ground truth output defined in the test case
     * @return true if outputs match after normalization, false otherwise
     */
    public boolean compare(String actualOutput, String expectedOutput) {
        String normalizedActual = normalize(actualOutput);
        String normalizedExpected = normalize(expectedOutput);
        return normalizedActual.equals(normalizedExpected);
    }

    /**
     * Normalizes a string by:
     * 1. Converting all line separators (\r\n, \r) to standard \n
     * 2. Stripping trailing whitespace from each line
     * 3. Trimming leading and trailing overall whitespace
     */
    public String normalize(String text) {
        if (text == null) {
            return "";
        }

        // Standardize newlines
        String unified = text.replace("\r\n", "\n").replace("\r", "\n");

        // Trim whitespace from end of each line and rejoin
        String lineTrimmed = Arrays.stream(unified.split("\n", -1))
                .map(String::stripTrailing)
                .collect(Collectors.joining("\n"));

        // Strip overall leading and trailing whitespace
        return lineTrimmed.strip();
    }
}
