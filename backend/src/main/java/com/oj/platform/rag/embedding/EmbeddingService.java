package com.oj.platform.rag.embedding;

/**
 * Interface for generating dense vector embeddings from educational documents and user search queries.
 */
public interface EmbeddingService {

    /**
     * Generate a dense vector representation for the specified input text.
     *
     * @param text input text
     * @return normalized embedding vector
     */
    float[] generateEmbedding(String text);

    /**
     * Calculate cosine similarity between two embedding vectors.
     *
     * @param vectorA first vector
     * @param vectorB second vector
     * @return cosine similarity in range [-1.0, 1.0] (typically [0.0, 1.0])
     */
    double cosineSimilarity(float[] vectorA, float[] vectorB);
}
