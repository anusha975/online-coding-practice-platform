package com.oj.platform.entity;

import com.oj.platform.enums.Language;
import com.oj.platform.enums.SubmissionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Submission entity representing a user's code submission for a Problem.
 */
@Entity
@Table(
        name = "submissions",
        indexes = {
                @Index(name = "idx_submissions_user_id", columnList = "user_id"),
                @Index(name = "idx_submissions_problem_id", columnList = "problem_id"),
                @Index(name = "idx_submissions_status", columnList = "status"),
                @Index(name = "idx_submissions_user_problem", columnList = "user_id, problem_id"),
                @Index(name = "idx_submissions_submitted_at", columnList = "submitted_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "source_code", nullable = false, columnDefinition = "LONGTEXT")
    private String sourceCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false, length = 20)
    private Language language;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.PENDING;

    @Column(name = "execution_time_ms")
    private Long executionTime;

    @Column(name = "memory_used_kb")
    private Long memoryUsed;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "passed_test_cases")
    @Builder.Default
    private Integer passedTestCases = 0;

    @Column(name = "total_test_cases")
    @Builder.Default
    private Integer totalTestCases = 0;

    @CreatedDate
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;
}
