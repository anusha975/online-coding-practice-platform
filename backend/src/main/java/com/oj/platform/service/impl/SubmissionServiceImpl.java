package com.oj.platform.service.impl;

import com.oj.platform.dto.request.SubmissionCreateRequest;
import com.oj.platform.dto.response.PageResponse;
import com.oj.platform.dto.response.SubmissionResponse;
import com.oj.platform.entity.Problem;
import com.oj.platform.entity.Submission;
import com.oj.platform.entity.User;
import com.oj.platform.enums.Language;
import com.oj.platform.enums.Role;
import com.oj.platform.enums.SubmissionStatus;
import com.oj.platform.exception.ResourceNotFoundException;
import com.oj.platform.judge.worker.SubmissionWorker;
import com.oj.platform.repository.ProblemRepository;
import com.oj.platform.repository.SubmissionRepository;
import com.oj.platform.repository.TestCaseRepository;
import com.oj.platform.repository.UserRepository;
import com.oj.platform.repository.specification.SubmissionSpecification;
import com.oj.platform.security.UserPrincipal;
import com.oj.platform.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of SubmissionService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final TestCaseRepository testCaseRepository;
    private final SubmissionWorker submissionWorker;

    @Override
    @Transactional
    public SubmissionResponse createSubmission(UserPrincipal userPrincipal, SubmissionCreateRequest request) {
        log.info("User ID: {} submitting code for Problem ID: {}, Language: {}",
                userPrincipal.getId(), request.getProblemId(), request.getLanguage());

        // 1. Validate Problem exists
        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", request.getProblemId()));

        // 2. Load User entity
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        // 3. Count total test cases for problem
        int totalTestCases = (int) testCaseRepository.countByProblemId(problem.getId());

        // 4. Create Submission with initial PENDING status
        Submission submission = Submission.builder()
                .user(user)
                .problem(problem)
                .sourceCode(request.getSourceCode())
                .language(request.getLanguage())
                .status(SubmissionStatus.PENDING)
                .passedTestCases(0)
                .totalTestCases(totalTestCases)
                .build();

        Submission saved = submissionRepository.save(submission);
        log.info("Submission saved successfully with ID: {} and status: PENDING", saved.getId());

        // 5. Dispatch async execution to isolated judge worker
        submissionWorker.processSubmissionAsync(saved.getId());

        return SubmissionResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionResponse getSubmissionById(Long id, UserPrincipal userPrincipal) {
        log.info("Fetching submission ID: {} by user ID: {}", id, userPrincipal.getId());

        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));

        // Access Control: Only owner or ADMIN can view the submission
        boolean isOwner = submission.getUser().getId().equals(userPrincipal.getId());
        boolean isAdmin = userPrincipal.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You do not have permission to view this submission.");
        }

        return SubmissionResponse.fromEntity(submission);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SubmissionResponse> getMySubmissions(
            UserPrincipal userPrincipal,
            Long problemId,
            SubmissionStatus status,
            Language language,
            int page,
            int size,
            String sortBy,
            String sortDir) {
        log.info("Fetching submissions for user ID: {}, problemId: {}, status: {}, language: {}",
                userPrincipal.getId(), problemId, status, language);

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortProperty = (sortBy != null && !sortBy.isBlank()) ? sortBy : "submittedAt";
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));

        Specification<Submission> spec = SubmissionSpecification.withFilters(
                userPrincipal.getId(),
                problemId,
                status,
                language
        );

        Page<Submission> submissionPage = submissionRepository.findAll(spec, pageable);
        return PageResponse.of(submissionPage.map(SubmissionResponse::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SubmissionResponse> getSubmissionsByProblem(
            UserPrincipal userPrincipal,
            Long problemId,
            int page,
            int size) {
        log.info("Fetching submissions for user ID: {} on problem ID: {}", userPrincipal.getId(), problemId);

        // Verify problem exists
        if (!problemRepository.existsById(problemId)) {
            throw new ResourceNotFoundException("Problem", "id", problemId);
        }

        return getMySubmissions(userPrincipal, problemId, null, null, page, size, "submittedAt", "desc");
    }
}
