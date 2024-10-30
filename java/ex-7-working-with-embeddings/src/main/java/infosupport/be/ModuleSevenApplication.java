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

    private final List<String> initialTerms = List.of(
            "king", "queen", "man", "woman", "prince", "princess", "boy", "girl",
            "nurse", "doctor", "waiter", "waitress", "actor", "actress", "teacher", "ceo", "secretary",
            "plumber", "nanny", "programmer", "homemaker", "painter", "dancer",
            "singer", "prostitute", "thief", "businessman", "businesswoman",
            "policeman", "policewoman", "janitor", "priest",
            "nun", "smart", "dumb", "idiot", "genius", "intelligent",
            "stupid", "clever", "go", "went",
            "swim", "swam", "sing", "sang",
            "dance", "danced", "bring", "brought",
            "break", "broke", "destroy", "destroyed",
            "play", "played", "paint", "painted",
            "look", "looked", "fail", "failed",
            "fight", "fought", "betray", "betrayed",
            "cheat", "cheated", "happy", "sad",
            "rich", "poor", "important", "unimportant",
            "evil", "healthy", "ill",
            "high", "low", "conscious", "unconscious",
            "unknown", "known", "finished", "incomplete", "complete",
            "positive", "negative", "active", "passive",
            "hot", "cold", "loud", "quiet",
            "on", "off", "two", "2",
            "three", "3", "four", "4",
            "five", "5", "six", "6",
            "seven", "7", "clear", "clearer", "clearest",
            "dark", "darker", "darkest", "strong", "stronger", "strongest",
            "long", "longer", "longest", "big", "bigger", "biggest",
            "fat", "fatter", "fattest", "thin", "thinner", "thinnest",
            "man", "woman", "uncle", "aunt",
            "niece", "nephew", "brother", "sister",
            "heir", "heiress", "son", "daughter",
            "father", "mother", "grandfather", "grandmother");
}
