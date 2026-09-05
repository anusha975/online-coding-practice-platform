package com.oj.platform.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Citation metadata for a retrieved document chunk in RAG responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrievedSourceItem {

    private String chunkId;
    private String documentId;
    private String title;
    private String concept;
    private String topic;
    private String difficulty;
    private String language;
    private String source;
    private double similarityScore; // 0.0 - 1.0 (e.g. 0.89)
    private String snippet;
}
