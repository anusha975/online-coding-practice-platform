package com.oj.platform.judge.executor;

import com.oj.platform.enums.Language;
import com.oj.platform.entity.Problem;
import com.oj.platform.entity.TestCase;
import com.oj.platform.judge.model.ExecutionResult;

import java.util.List;

/**
 * Strategy interface for executing code in a specific programming language.
 */
public interface CodeExecutor {

    /**
     * Checks if this executor supports the given programming language.
     */
    boolean supports(Language language);

    /**
     * Compiles (if applicable) and executes the source code against all test cases.
     *
     * @param sourceCode User-submitted code
     * @param problem    Target problem with time and memory constraints
     * @param testCases  List of public and hidden test cases
     * @return ExecutionResult containing overall status, metrics, and error messages
     */
    ExecutionResult execute(String sourceCode, Problem problem, List<TestCase> testCases);
}
