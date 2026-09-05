package com.oj.platform.repository;

import com.oj.platform.entity.Submission;
import com.oj.platform.enums.Difficulty;
import com.oj.platform.enums.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for Submission entity.
 *
 * Provides indexed queries and direct SQL aggregation for user statistics.
 */
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long>, JpaSpecificationExecutor<Submission> {

    Page<Submission> findByUserId(Long userId, Pageable pageable);

    Page<Submission> findByUserIdAndProblemId(Long userId, Long problemId, Pageable pageable);

    List<Submission> findByProblemId(Long problemId);

    /**
     * Counts total submissions for a user.
     */
    long countByUserId(Long userId);

    /**
     * Counts submissions for a user by status (e.g. ACCEPTED).
     */
    long countByUserIdAndStatus(Long userId, SubmissionStatus status);

    /**
     * Counts distinct problems solved (ACCEPTED) by a user.
     */
    @Query("SELECT COUNT(DISTINCT s.problem.id) FROM Submission s WHERE s.user.id = :userId AND s.status = :status")
    long countDistinctProblemsByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") SubmissionStatus status);

    /**
     * Counts distinct problems solved (ACCEPTED) by a user for a specific difficulty (EASY/MEDIUM/HARD).
     */
    @Query("SELECT COUNT(DISTINCT s.problem.id) FROM Submission s WHERE s.user.id = :userId AND s.status = :status AND s.problem.difficulty = :difficulty")
    long countDistinctProblemsByUserIdAndStatusAndDifficulty(
            @Param("userId") Long userId,
            @Param("status") SubmissionStatus status,
            @Param("difficulty") Difficulty difficulty);
}
