package infosupport.be.util;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class EmbeddingManager {
    private final EmbeddingModel embeddingModel;
    private final ConcurrentHashMap<String, EmbeddingVector> embeddingsMap;

    public EmbeddingManager(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
        this.embeddingsMap = new ConcurrentHashMap<>();
    }

    public EmbeddingVector valueOf(String term) {
        return embeddingsMap.computeIfAbsent(term, this::embedNewTerm);
    }

    // Methods to perform vector addition
    // <editor-fold desc="Vector addition methods">
    public EmbeddingVector addition(String term1, String term2) {
        EmbeddingVector vec1 = valueOf(term1);
        EmbeddingVector vec2 = valueOf(term2);
        return vec1.plus(vec2);
    }

    private float[] addVectors(float[] vecA, float[] vecB) {
        float[] result = new float[vecA.length];
        for (int i = 0; i < vecA.length; i++) {
            result[i] = vecA[i] + vecB[i];
        }
        return result;
    }
    // </editor-fold>

    // Methods to perform vector subtraction
    // <editor-fold desc="Vector subtraction methods">
    public EmbeddingVector subtraction(String term1, String term2) {
        EmbeddingVector vec1 = valueOf(term1);
        EmbeddingVector vec2 = valueOf(term2);
        return vec1.minus(vec2);
    }

    public EmbeddingVector subtraction(String term1, EmbeddingVector vec2) {
        EmbeddingVector vec1 = valueOf(term1);
        return vec1.minus(vec2);
    }

    public EmbeddingVector subtraction(EmbeddingVector vec1, String term2) {
        EmbeddingVector vec2 = valueOf(term2);
        return vec1.minus(vec2);
    }
    // </editor-fold>

    // Methods to find top K similar terms
    // <editor-fold desc="Top K similar terms methods">
    public List<Map.Entry<String, Double>> findTopK(String term, int amount) {
        EmbeddingVector queryVector = valueOf(term);
        return findTopK(queryVector, amount, List.of(term));
    }

    public List<Map.Entry<String, Double>> findTopK(EmbeddingVector queryVector, int amount) {
        return findTopK(queryVector, amount, null);
    }

    public List<Map.Entry<String, Double>> findTopK(EmbeddingVector queryVector, int amount, List<String> excludedTerms) {
        Map<String, Double> similarityMap = new HashMap<>();
        for (Map.Entry<String, EmbeddingVector> entry : embeddingsMap.entrySet()) {
            String term = entry.getKey();
            if (excludedTerms.contains(term)) {
                continue;
            }
            float[] embedding = entry.getValue().vector();
            double similarity = SimpleVectorStore.EmbeddingMath.cosineSimilarity(queryVector.vector(), embedding);
            similarityMap.put(term, similarity);
        }
        List<Map.Entry<String, Double>> sortedResults = new ArrayList<>(similarityMap.entrySet());
        sortedResults.sort(Map.Entry.<String, Double>comparingByValue().reversed());
        return sortedResults.stream().limit(amount).collect(Collectors.toList());
    }
    // </editor-fold>

    // Methods to add and embed new terms
    // <editor-fold desc="Add and embed new terms methods">
    public EmbeddingVector embedNewTerm(String term) {
        float[] embedding = embeddingModel.embed(term);
        EmbeddingVector vector = new EmbeddingVector(embedding);
        embeddingsMap.put(term, vector);
        return vector;
    }

    public void embedNewTerms(List<String> terms) {
        List<float[]> embeddings = embeddingModel.embed(terms);
        for (int i = 0; i < terms.size(); i++) {
            embeddingsMap.put(terms.get(i), new EmbeddingVector(embeddings.get(i)));
        }
    }
    // </editor-fold>
}