package com.oj.platform.controller;

import com.oj.platform.dto.request.SubmissionCreateRequest;
import com.oj.platform.dto.response.ApiResponse;
import com.oj.platform.dto.response.PageResponse;
import com.oj.platform.dto.response.SubmissionResponse;
import com.oj.platform.enums.Language;
import com.oj.platform.enums.SubmissionStatus;
import com.oj.platform.security.UserPrincipal;
import com.oj.platform.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Code Submissions.
 *
 * Base Path: /api/submissions
 */
@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    /**
     * Submits code for a problem.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SubmissionResponse>> createSubmission(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody SubmissionCreateRequest request) {
        SubmissionResponse response = submissionService.createSubmission(userPrincipal, request);
        return new ResponseEntity<>(
                ApiResponse.success("Code submitted successfully", response),
                HttpStatus.CREATED
        );
    }

    /**
     * Retrieves submission details by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubmissionResponse>> getSubmissionById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        SubmissionResponse response = submissionService.getSubmissionById(id, userPrincipal);
        return ResponseEntity.ok(ApiResponse.success("Submission retrieved successfully", response));
    }

    /**
     * Retrieves paginated submission history for the logged-in user with optional filters.
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PageResponse<SubmissionResponse>>> getMySubmissions(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) Long problemId,
            @RequestParam(required = false) SubmissionStatus status,
            @RequestParam(required = false) Language language,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PageResponse<SubmissionResponse> response = submissionService.getMySubmissions(
                userPrincipal, problemId, status, language, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Submission history retrieved successfully", response));
    }

    /**
     * Retrieves paginated submission history for a specific problem by the logged-in user.
     */
    @GetMapping("/problem/{problemId}")
    public ResponseEntity<ApiResponse<PageResponse<SubmissionResponse>>> getSubmissionsByProblem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long problemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<SubmissionResponse> response = submissionService.getSubmissionsByProblem(
                userPrincipal, problemId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Problem submission history retrieved successfully", response));
    }
}
