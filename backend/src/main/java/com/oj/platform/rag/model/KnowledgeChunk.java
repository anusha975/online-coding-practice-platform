package com.oj.platform.rag.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a segmented chunk of educational content with semantic vector embedding and metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeChunk {

    private String chunkId;
    private String documentId;
    private String title;
    private String topic;
    private String difficulty;
    private String language;
    private String source;
    private String concept;
    private String text;
    private List<String> keywords;
    private float[] embedding;
}
