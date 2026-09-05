package com.oj.platform.repository;

import com.oj.platform.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Problem entity.
 *
 * Extends JpaSpecificationExecutor to support dynamic criteria queries (search, filters, sort).
 */
@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long>, JpaSpecificationExecutor<Problem> {

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Long id);

    Optional<Problem> findByTitle(String title);

    long countByDifficulty(com.oj.platform.enums.Difficulty difficulty);

    @Query("SELECT DISTINCT p.category FROM Problem p WHERE p.category IS NOT NULL AND TRIM(p.category) != '' ORDER BY p.category ASC")
    List<String> findDistinctCategories();
}

