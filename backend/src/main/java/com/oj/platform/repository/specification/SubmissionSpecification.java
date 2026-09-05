package com.oj.platform.repository.specification;

import com.oj.platform.entity.Submission;
import com.oj.platform.enums.Language;
import com.oj.platform.enums.SubmissionStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic JPA Specification for filtering Submissions.
 */
public class SubmissionSpecification {

    public static Specification<Submission> withFilters(
            Long userId,
            Long problemId,
            SubmissionStatus status,
            Language language) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }

            if (problemId != null) {
                predicates.add(cb.equal(root.get("problem").get("id"), problemId));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (language != null) {
                predicates.add(cb.equal(root.get("language"), language));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
