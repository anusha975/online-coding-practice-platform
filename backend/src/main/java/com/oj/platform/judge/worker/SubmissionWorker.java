package com.oj.platform.judge.worker;

import com.oj.platform.entity.Submission;
import com.oj.platform.enums.SubmissionStatus;
import com.oj.platform.entity.TestCase;
import com.oj.platform.judge.JudgeEngine;
import com.oj.platform.judge.model.ExecutionResult;
import com.oj.platform.repository.SubmissionRepository;
import com.oj.platform.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Asynchronous worker that processes code submissions from the background execution pool.
 *
 * Lifecycle:
 * 1. Picks up submission by ID
 * 2. Sets status to RUNNING
 * 3. Evaluates code against problem test cases in isolated subprocess
 * 4. Updates submission with final status (ACCEPTED, WRONG_ANSWER, TLE, etc.)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionWorker {

    private final SubmissionRepository submissionRepository;
    private final TestCaseRepository testCaseRepository;
    private final JudgeEngine judgeEngine;

    /**
     * Executes a submission asynchronously in a worker thread.
     *
     * @param submissionId ID of the submission to evaluate
     */
    @Async("judgeExecutorPool")
    @Transactional
    public void processSubmissionAsync(Long submissionId) {
        log.info("Worker picked up submission ID: {} for evaluation", submissionId);

        Submission submission = submissionRepository.findById(submissionId).orElse(null);
        if (submission == null) {
            log.error("Submission ID: {} not found for evaluation", submissionId);
            return;
        }

        try {
            // 1. Mark status as RUNNING
            submission.setStatus(SubmissionStatus.RUNNING);
            submission = submissionRepository.saveAndFlush(submission);

            // 2. Load all problem test cases (including hidden judge test cases)
            List<TestCase> testCases = testCaseRepository.findByProblemId(submission.getProblem().getId());

            // 3. Delegate to Judge Engine
            ExecutionResult result = judgeEngine.evaluate(
                    submission.getLanguage(),
                    submission.getSourceCode(),
                    submission.getProblem(),
                    testCases
            );

            // 4. Update Submission entity with final evaluation outcome
            submission.setStatus(result.getStatus());
            submission.setExecutionTime(result.getExecutionTimeMs());
            submission.setMemoryUsed(result.getMemoryUsedKb());
            submission.setPassedTestCases(result.getPassedTestCases());
            submission.setTotalTestCases(result.getTotalTestCases());
            submission.setErrorMessage(result.getErrorMessage());

            submissionRepository.save(submission);
            log.info("Submission ID: {} evaluated successfully. Final status: {}, Passed: {}/{}",
                    submissionId, result.getStatus(), result.getPassedTestCases(), result.getTotalTestCases());

        } catch (Exception e) {
            log.error("Unexpected failure while judging submission ID: {}", submissionId, e);
            submission.setStatus(SubmissionStatus.RUNTIME_ERROR);
            submission.setErrorMessage("Internal judge error: " + e.getMessage());
            submissionRepository.save(submission);
        }
    }
}
