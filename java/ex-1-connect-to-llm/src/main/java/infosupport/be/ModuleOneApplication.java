package infosupport.be;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.time.Duration;

@SpringBootApplication
@Slf4j
public class ModuleOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleOneApplication.class, args);
    }

    /**
     * This method waits for the LLM to finish processing the chat and then prints the response.
     */
    @Bean
    public CommandLineRunner runner(ChatClient.Builder builder) {
        return args -> {
            ChatClient chatClient = builder.build();

            try {
                String response = chatClient
                        .prompt("Tell me a joke")
                        .call()
                        .content();

                System.out.println(response);
            } catch (Exception e) {
                log.error("An error occurred while processing the chat. Please try again.", e);
            }
        };
    }

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
