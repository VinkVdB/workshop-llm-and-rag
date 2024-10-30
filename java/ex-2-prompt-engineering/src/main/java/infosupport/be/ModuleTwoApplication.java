package infosupport.be;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class ModuleTwoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleTwoApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(ChatClient.Builder builder) {
        return args -> {
            ChatClient chatClient = builder.build();

            // Example 1: Zero-shot Prompting
            zeroShotPrompt(chatClient);

            // Example 2: One-shot Prompting
            oneShotPrompt(chatClient);

            // Example 3: Few-shot Prompting
            fewShotPrompt(chatClient);

            // Example 4: Chain-of-thought Prompting
            chainOfThoughtPrompt(chatClient);

            // Example 5: Using System Prompts
            systemPromptExample(chatClient);
        };
    }

    private void zeroShotPrompt(ChatClient chatClient) {
        log.info("=== Zero-shot Prompting ===");
        try {
            String prompt = """
                    //TODO Translate something from english to french
                    """;
            String response = chatClient
                    .prompt(prompt)
                    .call()
                    .content();

            System.out.println("Translation:\n" + response);
        } catch (Exception e) {
            log.error("Error in zero-shot prompting", e);
        }
    }

    private void oneShotPrompt(ChatClient chatClient) {
        log.info("=== One-shot Prompting ===");
        try {
            String prompt = """
                    // TODO also translate, but add a single example sentence
                    """;

            String response = chatClient
                    .prompt(prompt)
                    .call()
                    .content();

            System.out.println("Translation:\n" + response);
        } catch (Exception e) {
            log.error("Error in one-shot prompting", e);
        }
    }

    private void fewShotPrompt(ChatClient chatClient) {
        log.info("=== Few-shot Prompting ===");
        try {
            String prompt = """
                    // TODO convert €50 to USD, add multiple examples using 1.1USD=1EUR
                    """;

            String response = chatClient
                    .prompt(prompt)
                    .call()
                    .content();

            System.out.println("€50 -> " + response.trim());
        } catch (Exception e) {
            log.error("Error in few-shot prompting", e);
        }
    }

    private void chainOfThoughtPrompt(ChatClient chatClient) {
        log.info("=== Chain-of-thought Prompting ===");
        try {
            String prompt = """
                    // TODO ask the model to calculate the distance between Antwerp and Ghent,
                    // when the distance between Antwerp and Brussels is 50km
                    // and the distance between Brussels and Ghent is 60km.
                    // Demand the model to explain its reasoning
                    """;

            String response = chatClient
                    .prompt(prompt)
                    .call()
                    .content();

            System.out.println("Answer:\n" + response);
        } catch (Exception e) {
            log.error("Error in chain-of-thought prompting", e);
        }
    }

    private void systemPromptExample(ChatClient chatClient) {
        log.info("=== Using System Prompts ===");
        try {
            // Modifying the client or even the builder, will affect all existing clients.
            // So for example, by creating a new client through the builder, if you use defaultSystem, it will affect all clients.
            // Mutate will make a new builder based on the settings from an existing client.
            var clonedClient = chatClient.mutate()
                    .defaultSystem("""
                    // TODO try to impersonate the LLM as a travel assistant
                    """)
                    .build();

            String response = clonedClient
                    .prompt("Can you recommend some attractions in Geraardsbergen?")
                    .call()
                    .content();

            System.out.println("Recommendations:\n" + response);
        } catch (Exception e) {
            log.error("Error in using system prompts", e);
        }
    }
}
