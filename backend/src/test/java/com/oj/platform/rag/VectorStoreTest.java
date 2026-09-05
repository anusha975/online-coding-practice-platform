package com.oj.platform.rag;

import com.oj.platform.rag.embedding.SemanticFeatureVectorizer;
import com.oj.platform.rag.knowledge.DefaultKnowledgeBaseProvider;
import com.oj.platform.rag.model.KnowledgeChunk;
import com.oj.platform.rag.model.KnowledgeDocument;
import com.oj.platform.rag.model.ScoredChunk;
import com.oj.platform.rag.pipeline.DocumentIngestionPipeline;
import com.oj.platform.rag.vectorstore.InMemoryVectorStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VectorStoreTest {

    private SemanticFeatureVectorizer vectorizer;
    private InMemoryVectorStore vectorStore;
    private DocumentIngestionPipeline ingestionPipeline;
    private DefaultKnowledgeBaseProvider knowledgeBaseProvider;

    @BeforeEach
    void setUp() {
        vectorizer = new SemanticFeatureVectorizer();
        vectorStore = new InMemoryVectorStore(vectorizer);
        knowledgeBaseProvider = new DefaultKnowledgeBaseProvider();
        ingestionPipeline = new DocumentIngestionPipeline(vectorStore, vectorizer, knowledgeBaseProvider);
        ingestionPipeline.initializeKnowledgeBase();
    }

    @Test
    @DisplayName("Vectorizer - Should generate normalized L2 unit vectors with positive cosine similarity for related terms")
    void testSemanticVectorizerCosineSimilarity() {
        float[] vecBinary = vectorizer.generateEmbedding("binary search sorted array divide and conquer");
        float[] vecSearch = vectorizer.generateEmbedding("how to perform binary search on sorted collection");
        float[] vecSql = vectorizer.generateEmbedding("sql database transaction acid isolation levels");

        double simRelated = vectorizer.cosineSimilarity(vecBinary, vecSearch);
        double simUnrelated = vectorizer.cosineSimilarity(vecBinary, vecSql);

        assertThat(simRelated).isGreaterThan(0.4);
        assertThat(simRelated).isGreaterThan(simUnrelated);
    }

    @Test
    @DisplayName("Ingestion Pipeline - Should chunk documents and index into InMemoryVectorStore")
    void testDocumentIngestion() {
        assertThat(vectorStore.size()).isGreaterThan(10);
        List<KnowledgeChunk> chunks = vectorStore.getAllChunks();
        assertThat(chunks).isNotEmpty();
        assertThat(chunks.get(0).getEmbedding()).isNotNull();
        assertThat(chunks.get(0).getEmbedding().length).isEqualTo(SemanticFeatureVectorizer.VECTOR_DIMENSION);
    }

    @Test
    @DisplayName("VectorStore Search - Should retrieve Binary Search chunks for binary search query")
    void testSearchBinarySearch() {
        List<ScoredChunk> results = vectorStore.search("Explain binary search and how to avoid integer overflow", 3, null, null, null);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getChunk().getConcept()).containsIgnoringCase("Binary Search");
        assertThat(results.get(0).getSimilarityScore()).isGreaterThan(0.3);
    }

    @Test
    @DisplayName("VectorStore Search - Should retrieve HashMap chunks for hashmap query")
    void testSearchHashMap() {
        List<ScoredChunk> results = vectorStore.search("When should I use a HashMap vs TreeMap in Java?", 3, "DATA_STRUCTURES", "Java", null);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getChunk().getConcept()).containsIgnoringCase("HashMap");
        assertThat(results.get(0).getChunk().getTopic()).isEqualTo("DATA_STRUCTURES");
    }

    @Test
    @DisplayName("VectorStore Search - Should retrieve SQL Indexing for database indexing query")
    void testSearchSqlIndexing() {
        List<ScoredChunk> results = vectorStore.search("How do B+Tree indexes work in SQL?", 3, "SQL_DATABASES", null, null);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getChunk().getConcept()).containsIgnoringCase("Indexing");
        assertThat(results.get(0).getChunk().getTopic()).isEqualTo("SQL_DATABASES");
    }
}
