package infosupport.be;

import infosupport.be.plot.EmbeddingPlotter;
import infosupport.be.util.EmbeddingCalculator;
import infosupport.be.util.EmbeddingManager;
import infosupport.be.util.EmbeddingVector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class ModuleOneApplication implements CommandLineRunner {

    private final EmbeddingManager embeddingManager;
    private final EmbeddingCalculator embeddingCalculator;
    private final EmbeddingPlotter embeddingPlotter;

    public static void main(String[] args) {
        SpringApplication.run(ModuleOneApplication.class, args).close();
    }

    @Override
    public void run(String... args) {
        // 1) Initialize some general terms
        embeddingManager.embedNewTerms(initialTerms);

        // 2) Fluent API: Perform embedding arithmetic
        EmbeddingVector result = embed("king").minus(
                embed("man").minus(embed("woman"))
        );

        // 3) Find the top 5 closest terms to the resulting vector, excluding the terms used in the arithmetic
        // Explanation:
        //      distance(man, woman)    ~= distance(king, queen)
        //      man - woman             ~= king - queen
        //      queen                   ~= king - (man - woman)
        var closestEmbeddings = embeddingManager.findTopKClosest(result, 5, List.of("king", "man", "woman"));

        // 4) Print the results
        System.out.println("Results for embedding arithmetic (king - (man - woman)):");
        printResults(closestEmbeddings);

        // EXTRAS:

        // A) Run an interactive console to experiment with embedding arithmetic
        // Examples to try:
        //      sushi - (japan - germany) // What's a typical German dish?
        //      computer - (cat - mouse) // If the computer were a cat, who would be the mouse?
        //      "Albert Einstein" - (genius - idiot) // Assuming Einstein fits best for genius, who fits for idiot?
        runInteractiveConsole();

        // B) Plot words on a 2D canvas
        // See https://lamyiowce.github.io/word2viz/
        //      music genres // plotted on sad-happy & slow-fast
        //      professions // plotted on he-she & poor-rich
        List<String> words = List.of(
                "man", "woman", "father", "mother", "king", "queen",
                "uncle", "aunt", "nephew", "niece", "brother", "sister",
                "duchess", "duke", "prince", "princess", "emperor", "empress",
                "waiter", "waitress", "actor", "actress", "steward", "stewardess"
        );
        plotWordsOnCanvas(words, "he", "she", "poor", "rich");
    }

    // Method to shorten embedding a term, to improve fluency
    private EmbeddingVector embed(String term) {
        return embeddingManager.valueOf(term);
    }

    // Method to print the results
    private void printResults(List<Map.Entry<String, Double>> results) {
        for (Map.Entry<String, Double> entry : results) {
            System.out.printf("Proximity: %.3f, Term: %s%n", entry.getValue(), entry.getKey());
        }
    }

    /**
     * An optional method to run a simple console-based REPL (read-eval-print loop).
     * This can be called from your CommandLineRunner or anywhere else.
     */
    public void runInteractiveConsole() {
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
                printResults(embeddingCalculator.calculate(input));
            } catch (IllegalArgumentException ex) {
                log.warn(ex.getMessage());
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }

    /**
     * An optional method to plot words on a 2D canvas.
     * This can be called from your CommandLineRunner or anywhere else.
     */
    public void plotWordsOnCanvas(List<String> words, String xLabelLeft, String xLabelRight, String yLabelBottom, String yLabelTop) {
        // 3) Build an output path that includes the datetime and the chosen axes
        Path outputPath = createOutputPath(
                xLabelLeft + xLabelRight,
                yLabelBottom + yLabelTop
        );

        // 4) Plot the data (this method handles embedding + axis creation)
        plotWordsOnAxes(
                xLabelLeft,
                xLabelRight,
                yLabelBottom,
                yLabelTop,
                words,
                outputPath,
                1600,
                1200
        );

        log.info("Plot saved to {}", outputPath.toAbsolutePath());
    }

    /**
     * Embeds axis terms, creates the axis vectors (axisX, axisY),
     * embeds the given words, then plots everything to a PNG file.
     */
    private void plotWordsOnAxes(
            String xLabelLeft,
            String xLabelRight,
            String yLabelBottom,
            String yLabelTop,
            List<String> words,
            Path outputPath,
            int width,
            int height
    ) {
        // 1) Embed the given words and axis terms if not already present
        embeddingManager.embedNewTerms(List.of(xLabelLeft, xLabelRight, yLabelBottom, yLabelTop));
        embeddingManager.embedNewTerms(words);

        // 2) Plot
        embeddingPlotter.plot2DToImage(
                words,
                width,
                height,
                xLabelLeft,    // X-axis left label
                xLabelRight,   // X-axis right label
                yLabelBottom,  // Y-axis bottom label
                yLabelTop,     // Y-axis top label
                outputPath
        );
    }

    /**
     * Builds an output path with a filename including the datetime and short axis labels.
     * Example: "output/embedding_plot_20250321_173042_shehe_poorrich.png"
     */
    private Path createOutputPath(
            String xAxisSuffix,
            String yAxisSuffix
    ) {
        // current datetime as yyyyMMdd_HHmmss
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // combine baseName, timestamp, and axis info
        String fileName = String.format(
                "embedding_plot_%s_%s_%s.png",
                timestamp,
                xAxisSuffix,
                yAxisSuffix
        );

        return Paths.get("plots", fileName);
    }

    // TODO: load in English dictionary instead?
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
            "japan", "germany", "sauerkraut", "france", "baguette", "italy", "pasta", "spain", "paella", "popeye",
            "Albert Einstein", "Donald Trump", "Brad Pitt", "Elvis Presley", "Leonardo Da Vinci", "Bruce Lee",
            "Marilyn Monroe", "Charlie Chaplin", "Michael Jackson", "Madonna");
}
