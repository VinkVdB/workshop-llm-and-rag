package infosupport.be.services;

import infosupport.be.domain.Joke;
import infosupport.be.domain.JokeCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class JokeService {

    private final List<Joke> jokes = new ArrayList<>();

    public JokeService() {
        loadJokes();
    }

    /**
     * Loads jokes from the jokes file into the jokes list.
     * Generated with ChatGPT
     */
    private void loadJokes() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("jokes.txt").getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split each line by the "|" character to separate category and joke text
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    JokeCategory category = JokeCategory.valueOf(parts[0].trim().toUpperCase());
                    String jokeText = parts[1].trim();
                    jokes.add(new Joke(category, jokeText));
                } else {
                    log.warn("Skipping malformed joke entry: {}", line);
                }
            }
            log.info("Loaded {} jokes.", jokes.size());
        } catch (IOException e) {
            log.error("Failed to load jokes from file: {}", e.getMessage());
        }
    }

    /**
     * Retrieves all jokes from the jokes file.
     *
     * @return List of Joke objects containing category and joke text
     */
    public List<Joke> getAllJokes() {
        return new ArrayList<>(jokes);
    }
}
