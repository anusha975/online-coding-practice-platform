package com.oj.platform.service;

import com.oj.platform.dto.request.ProblemCreateRequest;
import com.oj.platform.dto.request.ProblemUpdateRequest;
import com.oj.platform.dto.response.PageResponse;
import com.oj.platform.dto.response.ProblemResponse;
import com.oj.platform.enums.Difficulty;

import java.util.List;

/**
 * Service interface for Problem Management operations.
 */
public interface ProblemService {

    /**
     * Creates a new problem (ADMIN).
     */
    ProblemResponse createProblem(ProblemCreateRequest request);

    /**
     * Retrieves problem details by ID.
     */
    ProblemResponse getProblemById(Long id);

    /**
     * Retrieves paginated, filtered, and sorted problems.
     */
    PageResponse<ProblemResponse> getProblems(
            String search,
            Difficulty difficulty,
            String category,
            int page,
            int size,
            String sortBy,
            String sortDir
    );

    /**
     * Retrieves all distinct categories available across existing problems.
     */
    List<String> getAllCategories();

    /**
     * Updates an existing problem (ADMIN).
     */
    ProblemResponse updateProblem(Long id, ProblemUpdateRequest request);

    /**
     * Deletes a problem by ID (ADMIN).
     */
    void deleteProblem(Long id);
}
