package infosupport.be;

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
        SpringApplication.run(ModuleFiveApplication.class, args).close();
    }

    @Bean
    public CommandLineRunner runner(Assistant assistant) {
        return args -> {
            // Start the command-line chat interface
            Scanner scanner = new Scanner(System.in);
            System.out.print("--- INFO: The LLM will need a username and password, before telling a joke");

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
}