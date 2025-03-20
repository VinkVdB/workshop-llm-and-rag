package infosupport.be;

import infosupport.be.util.EmbeddingCalculator;
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
        SpringApplication.run(ModuleSevenApplication.class, args).close();
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            // Initialize some general terms
            embeddingManager.embedNewTerms(initialTerms);

            // Fluent API: Perform embedding arithmetic
            EmbeddingVector result = embed("king").minus(embed("man").minus(embed("woman")));

            // Find the top 5 closest terms to the resulting vector, excluding the terms used in the arithmetic
            // Explanation:
            //      distance(king, queen)   ~= distance(man, woman)
            //      king - queen            ~= man - woman
            //      king - (man - woman)    ~= queen
            var closestEmbeddings = embeddingManager.findTopKClosest(result, 5, List.of("king", "man", "woman"));

            // Print the results
            System.out.println("Results for embedding arithmetic (king - (man - woman)):");
            printResults(closestEmbeddings);

            // Run an interactive console to experiment with embedding arithmetic
            // Examples to try:
            //      computer - (cat - mouse)
            runInteractiveConsole();
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

    /**
     * An optional method to run a simple console-based REPL (read-eval-print loop).
     * This can be called from your CommandLineRunner or anywhere else.
     */
    public void runInteractiveConsole() {
        EmbeddingCalculator calculator = new EmbeddingCalculator(embeddingManager);

        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== Embedding Arithmetic Console ===");
        System.out.println("Enter an expression such as: king - (man - woman)");
        System.out.println("Enter 'exit' to terminate.\n");

        while (true) {
            System.out.print("Expression> ");
            String input = scanner.nextLine().trim();
            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("Goodbye!");
                break;
            }

            try {
                var result = calculator.calculate(input);
                if (result == null) {
                    continue;
                }

                // 5) Print results
                printResults(result);

            } catch (IllegalArgumentException ex) {
                System.out.println("ERROR: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("ERROR: Something went wrong. " + ex);
            }
        }
    }

    private final List<String> initialTerms = List.of("king", "queen", "man", "woman", "prince", "princess",
            "boy", "girl", "nurse", "doctor", "waiter", "waitress", "actor", "actress", "teacher", "ceo", "secretary",
            "plumber", "nanny", "programmer", "homemaker", "painter", "dancer", "singer", "prostitute", "thief",
            "businessman", "businesswoman", "policeman", "policewoman", "janitor", "priest", "nun", "smart", "dumb",
            "idiot", "genius", "intelligent", "stupid", "clever", "go", "went", "swim", "swam", "sing", "sang",
            "dance", "danced", "bring", "brought", "break", "broke", "destroy", "destroyed", "play", "played", "paint",
            "painted", "look", "looked", "fail", "failed", "fight", "fought", "betray", "betrayed", "cheat", "cheated",
            "happy", "sad", "rich", "poor", "important", "unimportant", "evil", "healthy", "ill", "high", "low",
            "conscious", "unconscious", "unknown", "known", "finished", "incomplete", "complete", "positive",
            "negative", "active", "passive", "hot", "cold", "loud", "quiet", "on", "off", "two", "2", "three", "3",
            "four", "4", "five", "5", "six", "6", "seven", "7", "clear", "clearer", "clearest", "dark", "darker",
            "darkest", "strong", "stronger", "strongest", "long", "longer", "longest", "big", "bigger", "biggest",
            "fat", "fatter", "fattest", "thin", "thinner", "thinnest", "uncle", "aunt", "niece", "cousin", "grandson",
            "nephew", "brother", "sister", "heir", "heiress", "son", "daughter", "father", "mother", "grandfather",
            "grandmother", "bird", "wolf", "cat", "dog", "fish", "whale", "dolphin", "shark", "elephant", "tiger",
            "mouse", "rat", "snake", "lizard", "crocodile", "alligator", "lion", "bear", "penguin", "owl", "eagle",
            "sparrow", "swan", "duck", "goose", "chicken", "rooster", "cow", "bull", "horse", "donkey", "zebra",
            "beans", "carrots", "potatoes", "tomatoes", "cucumbers", "lettuce", "spinach", "broccoli", "cauliflower",
            "cabbage", "onions", "garlic", "peppers", "chili", "pumpkin", "zucchini", "eggplant", "mushrooms", "corn",
            "rice", "wheat", "barley", "oats", "rye", "millet", "quinoa", "lentils", "chickpeas", "peas", "sushi",
            "japan", "germany", "sauerkraut", "france", "baguette", "italy", "pasta", "spain", "paella");
}
