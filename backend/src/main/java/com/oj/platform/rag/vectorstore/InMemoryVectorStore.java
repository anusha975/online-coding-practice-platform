package com.oj.platform.rag.vectorstore;

import com.oj.platform.rag.embedding.EmbeddingService;
import com.oj.platform.rag.model.KnowledgeChunk;
import com.oj.platform.rag.model.ScoredChunk;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * High-performance in-memory vector store utilizing Cosine Similarity, metadata filtering,
 * and keyword boost re-ranking for educational knowledge retrieval.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryVectorStore implements VectorStore {

    private final EmbeddingService embeddingService;
    private final Map<String, KnowledgeChunk> chunksMap = new ConcurrentHashMap<>();

    @Override
    public void store(KnowledgeChunk chunk) {
        if (chunk == null || chunk.getChunkId() == null) return;
        if (chunk.getEmbedding() == null || chunk.getEmbedding().length == 0) {
            String embedText = (chunk.getTitle() != null ? chunk.getTitle() + " " : "")
                    + (chunk.getConcept() != null ? chunk.getConcept() + " " : "")
                    + (chunk.getText() != null ? chunk.getText() : "");
            chunk.setEmbedding(embeddingService.generateEmbedding(embedText));
        }
        chunksMap.put(chunk.getChunkId(), chunk);
    }

    @Override
    public void storeAll(List<KnowledgeChunk> chunks) {
        if (chunks == null || chunks.isEmpty()) return;
        for (KnowledgeChunk chunk : chunks) {
            store(chunk);
        }
        log.info("Indexed {} educational chunks into InMemoryVectorStore. Total stored: {}", chunks.size(), chunksMap.size());
    }

    @Override
    public List<ScoredChunk> search(String query, int topK, String topicFilter, String languageFilter, String difficultyFilter) {
        if (query == null || query.isBlank() || chunksMap.isEmpty()) {
            return Collections.emptyList();
        }

        int k = Math.max(1, topK);
        float[] queryEmbedding = embeddingService.generateEmbedding(query);
        String cleanQuery = query.toLowerCase();
        Set<String> queryTokens = Arrays.stream(cleanQuery.split("[^a-zA-Z0-9_+#]+"))
                .filter(t -> t.length() >= 3)
                .collect(Collectors.toSet());

        List<ScoredChunk> scoredList = new ArrayList<>();

        for (KnowledgeChunk chunk : chunksMap.values()) {
            // Apply Metadata Filters if specified
            if (topicFilter != null && !topicFilter.equalsIgnoreCase("ALL") && !topicFilter.isBlank()) {
                if (chunk.getTopic() != null && !chunk.getTopic().equalsIgnoreCase(topicFilter)) {
                    continue;
                }
            }
            if (languageFilter != null && !languageFilter.equalsIgnoreCase("ALL") && !languageFilter.isBlank()) {
                if (chunk.getLanguage() != null && !chunk.getLanguage().equalsIgnoreCase("General")
                        && !chunk.getLanguage().equalsIgnoreCase(languageFilter)) {
                    continue;
                }
            }
            if (difficultyFilter != null && !difficultyFilter.equalsIgnoreCase("ALL") && !difficultyFilter.isBlank()) {
                if (chunk.getDifficulty() != null && !chunk.getDifficulty().equalsIgnoreCase(difficultyFilter)) {
                    continue;
                }
            }

            // Calculate Base Cosine Similarity
            double cosineSim = embeddingService.cosineSimilarity(queryEmbedding, chunk.getEmbedding());

            // Keyword boost for exact concept or title matches
            double keywordBoost = 0.0;
            if (chunk.getConcept() != null && cleanQuery.contains(chunk.getConcept().toLowerCase())) {
                keywordBoost += 0.25;
            }
            if (chunk.getTitle() != null && cleanQuery.contains(chunk.getTitle().toLowerCase())) {
                keywordBoost += 0.15;
            }
            if (chunk.getKeywords() != null) {
                for (String kw : chunk.getKeywords()) {
                    if (queryTokens.contains(kw.toLowerCase()) || cleanQuery.contains(kw.toLowerCase())) {
                        keywordBoost += 0.05;
                    }
                }
            }

            double combinedScore = Math.min(1.0, (cosineSim * 0.75) + (Math.min(0.25, keywordBoost)));
            scoredList.add(new ScoredChunk(chunk, combinedScore));
        }

        // Sort descending by similarity score
        Collections.sort(scoredList);

        return scoredList.stream().limit(k).collect(Collectors.toList());
    }

    @Override
    public void clear() {
        chunksMap.clear();
        log.info("Cleared all chunks from InMemoryVectorStore.");
    }

    @Override
    public int size() {
        return chunksMap.size();
    }

    @Override
    public List<KnowledgeChunk> getAllChunks() {
        return new ArrayList<>(chunksMap.values());
    }
}
