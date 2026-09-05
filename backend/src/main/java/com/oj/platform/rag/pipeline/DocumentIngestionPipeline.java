package com.oj.platform.rag.pipeline;

import com.oj.platform.rag.embedding.EmbeddingService;
import com.oj.platform.rag.knowledge.DefaultKnowledgeBaseProvider;
import com.oj.platform.rag.model.KnowledgeChunk;
import com.oj.platform.rag.model.KnowledgeDocument;
import com.oj.platform.rag.vectorstore.VectorStore;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Ingestion pipeline responsible for chunking educational platform documents,
 * generating dense semantic embeddings, and indexing them into the VectorStore.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentIngestionPipeline {

    private final VectorStore vectorStore;
    private final EmbeddingService embeddingService;
    private final DefaultKnowledgeBaseProvider knowledgeBaseProvider;

    private static final Pattern SECTION_SPLITTER = Pattern.compile("(?m)(?=^#{1,3}\\s+)");

    @PostConstruct
    public void initializeKnowledgeBase() {
        log.info("Starting RAG document ingestion pipeline on startup...");
        List<KnowledgeDocument> initialDocs = knowledgeBaseProvider.getEducationalDocuments();
        int totalChunks = 0;

        for (KnowledgeDocument doc : initialDocs) {
            List<KnowledgeChunk> chunks = chunkDocument(doc);
            vectorStore.storeAll(chunks);
            totalChunks += chunks.size();
        }

        log.info("RAG Ingestion complete! Ingested {} documents into {} searchable semantic chunks in VectorStore.",
                initialDocs.size(), totalChunks);
    }

    /**
     * Ingest a single document into the vector store dynamically.
     */
    public List<KnowledgeChunk> ingest(KnowledgeDocument document) {
        if (document == null) return Collections.emptyList();
        List<KnowledgeChunk> chunks = chunkDocument(document);
        vectorStore.storeAll(chunks);
        log.info("Ingested dynamic document '{}' ({}) into {} chunks.",
                document.getTitle(), document.getId(), chunks.size());
        return chunks;
    }

    /**
     * Split document into logical semantic chunks based on markdown sections and size limits.
     */
    public List<KnowledgeChunk> chunkDocument(KnowledgeDocument doc) {
        if (doc == null || doc.getContent() == null || doc.getContent().isBlank()) {
            return Collections.emptyList();
        }

        List<KnowledgeChunk> chunks = new ArrayList<>();
        String content = doc.getContent().trim();
        String[] sections = SECTION_SPLITTER.split(content);

        int chunkIndex = 0;
        for (String section : sections) {
            String trimmedSection = section.trim();
            if (trimmedSection.length() < 30) continue; // Skip trivial empty sections

            // Extract section sub-heading if available
            String sectionTitle = doc.getTitle();
            String[] lines = trimmedSection.split("\\R", 2);
            if (lines.length > 0 && lines[0].startsWith("#")) {
                sectionTitle = lines[0].replace("#", "").trim();
            }

            // Extract keywords from document tags and section content
            Set<String> keywords = new HashSet<>(doc.getTags() != null ? doc.getTags() : Collections.emptyList());
            keywords.add(doc.getConcept().toLowerCase());
            keywords.add(doc.getTopic().toLowerCase());

            String chunkText = trimmedSection;
            String textForEmbedding = doc.getTitle() + " - " + sectionTitle + " : " + chunkText;
            float[] embedding = embeddingService.generateEmbedding(textForEmbedding);

            KnowledgeChunk chunk = KnowledgeChunk.builder()
                    .chunkId(doc.getId() + "#chunk-" + chunkIndex++)
                    .documentId(doc.getId())
                    .title(doc.getTitle() + " - " + sectionTitle)
                    .topic(doc.getTopic())
                    .difficulty(doc.getDifficulty())
                    .language(doc.getLanguage())
                    .source(doc.getSource())
                    .concept(doc.getConcept())
                    .text(chunkText)
                    .keywords(new ArrayList<>(keywords))
                    .embedding(embedding)
                    .build();

            chunks.add(chunk);
        }

        // Fallback: If section splitting produced no chunks, create one chunk for entire content
        if (chunks.isEmpty()) {
            String textForEmbedding = doc.getTitle() + " : " + doc.getContent();
            chunks.add(KnowledgeChunk.builder()
                    .chunkId(doc.getId() + "#chunk-0")
                    .documentId(doc.getId())
                    .title(doc.getTitle())
                    .topic(doc.getTopic())
                    .difficulty(doc.getDifficulty())
                    .language(doc.getLanguage())
                    .source(doc.getSource())
                    .concept(doc.getConcept())
                    .text(doc.getContent())
                    .keywords(doc.getTags())
                    .embedding(embeddingService.generateEmbedding(textForEmbedding))
                    .build());
        }

        return chunks;
    }
}
