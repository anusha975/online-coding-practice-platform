package com.oj.platform.rag.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Encapsulates a retrieved knowledge chunk paired with its calculated similarity/relevance score.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoredChunk implements Comparable<ScoredChunk> {

    private KnowledgeChunk chunk;
    private double similarityScore; // 0.0 to 1.0 (Cosine similarity + keyword boost)

    @Override
    public int compareTo(ScoredChunk other) {
        return Double.compare(other.similarityScore, this.similarityScore); // Descending order
    }
}
