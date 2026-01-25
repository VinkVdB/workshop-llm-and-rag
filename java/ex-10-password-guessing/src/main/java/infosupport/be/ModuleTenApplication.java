package infosupport.be;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@SpringBootApplication
@Slf4j
public class ModuleTenApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleTenApplication.class, args).close();
    }

    @Bean
    public CommandLineRunner runner(WebClient.Builder webClientBuilder,
                                    @Value("${spring.ai.openai.api-key}") String apiKey,
                                    @Value("${spring.ai.openai.chat.options.model}") String model,
                                    @Value("${password}") String password) {
        return args -> {
            WebClient client = webClientBuilder
                    .baseUrl("https://api.openai.com/v1")
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .build();

            Scanner scanner = new Scanner(System.in);
            boolean passwordGuessed = false;

            while (!passwordGuessed) {
                System.out.print("Guess the password: ");
                String userGuess = scanner.nextLine().trim();

                if (userGuess.equals(password)) {
                    System.out.println("Password guessed correctly! Ending runner.");
                    passwordGuessed = true;
                } else {
                    System.out.println("Incorrect password. Try again.");
                }
            }

            // Prepare the request body for the Chat Completions API.
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            // TODO: Add your message, history, config, ... to the request body

            try {
                System.out.println("Streaming response from OpenAI:");

                // Send the POST request
                String jsonResponse = client.post()
                        .uri("/chat/completions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                System.out.println(jsonResponse);

            } catch (Exception e) {
                log.error("An error occurred while processing the chat. Please try again.", e);
            }
        };
    }
}
