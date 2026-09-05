package com.oj.platform.controller;

import com.oj.platform.dto.response.AdminStatsResponse;
import com.oj.platform.dto.response.ApiResponse;
import com.oj.platform.dto.response.PageResponse;
import com.oj.platform.dto.response.SubmissionResponse;
import com.oj.platform.entity.Submission;
import com.oj.platform.enums.Difficulty;
import com.oj.platform.enums.Language;
import com.oj.platform.enums.SubmissionStatus;
import com.oj.platform.repository.ProblemRepository;
import com.oj.platform.repository.SubmissionRepository;
import com.oj.platform.repository.TestCaseRepository;
import com.oj.platform.repository.UserRepository;
import com.oj.platform.repository.specification.SubmissionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for Administrative operations.
 *
 * All endpoints under /api/admin are protected and require ROLE_ADMIN authority.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;
    private final TestCaseRepository testCaseRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, String>>> getAdminDashboard() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Admin dashboard accessed successfully",
                        Map.of("message", "Welcome, Administrator! You have access to admin-only privileges.")
                )
        );
    }

    /**
     * Retrieves overall platform analytics and metrics.
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getPlatformStats() {
        long totalUsers = userRepository.count();
        long totalProblems = problemRepository.count();
        long totalSubmissions = submissionRepository.count();
        long totalTestCases = testCaseRepository.count();

        long easyProblems = problemRepository.countByDifficulty(Difficulty.EASY);
        long mediumProblems = problemRepository.countByDifficulty(Difficulty.MEDIUM);
        long hardProblems = problemRepository.countByDifficulty(Difficulty.HARD);

        long accepted = submissionRepository.count(
                (root, query, cb) -> cb.equal(root.get("status"), SubmissionStatus.ACCEPTED));
        long wrongAnswer = submissionRepository.count(
                (root, query, cb) -> cb.equal(root.get("status"), SubmissionStatus.WRONG_ANSWER));
        long compilationError = submissionRepository.count(
                (root, query, cb) -> cb.equal(root.get("status"), SubmissionStatus.COMPILATION_ERROR));
        long runtimeError = submissionRepository.count(
                (root, query, cb) -> cb.equal(root.get("status"), SubmissionStatus.RUNTIME_ERROR));
        long tle = submissionRepository.count(
                (root, query, cb) -> cb.equal(root.get("status"), SubmissionStatus.TIME_LIMIT_EXCEEDED));

        AdminStatsResponse stats = AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalProblems(totalProblems)
                .totalSubmissions(totalSubmissions)
                .totalTestCases(totalTestCases)
                .easyProblems(easyProblems)
                .mediumProblems(mediumProblems)
                .hardProblems(hardProblems)
                .acceptedSubmissions(accepted)
                .wrongAnswerSubmissions(wrongAnswer)
                .compilationErrorSubmissions(compilationError)
                .runtimeErrorSubmissions(runtimeError)
                .timeLimitExceededSubmissions(tle)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Platform statistics retrieved successfully", stats));
    }

    /**
     * Retrieves global paginated submission logs for auditing.
     */
    @GetMapping("/submissions")
    public ResponseEntity<ApiResponse<PageResponse<SubmissionResponse>>> getAllSubmissions(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long problemId,
            @RequestParam(required = false) SubmissionStatus status,
            @RequestParam(required = false) Language language,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortProperty = (sortBy != null && !sortBy.isBlank()) ? sortBy : "submittedAt";
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));

        Specification<Submission> spec = SubmissionSpecification.withFilters(
                userId,
                problemId,
                status,
                language
        );

        Page<Submission> submissionPage = submissionRepository.findAll(spec, pageable);
        return ResponseEntity.ok(ApiResponse.success("Global submissions retrieved successfully",
                PageResponse.of(submissionPage.map(SubmissionResponse::fromEntity))));
    }
}
