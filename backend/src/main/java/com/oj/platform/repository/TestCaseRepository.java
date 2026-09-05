package com.oj.platform.repository;

import com.oj.platform.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for TestCase entity.
 */
@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    /**
     * Retrieves all test cases for a problem (used by Admin and Judge engine).
     */
    List<TestCase> findByProblemId(Long problemId);

    /**
     * Retrieves only public sample test cases (where hidden = false).
     */
    List<TestCase> findByProblemIdAndHiddenFalse(Long problemId);

    /**
     * Counts the total number of test cases configured for a problem.
     */
    long countByProblemId(Long problemId);

    /**
     * Deletes all test cases for a problem.
     */
    void deleteByProblemId(Long problemId);
}
