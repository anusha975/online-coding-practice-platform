package com.oj.platform.entity;

import com.oj.platform.enums.Difficulty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Problem entity representing coding problems in the platform.
 */
@Entity
@Table(
        name = "problems",
        indexes = {
                @Index(name = "idx_problems_title", columnList = "title", unique = true),
                @Index(name = "idx_problems_difficulty", columnList = "difficulty"),
                @Index(name = "idx_problems_category", columnList = "category"),
                @Index(name = "idx_problems_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, unique = true, length = 200)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "LONGTEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false, length = 20)
    private Difficulty difficulty;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "constraints", columnDefinition = "TEXT")
    private String constraints;

    @Column(name = "input_format", columnDefinition = "TEXT")
    private String inputFormat;

    @Column(name = "output_format", columnDefinition = "TEXT")
    private String outputFormat;

    @Column(name = "sample_input", columnDefinition = "TEXT")
    private String sampleInput;

    @Column(name = "sample_output", columnDefinition = "TEXT")
    private String sampleOutput;

    @Column(name = "time_limit_ms", nullable = false)
    @Builder.Default
    private Integer timeLimitMs = 2000;

    @Column(name = "memory_limit_mb", nullable = false)
    @Builder.Default
    private Integer memoryLimitMb = 256;
}
