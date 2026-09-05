package com.oj.platform.controller;

import com.oj.platform.dto.request.TestCaseCreateRequest;
import com.oj.platform.dto.request.TestCaseUpdateRequest;
import com.oj.platform.dto.response.ApiResponse;
import com.oj.platform.dto.response.TestCaseAdminResponse;
import com.oj.platform.dto.response.TestCaseSampleResponse;
import com.oj.platform.service.TestCaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for Test Case Management.
 *
 * Public routes:
 * - GET /api/problems/{problemId}/testcases/sample (Sample test cases only)
 *
 * Admin routes:
 * - POST /api/admin/problems/{problemId}/testcases
 * - GET /api/admin/problems/{problemId}/testcases
 * - GET /api/admin/testcases/{id}
 * - PUT /api/admin/testcases/{id}
 * - DELETE /api/admin/testcases/{id}
 */
@RestController
@RequiredArgsConstructor
public class TestCaseController {

    private final TestCaseService testCaseService;

    /**
     * Public endpoint to get sample test cases for a problem (only where hidden = false).
     */
    @GetMapping("/api/problems/{problemId}/testcases/sample")
    public ResponseEntity<ApiResponse<List<TestCaseSampleResponse>>> getSampleTestCases(
            @PathVariable Long problemId) {
        List<TestCaseSampleResponse> response = testCaseService.getSampleTestCases(problemId);
        return ResponseEntity.ok(ApiResponse.success("Sample test cases retrieved successfully", response));
    }

    /**
     * Admin-only endpoint to add a new test case to a problem.
     */
    @PostMapping("/api/admin/problems/{problemId}/testcases")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TestCaseAdminResponse>> createTestCase(
            @PathVariable Long problemId,
            @Valid @RequestBody TestCaseCreateRequest request) {
        TestCaseAdminResponse response = testCaseService.createTestCase(problemId, request);
        return new ResponseEntity<>(
                ApiResponse.success("Test case created successfully", response),
                HttpStatus.CREATED
        );
    }

    /**
     * Admin-only endpoint to view all test cases for a problem (including hidden).
     */
    @GetMapping("/api/admin/problems/{problemId}/testcases")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TestCaseAdminResponse>>> getAllTestCasesForProblem(
            @PathVariable Long problemId) {
        List<TestCaseAdminResponse> response = testCaseService.getAllTestCasesForProblem(problemId);
        return ResponseEntity.ok(ApiResponse.success("All test cases retrieved successfully", response));
    }

    /**
     * Admin-only endpoint to get a specific test case by ID.
     */
    @GetMapping("/api/admin/testcases/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TestCaseAdminResponse>> getTestCaseById(
            @PathVariable Long id) {
        TestCaseAdminResponse response = testCaseService.getTestCaseById(id);
        return ResponseEntity.ok(ApiResponse.success("Test case retrieved successfully", response));
    }

    /**
     * Admin-only endpoint to update a test case.
     */
    @PutMapping("/api/admin/testcases/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TestCaseAdminResponse>> updateTestCase(
            @PathVariable Long id,
            @Valid @RequestBody TestCaseUpdateRequest request) {
        TestCaseAdminResponse response = testCaseService.updateTestCase(id, request);
        return ResponseEntity.ok(ApiResponse.success("Test case updated successfully", response));
    }

    /**
     * Admin-only endpoint to delete a test case.
     */
    @DeleteMapping("/api/admin/testcases/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTestCase(
            @PathVariable Long id) {
        testCaseService.deleteTestCase(id);
        return ResponseEntity.ok(ApiResponse.success("Test case deleted successfully", null));
    }
}
