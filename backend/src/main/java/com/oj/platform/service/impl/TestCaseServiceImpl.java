package com.oj.platform.service.impl;

import com.oj.platform.dto.request.TestCaseCreateRequest;
import com.oj.platform.dto.request.TestCaseUpdateRequest;
import com.oj.platform.dto.response.TestCaseAdminResponse;
import com.oj.platform.dto.response.TestCaseSampleResponse;
import com.oj.platform.entity.Problem;
import com.oj.platform.entity.TestCase;
import com.oj.platform.exception.ResourceNotFoundException;
import com.oj.platform.repository.ProblemRepository;
import com.oj.platform.repository.TestCaseRepository;
import com.oj.platform.service.TestCaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of TestCaseService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestCaseServiceImpl implements TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final ProblemRepository problemRepository;

    @Override
    @Transactional
    public TestCaseAdminResponse createTestCase(Long problemId, TestCaseCreateRequest request) {
        log.info("Creating test case for problem ID: {}, isHidden: {}", problemId, request.isHidden());

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", problemId));

        TestCase testCase = TestCase.builder()
                .problem(problem)
                .input(request.getInput())
                .expectedOutput(request.getExpectedOutput())
                .hidden(request.isHidden())
                .build();

        TestCase saved = testCaseRepository.save(testCase);
        log.info("Test case created successfully with ID: {}", saved.getId());

        return TestCaseAdminResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestCaseSampleResponse> getSampleTestCases(Long problemId) {
        log.info("Fetching sample test cases for problem ID: {}", problemId);

        if (!problemRepository.existsById(problemId)) {
            throw new ResourceNotFoundException("Problem", "id", problemId);
        }

        // Strictly query test cases where hidden == false
        List<TestCase> sampleTestCases = testCaseRepository.findByProblemIdAndHiddenFalse(problemId);

        return sampleTestCases.stream()
                .map(TestCaseSampleResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestCaseAdminResponse> getAllTestCasesForProblem(Long problemId) {
        log.info("Admin fetching all test cases for problem ID: {}", problemId);

        if (!problemRepository.existsById(problemId)) {
            throw new ResourceNotFoundException("Problem", "id", problemId);
        }

        List<TestCase> testCases = testCaseRepository.findByProblemId(problemId);

        return testCases.stream()
                .map(TestCaseAdminResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TestCaseAdminResponse getTestCaseById(Long id) {
        log.info("Admin fetching test case by ID: {}", id);

        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase", "id", id));

        return TestCaseAdminResponse.fromEntity(testCase);
    }

    @Override
    @Transactional
    public TestCaseAdminResponse updateTestCase(Long id, TestCaseUpdateRequest request) {
        log.info("Admin updating test case ID: {}", id);

        TestCase testCase = testCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase", "id", id));

        testCase.setInput(request.getInput());
        testCase.setExpectedOutput(request.getExpectedOutput());
        testCase.setHidden(request.isHidden());

        TestCase updated = testCaseRepository.save(testCase);
        log.info("Test case ID: {} updated successfully.", updated.getId());

        return TestCaseAdminResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteTestCase(Long id) {
        log.info("Admin deleting test case ID: {}", id);

        if (!testCaseRepository.existsById(id)) {
            throw new ResourceNotFoundException("TestCase", "id", id);
        }

        testCaseRepository.deleteById(id);
        log.info("Test case ID: {} deleted successfully.", id);
    }
}
