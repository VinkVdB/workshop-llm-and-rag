package infosupport.be;

import infosupport.be.util.EmbeddingManager;
import infosupport.be.util.EmbeddingVector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.*;

@SpringBootApplication
@Slf4j
public class ModuleSevenApplication {

    @Autowired
    private EmbeddingManager embeddingManager;

    public static void main(String[] args) {
        SpringApplication.run(ModuleSevenApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            // Initialize some embeddings
            var initialTerms = List.of("king", "queen", "man", "woman", "prince", "princess", "boy", "girl");
            embeddingManager.embedNewTerms(initialTerms);

            // Fluent API: Perform embedding arithmetic
            EmbeddingVector result = embed("king").minus(embed("man").minus(embed("woman")));

            // Find the top 5 closest terms to the resulting vector
            var closestEmbeddings = embeddingManager.findTopK(result, 5, List.of("king", "man", "woman"));

            // Print the results
            System.out.println("Results for embedding arithmetic (king - (man - woman)):");
            printResults(closestEmbeddings);
        };
    }

    // Method to shorten embedding a term, to improve fluency
    private EmbeddingVector embed(String term) {
        return embeddingManager.valueOf(term);
    }

    // Method to print the results
    private void printResults(List<Map.Entry<String, Double>> results) {
        for (Map.Entry<String, Double> entry : results) {
            System.out.printf("Term: %s, Similarity: %.4f%n", entry.getKey(), entry.getValue());
        }
    }
}
