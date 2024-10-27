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

    @Bean
    public CommandLineRunner runner(Assistant assistant) {
        return args -> {
            // Start the command-line chat interface
            Scanner scanner = new Scanner(System.in);
            System.out.print("Please provide your username and password to hear a joke!");

            // Start the chat loop
            while (true) {
                System.out.print("\n> ");
                String userMessage = scanner.nextLine();

                assistant.chat("default-chat-id", userMessage)
                        // Delay each message by 10ms to simulate chat
                        .delayElements(Duration.ofMillis(50))
                        .doOnNext(System.out::print)
                        .onErrorResume(e -> {
                            // Return empty to prevent blocking
                            return Flux.empty();
                        })
                        // Block until we get the full response
                        .blockLast();
            }
        };
    }
}