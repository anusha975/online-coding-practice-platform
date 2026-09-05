package com.oj.platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * TestCase entity associated with a Problem.
 *
 * Distinguishes between sample test cases (visible in UI) and hidden test cases (judge-only).
 */
@Entity
@Table(
        name = "test_cases",
        indexes = {
                @Index(name = "idx_test_cases_problem_id", columnList = "problem_id"),
                @Index(name = "idx_test_cases_is_hidden", columnList = "is_hidden")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "input_data", nullable = false, columnDefinition = "LONGTEXT")
    private String input;

    @Column(name = "expected_output", nullable = false, columnDefinition = "LONGTEXT")
    private String expectedOutput;

    @Column(name = "is_hidden", nullable = false)
    @Builder.Default
    private boolean hidden = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
