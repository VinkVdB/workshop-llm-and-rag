package infosupport.be;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Scanner;

@SpringBootApplication
@Slf4j
public class ModuleSixApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleSixApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(
            Assistant assistant) {
        return args -> {
            // Start the command-line chat interface
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ask me anything about pokemon!");

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