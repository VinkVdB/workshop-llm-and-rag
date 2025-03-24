package infosupport.be;

import infosupport.be.plot.EmbeddingPlotter;
import infosupport.be.util.EmbeddingManager;
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

/**
 * Demonstrates a plot of word embeddings with labeled axes and data points,
 * saving the result to a PNG file to output directory in root of the project.
 */
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class CanvasPlotApplication implements CommandLineRunner {

    private final EmbeddingManager embeddingManager;

    public static void main(String[] args) {
        SpringApplication.run(CanvasPlotApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // 1) Choose your axes
        String xLabelLeft = "she";
        String xLabelRight = "he";
        String yLabelBottom = "poor";
        String yLabelTop = "rich";

        // 2) Example words to visualize
        List<String> words = List.of(
                "man", "woman", "father", "mother", "king", "queen",
                "uncle", "aunt", "nephew", "niece", "brother", "sister",
                "duchess", "duke", "prince", "princess", "emperor", "empress",
                "waiter", "waitress", "actor", "actress", "steward", "stewardess"
        );

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
        // 1) Embed the axis terms if not already present
        embeddingManager.embedNewTerms(List.of(xLabelLeft, xLabelRight, yLabelBottom, yLabelTop));

        // 2) Embed the words
        embeddingManager.embedNewTerms(words);

        // 3) Plot
        new EmbeddingPlotter().plot2DToImage(
                embeddingManager,
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
}
