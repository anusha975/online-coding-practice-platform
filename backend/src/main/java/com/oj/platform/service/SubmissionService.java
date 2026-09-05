package com.oj.platform.service;

import com.oj.platform.dto.request.SubmissionCreateRequest;
import com.oj.platform.dto.response.PageResponse;
import com.oj.platform.dto.response.SubmissionResponse;
import com.oj.platform.enums.Language;
import com.oj.platform.enums.SubmissionStatus;
import com.oj.platform.security.UserPrincipal;

/**
 * Service interface for Code Submission operations.
 */
public interface SubmissionService {

    /**
     * Records a new code submission for the logged-in user with status PENDING.
     */
    SubmissionResponse createSubmission(UserPrincipal userPrincipal, SubmissionCreateRequest request);

    /**
     * Retrieves submission details by ID (Accessible by submission owner or ADMIN).
     */
    SubmissionResponse getSubmissionById(Long id, UserPrincipal userPrincipal);

    /**
     * Retrieves paginated submission history for the logged-in user with optional filters.
     */
    PageResponse<SubmissionResponse> getMySubmissions(
            UserPrincipal userPrincipal,
            Long problemId,
            SubmissionStatus status,
            Language language,
            int page,
            int size,
            String sortBy,
            String sortDir);

    /**
     * Retrieves paginated submission history for a specific problem submitted by the logged-in user.
     */
    PageResponse<SubmissionResponse> getSubmissionsByProblem(
            UserPrincipal userPrincipal,
            Long problemId,
            int page,
            int size);
}
