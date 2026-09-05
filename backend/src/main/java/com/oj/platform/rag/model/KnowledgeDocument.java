package com.oj.platform.rag.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a structured educational document in the platform's knowledge base.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeDocument {

    private String id;
    private String title;
    private String topic; // DATA_STRUCTURES, ALGORITHMS, JAVA_CORE, SQL_DATABASES, CODING_PATTERNS, DEBUGGING_GUIDE, SYSTEM_DESIGN
    private String difficulty; // BEGINNER, INTERMEDIATE, ADVANCED
    private String language; // Java, Python, C++, SQL, General
    private String source; // E.g., "Platform DSA Master Guide v2.1"
    private String concept; // E.g., "Binary Search", "HashMap Internals"
    private String content; // Full markdown / instructional content
    private List<String> tags;
}
