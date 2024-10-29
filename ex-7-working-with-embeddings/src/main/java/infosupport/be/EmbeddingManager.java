package infosupport.be;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EmbeddingManager {
    private final EmbeddingModel embeddingModel;
    private final ConcurrentHashMap<String, float[]> embeddingsMap;

    public EmbeddingManager(EmbeddingModel embeddingModel, List<String> initialTerms) {
        this.embeddingModel = embeddingModel;
        this.embeddingsMap = new ConcurrentHashMap<>();
        // Initialize embeddings for initial terms
        embedNewTerms(initialTerms);
    }

    public float[] valueOf(String term) {
        return embeddingsMap.computeIfAbsent(term, this::embedNewTerm);
    }

    // Methods to perform vector addition
    // <editor-fold desc="Vector addition methods">
    public float[] addition(String term1, String term2) {
        float[] vec1 = valueOf(term1);
        float[] vec2 = valueOf(term2);
        return addVectors(vec1, vec2);
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
    public float[] subtraction(String term1, String term2) {
        float[] vec1 = valueOf(term1);
        float[] vec2 = valueOf(term2);
        return subtractVectors(vec1, vec2);
    }

    public float[] subtraction(String term1, float[] vec2) {
        float[] vec1 = valueOf(term1);
        return subtractVectors(vec1, vec2);
    }

    public float[] subtraction(float[] vec1, String term2) {
        float[] vec2 = valueOf(term2);
        return subtractVectors(vec1, vec2);
    }

    private float[] subtractVectors(float[] vecA, float[] vecB) {
        float[] result = new float[vecA.length];
        for (int i = 0; i < vecA.length; i++) {
            result[i] = vecA[i] - vecB[i];
        }
        return result;
    }
    // </editor-fold>

    // Methods to find top K similar terms
    // <editor-fold desc="Top K similar terms methods">
    public List<Map.Entry<String, Double>> findTopK(String term, int amount) {
        float[] queryVector = valueOf(term);
        return findTopK(queryVector, amount, term);
    }

    public List<Map.Entry<String, Double>> findTopK(float[] queryVector, int amount) {
        return findTopK(queryVector, amount, null);
    }

    private List<Map.Entry<String, Double>> findTopK(float[] queryVector, int amount, String excludeTerm) {
        Map<String, Double> similarityMap = new HashMap<>();
        for (Map.Entry<String, float[]> entry : embeddingsMap.entrySet()) {
            String term = entry.getKey();
            if (term.equals(excludeTerm)) {
                continue;
            }
            float[] embedding = entry.getValue();
            double similarity = SimpleVectorStore.EmbeddingMath.cosineSimilarity(queryVector, embedding);
            similarityMap.put(term, similarity);
        }
        List<Map.Entry<String, Double>> sortedResults = new ArrayList<>(similarityMap.entrySet());
        sortedResults.sort(Map.Entry.<String, Double>comparingByValue().reversed());
        return sortedResults.stream().limit(amount).collect(Collectors.toList());
    }
    // </editor-fold>

    // Methods to add and embed new terms
    // <editor-fold desc="Add and embed new terms methods">
    private float[] embedNewTerm(String term) {
        // Use the embedding model to generate embedding for the term
        float[] embedding = embeddingModel.embed(term);
        embeddingsMap.put(term, embedding);
        return embedding;
    }

    private void embedNewTerms(List<String> terms) {
        // Generate embeddings for multiple terms
        List<float[]> embeddings = embeddingModel.embed(terms);
        for (int i = 0; i < terms.size(); i++) {
            embeddingsMap.put(terms.get(i), embeddings.get(i));
        }
    }
    // </editor-fold>
}
