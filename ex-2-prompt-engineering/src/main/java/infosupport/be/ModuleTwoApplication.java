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

            // Example 6: Correcting Incorrect Responses
            incorrectResponseExample(chatClient);
        };
    }

    private void zeroShotPrompt(ChatClient chatClient) {
        log.info("=== Zero-shot Prompting ===");
        try {
            String prompt = "Translate the following Dutch sentence to French: 'De befaamde Geraardsbergse mattentaart is genoemd naar het voornaamste ingrediënt, de 'matten' -oftewel rauwe, gestremde en uitgelekte koemelk.'";
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
                    Translate the following Dutch sentences to French:
                    Dutch: 'Ik hou van mattentaarten.'
                    French: 'J'aime les tartes au maton.'

                    Dutch: 'De trein vertrekt om vijf uur.'
                    French:
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
                    Convert the given amount from Euros to US Dollars using the following examples:
                    €10 -> $11.00
                    €20 -> $22.00
                    Do not offer your thought process, just the result!
                    
                    €50 ->
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
                    Antwerp is 50 km north of Brussels. Ghent is 60 km west of Brussels.
                    Assuming there's a direct road from Antwerp to Ghent, how many kilometers will you travel in total?
                    Please explain your reasoning step by step.
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
                    You are an expert travel assistant specialized in Belgian tourism and culinary delights.
                    Respond in a friendly, helpful, and informative manner.
                    Always end the conversation with a friendly goodbye message adressing them in regional slang.
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

    private void incorrectResponseExample(ChatClient chatClient) {
        log.info("=== Correcting Incorrect Responses ===");
        try {
            // Initial ambiguous question
            String prompt = "Is it always raining in Belgium?";

            String response = chatClient
                    .prompt(prompt)
                    .call()
                    .content();
            System.out.println("Initial Response:\n" + response);

            // Provide additional context to get a better answer
            String clarifiedPrompt = """
                    Provide a detailed answer considering Belgium's climate and seasons:
                    Is it always raining in Belgium?
                    """;

            String improvedResponse = chatClient
                    .prompt(clarifiedPrompt)
                    .call()
                    .content();

            System.out.println("Improved Response:\n" + improvedResponse);
        } catch (Exception e) {
            log.error("Error in correcting incorrect responses", e);
        }
    }
}
