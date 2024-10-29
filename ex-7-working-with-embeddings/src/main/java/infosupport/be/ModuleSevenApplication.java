package infosupport.be;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.*;

@SpringBootApplication
@Slf4j
public class ModuleSevenApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleSevenApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(
            EmbeddingModel embeddingModel) {
        return args -> {
            List<String> terms = Arrays.asList("king", "queen", "man", "woman", "prince", "princess", "boy", "girl");

            // Generate embeddings
            List<float[]> embeddings = embeddingModel.embed(terms);

            // Perform embedding arithmetic
            float[] embeddingMan = embeddings.get(terms.indexOf("man"));
            float[] embeddingWoman = embeddings.get(terms.indexOf("woman"));
            float[] vector1 = subtractVectors(embeddingMan, embeddingWoman);

            float[] embeddingKing = embeddings.get(terms.indexOf("king"));
            float[] targetVector = subtractVectors(embeddingKing, vector1);

            // Find the closest term
            Map<String, Double> similarityMap = new HashMap<>();

            for (String term : terms) {
                if (term.equals("king") || term.equals("man") || term.equals("woman")) {
                    continue; // Skip the original terms
                }
                float[] embedding = embeddings.get(terms.indexOf(term));
                double similarity = SimpleVectorStore.EmbeddingMath.cosineSimilarity(targetVector, embedding);
                similarityMap.put(term, similarity);
            }

            // Sort and print the results
            List<Map.Entry<String, Double>> sortedResults = new ArrayList<>(similarityMap.entrySet());
            sortedResults.sort(Map.Entry.<String, Double>comparingByValue().reversed());

            System.out.println("Results for embedding arithmetic (king - (man - woman)):");
            for (Map.Entry<String, Double> entry : sortedResults) {
                System.out.printf("Term: %s, Similarity: %.4f%n", entry.getKey(), entry.getValue());
            }

        };
    }

    public static float[] subtractVectors(float[] vecA, float[] vecB) {
        float[] result = new float[vecA.length];
        for (int i = 0; i < vecA.length; i++) {
            result[i] = vecA[i] - vecB[i];
        }
        return result;
    }
}