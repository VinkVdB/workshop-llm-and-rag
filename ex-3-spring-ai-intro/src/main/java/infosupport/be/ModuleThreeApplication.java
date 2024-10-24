package infosupport.be;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Scanner;

@SpringBootApplication
@Slf4j
public class ModuleThreeApplication {

    @Autowired
    private Assistant assistant;

    public static void main(String[] args) {
        SpringApplication.run(ModuleThreeApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        // Start the command-line chat interface
        Scanner scanner = new Scanner(System.in);
        System.out.print("How can I help you today? Type 'exit' to quit.");

        // Start the chat loop
        while (true) {
            System.out.print("\n> ");
            String userMessage = scanner.nextLine();

            if (userMessage.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

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
    }
}