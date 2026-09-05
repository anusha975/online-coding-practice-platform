package com.oj.platform.rag.embedding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Deterministic, high-dimensional semantic feature vectorizer for RAG knowledge retrieval.
 * Employs sublinear TF scaling, word & character n-grams, semantic anchor projections,
 * and L2 unit-norm normalization to deliver fast, zero-dependency cosine similarity search.
 */
@Service
@Slf4j
public class SemanticFeatureVectorizer implements EmbeddingService {

    public static final int VECTOR_DIMENSION = 128;
    private static final Pattern WORD_SPLITTER = Pattern.compile("[^a-zA-Z0-9_+#]+");

    // Predefined semantic concept anchor clusters to provide dense semantic clustering
    private static final Map<String, Integer> CONCEPT_ANCHORS = new LinkedHashMap<>();

    static {
        // Data Structures & Algorithms
        registerAnchorCluster(0, "binary", "search", "divide", "conquer", "sorted", "mid", "lower_bound", "upper_bound");
        registerAnchorCluster(8, "hashmap", "hashtable", "hash", "collision", "bucket", "chaining", "load_factor", "map", "set", "o1");
        registerAnchorCluster(16, "tree", "bst", "binary_tree", "traversal", "inorder", "preorder", "postorder", "avl", "red_black", "root", "leaf");
        registerAnchorCluster(24, "graph", "bfs", "dfs", "dijkstra", "shortest_path", "topological", "cycle", "adjacency", "vertex", "edge");
        registerAnchorCluster(32, "dynamic", "programming", "memoization", "tabulation", "subproblem", "knapsack", "transition", "dp");
        registerAnchorCluster(40, "two_pointers", "sliding_window", "left", "right", "window", "pointer", "subarray", "substring");
        registerAnchorCluster(48, "heap", "priority_queue", "min_heap", "max_heap", "kth", "top_k", "heapify");
        registerAnchorCluster(56, "stack", "monotonic", "queue", "deque", "lifo", "fifo", "parentheses", "next_greater");
        registerAnchorCluster(64, "trie", "prefix", "autocomplete", "word_search", "dictionary");
        registerAnchorCluster(72, "dsu", "union_find", "disjoint_set", "connected_components", "kruskal");

        // Java Core & System
        registerAnchorCluster(80, "jvm", "memory", "stack", "heap", "garbage_collection", "gc", "metaspace", "thread", "leak", "object");
        registerAnchorCluster(88, "concurrency", "synchronized", "volatile", "lock", "thread", "atomic", "deadlock", "race_condition");
        registerAnchorCluster(96, "generics", "collections", "arraylist", "linkedlist", "treemap", "comparable", "comparator", "equals", "hashcode");

        // SQL & Databases
        registerAnchorCluster(104, "sql", "index", "b_tree", "query", "select", "join", "table", "scan", "composite_index", "explain");
        registerAnchorCluster(112, "transaction", "acid", "isolation", "atomicity", "consistency", "durability", "wal", "locking", "rollback");

        // Debugging & Complexity
        registerAnchorCluster(120, "stackoverflow", "nullpointer", "npe", "outofbounds", "overflow", "tle", "time_limit", "big_o", "complexity");
    }

    private static void registerAnchorCluster(int baseIndex, String... terms) {
        for (int i = 0; i < terms.length; i++) {
            CONCEPT_ANCHORS.put(terms[i].toLowerCase(), (baseIndex + (i % 8)) % VECTOR_DIMENSION);
        }
    }

    @Override
    public float[] generateEmbedding(String text) {
        if (text == null || text.isBlank()) {
            return new float[VECTOR_DIMENSION];
        }

        float[] vector = new float[VECTOR_DIMENSION];
        String cleanText = text.toLowerCase();
        String[] tokens = WORD_SPLITTER.split(cleanText);

        Map<String, Integer> termFreqs = new HashMap<>();
        for (String token : tokens) {
            if (token.length() >= 2) {
                termFreqs.put(token, termFreqs.getOrDefault(token, 0) + 1);
            }
        }

        // Sublinear term frequency weighting
        for (Map.Entry<String, Integer> entry : termFreqs.entrySet()) {
            String term = entry.getKey();
            int freq = entry.getValue();
            float weight = (float) (1.0 + Math.log(freq));

            // Check if term maps to a known semantic anchor
            if (CONCEPT_ANCHORS.containsKey(term)) {
                int anchorDim = CONCEPT_ANCHORS.get(term);
                vector[anchorDim] += weight * 2.5f; // Stronger semantic anchor boost
            }

            // Universal feature hashing for vocabulary coverage
            int hash1 = Math.abs(term.hashCode()) % VECTOR_DIMENSION;
            int hash2 = Math.abs((term + "_salt").hashCode()) % VECTOR_DIMENSION;
            vector[hash1] += weight * 1.0f;
            vector[hash2] += weight * 0.5f;

            // Character trigrams for morphological robustness (e.g. "searches", "searching" -> "sea", "ear", "rch")
            if (term.length() >= 4) {
                for (int i = 0; i <= term.length() - 3; i++) {
                    String trigram = term.substring(i, i + 3);
                    int triHash = Math.abs(trigram.hashCode()) % VECTOR_DIMENSION;
                    vector[triHash] += weight * 0.25f;
                }
            }
        }

        // Add 2-word phrase n-grams
        for (int i = 0; i < tokens.length - 1; i++) {
            String bigram = tokens[i] + "_" + tokens[i + 1];
            if (CONCEPT_ANCHORS.containsKey(bigram)) {
                int anchorDim = CONCEPT_ANCHORS.get(bigram);
                vector[anchorDim] += 3.0f;
            }
            int biHash = Math.abs(bigram.hashCode()) % VECTOR_DIMENSION;
            vector[biHash] += 0.75f;
        }

        // L2 Unit Normalization: ||v|| = 1.0
        normalizeL2(vector);
        return vector;
    }

    @Override
    public double cosineSimilarity(float[] vectorA, float[] vectorB) {
        if (vectorA == null || vectorB == null || vectorA.length != vectorB.length) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        double similarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        return Math.max(0.0, Math.min(1.0, similarity)); // Bound strictly in [0.0, 1.0]
    }

    private void normalizeL2(float[] vector) {
        double sumSq = 0.0;
        for (float val : vector) {
            sumSq += val * val;
        }
        if (sumSq > 0.0) {
            float norm = (float) Math.sqrt(sumSq);
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= norm;
            }
        }
    }
}
