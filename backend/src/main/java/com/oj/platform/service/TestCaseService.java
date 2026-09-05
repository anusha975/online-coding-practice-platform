package com.oj.platform.service;

import com.oj.platform.dto.request.TestCaseCreateRequest;
import com.oj.platform.dto.request.TestCaseUpdateRequest;
import com.oj.platform.dto.response.TestCaseAdminResponse;
import com.oj.platform.dto.response.TestCaseSampleResponse;

import java.util.List;

/**
 * Service interface for Problem Test Case management.
 */
public interface TestCaseService {

    /**
     * Adds a new test case to a problem (ADMIN).
     */
    TestCaseAdminResponse createTestCase(Long problemId, TestCaseCreateRequest request);

    /**
     * Retrieves public sample test cases for a problem (hidden = false).
     * Accessible by public / regular users.
     */
    List<TestCaseSampleResponse> getSampleTestCases(Long problemId);

    /**
     * Retrieves all test cases (both public and hidden) for a problem (ADMIN).
     */
    List<TestCaseAdminResponse> getAllTestCasesForProblem(Long problemId);

    /**
     * Retrieves a test case by ID (ADMIN).
     */
    TestCaseAdminResponse getTestCaseById(Long id);

    /**
     * Updates an existing test case (ADMIN).
     */
    TestCaseAdminResponse updateTestCase(Long id, TestCaseUpdateRequest request);

    /**
     * Deletes a test case by ID (ADMIN).
     */
    void deleteTestCase(Long id);
}
