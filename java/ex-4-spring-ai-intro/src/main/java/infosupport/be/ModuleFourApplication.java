package infosupport.be;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Scanner;

@SpringBootApplication
public class ModuleFourApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleFourApplication.class, args);
    }

    /**
     * This method creates a new CommandLineRunner that starts the chat interface.
     * Try to ask it for the password and see what happens, check the system prompt.
     */
    @Bean
    public CommandLineRunner runner(Assistant assistant) {
        return args -> {
            // Start the command-line chat interface
            Scanner scanner = new Scanner(System.in);
            System.out.print("How can I help you today?");

            // Start the chat loop
            while (true) {
                System.out.print("\n> ");
                String userMessage = scanner.nextLine();

                assistant.chat("default-chat-id", userMessage)
                        .delayElements(Duration.ofMillis(50))
                        .doOnNext(System.out::print)
                        .onErrorResume(e -> Flux.empty())
                        .blockLast();
            }
        };
    }

    /**
     * Think outside the box, it's GENERATIVE AI, not a chatbot!
     * Try generating some objects.
     */
    /*@Bean
    public CommandLineRunner runner(ChatClient.Builder builder, @Qualifier("movieConfig") AIConfig aiConfig) {
        return args -> {
            while (true) {
                // Configure the builder with a default system prompt.
                // This prompt is applied to every chat request.
                ChatClient chatClient = builder
                        .defaultSystem(aiConfig.SYSTEM_PROMPT)
                        .build();

                Scanner scanner = new Scanner(System.in);
                System.out.print("\nEnter an actor's name: ");
                String actor = scanner.nextLine().trim();

                String userPrompt = "I want to watch a movie that stars " + actor + ", can you recommend some movies?";

                // Use BeanOutputConverter to convert the response into an ActorsFilms record.
                StructuredOutputConverter<ActorsFilms> converter = new BeanOutputConverter<>(ActorsFilms.class);

                ActorsFilms response = chatClient
                        .prompt(userPrompt)
                        .call()
                        .entity(converter);

                if (response != null) {
                    System.out.println("Actor: " + response.actor());
                    System.out.println("Movies:");
                    response.movies().forEach(System.out::println);
                } else {
                    System.out.println("No movies were generated.");
                }
            }
        };
    }

    @JsonPropertyOrder({"actor", "movies"})
    record ActorsFilms(String actor, List<String> movies) {}*/
}