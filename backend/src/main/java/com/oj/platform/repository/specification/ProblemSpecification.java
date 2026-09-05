package com.oj.platform.repository.specification;

import com.oj.platform.entity.Problem;
import com.oj.platform.enums.Difficulty;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic JPA Specification for filtering and searching Problems.
 */
public class ProblemSpecification {

    public static Specification<Problem> filterProblems(String search, Difficulty difficulty, String category) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Search filter: Matches keyword in title OR description
            if (StringUtils.hasText(search)) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate titleMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchPattern);
                Predicate descMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern);
                predicates.add(criteriaBuilder.or(titleMatch, descMatch));
            }

            // 2. Difficulty filter
            if (difficulty != null) {
                predicates.add(criteriaBuilder.equal(root.get("difficulty"), difficulty));
            }

            // 3. Category filter (case-insensitive)
            if (StringUtils.hasText(category)) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("category")),
                        category.trim().toLowerCase()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
