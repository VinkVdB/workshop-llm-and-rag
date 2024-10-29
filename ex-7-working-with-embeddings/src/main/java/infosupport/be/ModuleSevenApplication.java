package infosupport.be;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
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
            // Initialize the EmbeddingManager with initial terms
            List<String> initialTerms = Arrays.asList("king", "queen", "man", "woman", "prince", "princess", "boy", "girl");
            var embeddingManager = new EmbeddingManager(embeddingModel, initialTerms);

            // Perform embedding arithmetic: vector1 = man - woman
            float[] vector1 = embeddingManager.subtraction("man", "woman");

            // targetVector = king - vector1
            float[] targetVector = embeddingManager.subtraction("king", vector1);

            // Find the top 5 closest terms to the targetVector
            List<Map.Entry<String, Double>> topResults = embeddingManager.findTopK(targetVector, 5);

            // Print the results
            System.out.println("Results for embedding arithmetic (king - (man - woman)):");
            printResults(topResults);
        };
    }

    // Method to print the results
    private void printResults(List<Map.Entry<String, Double>> results) {
        for (Map.Entry<String, Double> entry : results) {
            System.out.printf("Term: %s, Similarity: %.4f%n", entry.getKey(), entry.getValue());
        }
    }
}
