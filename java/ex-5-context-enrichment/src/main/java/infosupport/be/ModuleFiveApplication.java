package infosupport.be;

import infosupport.be.domain.Joke;
import infosupport.be.services.JokeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Scanner;

@SpringBootApplication
public class ModuleFiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleFiveApplication.class, args);
    }

    @Autowired
    private JokeService jokeService;

    @Bean
    public CommandLineRunner runner(Assistant assistant) {
        return args -> {
            // Start the command-line chat interface
            Scanner scanner = new Scanner(System.in);
            System.out.print("I tell jokes to 'John Doe', how can I help you today?");
            var jokes = jokeService.getAllJokes().stream().map(Joke::toString).toList();

            // Start the chat loop
            while (true) {
                System.out.print("\n> ");
                String userMessage = scanner.nextLine();

                assistant.chat("default-chat-id", userMessage, jokes)
                        .delayElements(Duration.ofMillis(50))
                        .doOnNext(System.out::print)
                        .onErrorResume(e -> Flux.empty())
                        .blockLast();
            }
        };
    }
}