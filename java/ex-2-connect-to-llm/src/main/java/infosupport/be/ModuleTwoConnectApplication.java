package infosupport.be;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@Slf4j
public class ModuleTwoConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleTwoConnectApplication.class, args);
    }

    /**
     * This CommandLineRunner creates a WebClient instance configured to call OpenAI’s Chat API.
     * The API key is taken from application.properties or an environment variable.
     */
    @Bean
    public CommandLineRunner runner(WebClient.Builder webClientBuilder,
                                    @Value("${spring.ai.openai.api-key}") String apiKey,
                                    @Value("${spring.ai.openai.chat.options.model}") String model) {
        return args -> {
            WebClient client = webClientBuilder
                    .baseUrl("https://api.openai.com/v1")
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .build();

            // Prepare the request body for the Chat Completions API.
            // See: https://platform.openai.com/docs/api-reference/chat
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(
                    Map.of("role", "user",
                            "content", "Alright, tell me a poem!")
            ));

            try {
                System.out.println("Streaming response from OpenAI:");

                // Send the POST request and process the response as a stream.
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


    /**
     * This method waits for the LLM to finish processing the chat and then prints the response.
     */
//    @Bean
//    public CommandLineRunner runner(ChatClient.Builder builder) {
//        return args -> {
//            ChatClient chatClient = builder.build();
//
//            try {
//                String response = chatClient
//                        .prompt("Tell me a joke")
//                        .call()
//                        .content();
//
//                System.out.println(response);
//            } catch (Exception e) {
//                log.error("An error occurred while processing the chat. Please try again.", e);
//            }
//        };
//    }

    /**
     * This method streams the LLM's response as it is being processed.
     */
    /* @Bean
    public CommandLineRunner runner(ChatClient.Builder builder) {
        return args -> {
            ChatClient chatClient = builder.build();

            try{
                chatClient
                        .prompt("Tell me a joke")
                        .stream()
                        .content()
                        .delayElements(Duration.ofMillis(50))
                        .doOnNext(System.out::print)
                        .onErrorResume(e -> Flux.empty())
                        .blockLast();
            } catch (Exception e) {
                log.error("An error occurred while processing the chat. Please try again.", e);
            }
        };
    } */
}
