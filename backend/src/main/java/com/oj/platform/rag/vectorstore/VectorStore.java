package com.oj.platform.rag.vectorstore;

import com.oj.platform.rag.model.KnowledgeChunk;
import com.oj.platform.rag.model.ScoredChunk;

import java.util.List;

/**
 * Contract for vector storage and semantic similarity search over indexed knowledge chunks.
 */
public interface VectorStore {

    /**
     * Store a single knowledge chunk.
     */
    void store(KnowledgeChunk chunk);

    /**
     * Store multiple knowledge chunks in bulk.
     */
    void storeAll(List<KnowledgeChunk> chunks);

    /**
     * Search vector store for top-K most relevant chunks matching query and optional metadata filters.
     *
     * @param query natural language question or keywords
     * @param topK maximum number of chunks to return
     * @param topicFilter optional topic filter (e.g. DATA_STRUCTURES, ALGORITHMS, JAVA_CORE, SQL_DATABASES)
     * @param languageFilter optional programming language filter (e.g. Java, Python, C++, SQL)
     * @param difficultyFilter optional difficulty filter (e.g. BEGINNER, INTERMEDIATE, ADVANCED)
     * @return ranked list of scored chunks in descending order of similarity
     */
    List<ScoredChunk> search(String query, int topK, String topicFilter, String languageFilter, String difficultyFilter);

    /**
     * Clear all indexed chunks.
     */
    void clear();

    /**
     * Return total number of stored chunks.
     */
    int size();

    /**
     * Return all stored chunks.
     */
    List<KnowledgeChunk> getAllChunks();
}
