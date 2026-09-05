package com.oj.platform.controller;

import com.oj.platform.dto.request.ProblemCreateRequest;
import com.oj.platform.dto.request.ProblemUpdateRequest;
import com.oj.platform.dto.response.ApiResponse;
import com.oj.platform.dto.response.PageResponse;
import com.oj.platform.dto.response.ProblemResponse;
import com.oj.platform.enums.Difficulty;
import com.oj.platform.service.ProblemService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Problem Management.
 *
 * Base Path: /api/problems
 */
@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    /**
     * Public endpoint to get paginated, filtered, and sorted problems.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProblemResponse>>> getProblems(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PageResponse<ProblemResponse> response = problemService.getProblems(
                search, difficulty, category, page, size, sortBy, sortDir
        );
        return ResponseEntity.ok(ApiResponse.success("Problems retrieved successfully", response));
    }

    /**
     * Public endpoint to get all distinct problem categories.
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<java.util.List<String>>> getCategories() {
        java.util.List<String> categories = problemService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
    }

    /**
     * Public endpoint to get problem details by ID.
     */
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<ProblemResponse>> getProblemById(@PathVariable Long id) {
        ProblemResponse response = problemService.getProblemById(id);
        return ResponseEntity.ok(ApiResponse.success("Problem retrieved successfully", response));
    }

    /**
     * Admin-only endpoint to create a new coding problem.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProblemResponse>> createProblem(@Valid @RequestBody ProblemCreateRequest request) {
        ProblemResponse response = problemService.createProblem(request);
        return new ResponseEntity<>(
                ApiResponse.success("Problem created successfully", response),
                HttpStatus.CREATED
        );
    }

    /**
     * Admin-only endpoint to update an existing problem.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProblemResponse>> updateProblem(
            @PathVariable Long id,
            @Valid @RequestBody ProblemUpdateRequest request) {
        ProblemResponse response = problemService.updateProblem(id, request);
        return ResponseEntity.ok(ApiResponse.success("Problem updated successfully", response));
    }

    /**
     * Admin-only endpoint to delete a problem.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return ResponseEntity.ok(ApiResponse.success("Problem deleted successfully", null));
    }
}
