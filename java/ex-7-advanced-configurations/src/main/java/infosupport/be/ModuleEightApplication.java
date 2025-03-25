package infosupport.be;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@Slf4j
public class ModuleEightApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleEightApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(ChatClient.Builder builder) {
        return args -> {
            var defaultChatOptions = extractChatOptions(builder);

            // Temperature example
            temperatureExample(builder, defaultChatOptions.copy());

            // Max Tokens example
            maxTokensExample(builder, defaultChatOptions.copy());

            // Number of Completions example, TODO it's recommended to connect an inMemoryChatMemory, otherwise it will generate near identical responses
            numberOfCompletionsExample(builder, defaultChatOptions.copy());

            // Stop Sequence example
            stopSequenceExample(builder, defaultChatOptions.copy());

            // Presence Penalty example
            presencePenaltyExample(builder, defaultChatOptions.copy());

            // Frequency Penalty example
            frequencyPenaltyExample(builder, defaultChatOptions.copy());

            // Logit Bias example, TODO does not seem to function
            logitBiasExample(builder, defaultChatOptions.copy());
        };
    }

    // <editor-fold desc="Utility methods">

    /**
     * Reflection to access the ChatOptions object which was created by Spring for Azure.
     * Is a bit overkill, since the only settings currently are deployment name and temperature.
     * But I figured I'd do it this way to not override any settings you make in application.properties.
     */
    private static OpenAiChatOptions extractChatOptions(ChatClient.Builder builder) {
        try {
            Field privateField = DefaultChatClient.DefaultChatClientRequestSpec.class.getDeclaredField("chatOptions");
            privateField.setAccessible(true);
            return (OpenAiChatOptions) privateField.get(builder.build().prompt());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access default ChatOptions", e);
        }
    }

    /**
     * Just a method that executes a prompt for a given client, to reduce redundancy.
     */
    private void executePrompt(ChatClient client, String prompt) {
        try {
            client
                    .prompt(prompt)
                    .stream()
                    .content()
                    .delayElements(Duration.ofMillis(10))
                    .doOnNext(System.out::print)
                    .doOnComplete(System.out::println)
                    .onErrorResume(e -> Flux.empty())
                    .blockLast();
        } catch (Exception e) {
            log.error("An error occurred while processing the chat. Please try again.", e);
        }
    }

    /**
     * Method to create a new ChatClient with the given options.
     */
    private ChatClient createClient(ChatClient.Builder builder, ChatOptions options) {
        // Clone the builder to prevent defaultOptions from being set on the original builder
        var clonedBuilder = builder.build().mutate();

        return clonedBuilder.defaultOptions(options).build();
    }
    // </editor-fold>


    /**
     * Description: Controls the randomness of the model's output. Higher values make the output more diverse and creative, while lower values make it more focused and deterministic.
     * Range: Values typically range from 0.0 (highly deterministic) to 1.0 (highly random).
     * Usage: Increase temperature for creative tasks, like generating story ideas or brainstorming, and lower it for tasks needing accuracy and consistency, like fact-based queries.
     * Example: temperature = 0.2 results in a precise, repeatable answer, while temperature = 0.8 produces more varied, imaginative answers.
     */
    private void temperatureExample(ChatClient.Builder builder, OpenAiChatOptions options) {
        log.info("=== Temperature Setting ===");

        try {
            String prompt = "Tell me a short poem about pizza?";

            // Low temperature (more deterministic)
            org.springframework.ai.openai.OpenAiChatOptions lowTempOptions = options.copy();
            lowTempOptions.setTemperature(0.0);

            ChatClient clientLowTemp = createClient(builder, lowTempOptions);

            log.info("Low Temperature Response:");
            executePrompt(clientLowTemp, prompt);


            // High temperature (more creative)
            OpenAiChatOptions highTempOptions = options.copy();
            highTempOptions.setTemperature(1.5); // Try 2.0 ;)

            ChatClient clientHighTemp = createClient(builder, highTempOptions);

            log.info("High Temperature Response:");
            executePrompt(clientHighTemp, prompt);

        } catch (Exception e) {
            log.error("Error in temperature example", e);
        }
    }

    /**
     * maxTokens
     * Description: This setting controls the maximum number of tokens (words, subwords, or characters depending on the model) that the model will generate in a single response.
     * Usage: Set maxTokens to limit response length for tasks that need concise answers (e.g., summaries, short answers) or increase it to allow for more detailed, extensive responses.
     * Example: maxTokens = 50 might yield a brief answer, while maxTokens = 200 would generate a longer, more comprehensive response.
     */
    private void maxTokensExample(ChatClient.Builder builder, OpenAiChatOptions options) {
        log.info("=== Max Tokens Setting ===");

        try {
            String prompt = "Summarize the theory of relativity in simple terms.";

            // Set max tokens to 10
            OpenAiChatOptions maxTokensOptions = options.copy();
            maxTokensOptions.setMaxTokens(10);

            ChatClient clientMaxTokens = createClient(builder, maxTokensOptions);

            log.info("Response with Max Tokens 10:");
            executePrompt(clientMaxTokens, prompt);

        } catch (Exception e) {
            log.error("Error in max tokens example", e);
        }
    }

    /**
     * n (Number of Completions)
     * Description: Specifies how many different response completions the model should generate per request.
     * Usage: Set n to a higher number to generate multiple answers in a single request, useful for brainstorming or getting varied responses to the same prompt.
     * Example: n = 3 generates three different answers, allowing you to choose the best one for a given context.
     */
    private void numberOfCompletionsExample(ChatClient.Builder builder, OpenAiChatOptions options) {
        log.info("=== Number of Completions (n) Setting ===");

        try {
            String prompt = "Suggest a unique name for a new eco-friendly coffee cup.";

            // Set number of completions to 5
            OpenAiChatOptions multipleCompletionsOptions = options.copy();
            multipleCompletionsOptions.setN(2); // TODO, seems to run simultaneously on multiple LLMs.

            // TODO, it's recommended to connect an inMemoryChatMemory or raise temperature, otherwise it will generate identical responses
            ChatClient clientMultipleCompletions = createClient(builder, multipleCompletionsOptions);

            log.info("Response with Multiple Completions:");
            executePrompt(clientMultipleCompletions, prompt);

        } catch (Exception e) {
            log.error("Error in number of completions example", e);
        }
    }

    /**
     * stopSequences / stop
     * Description: A list of tokens or sequences that indicate where the model should stop generating further text. Useful for controlling the end of responses and preventing the model from going off-topic.
     * Usage: Define specific phrases (like a newline character \n or specific word) that signal the model to stop, controlling the length and relevance of responses.
     * Example: Setting stop = ["\n"] will make the model stop generating after the first line, ideal for single-line responses.
     */
    private void stopSequenceExample(ChatClient.Builder builder, OpenAiChatOptions options) {
        log.info("=== Stop Sequence Setting ===");

        try {
            String prompt = "List some popular programming languages and their creators.";

            // Set stop sequences to end after each line
            OpenAiChatOptions stopSequenceOptions = options.copy();
            stopSequenceOptions.setStopSequences(List.of("\n"));

            ChatClient clientStopSequence = createClient(builder, stopSequenceOptions);

            log.info("Response with Stop Sequence:");
            executePrompt(clientStopSequence, prompt);

        } catch (Exception e) {
            log.error("Error in stop sequence example", e);
        }
    }

    /**
     * presencePenalty
     * Description: A positive value that discourages the model from repeating information by penalizing words that have already been mentioned in the response. Higher values encourage introducing new content.
     * Range: Values usually range from 0.0 (no penalty) to 2.0 (high penalty).
     * Usage: Useful in creative or brainstorming tasks where you want diverse output, and repetition is undesirable.
     * Example: presencePenalty = 1.0 makes the model more likely to bring in new ideas and topics, while presencePenalty = 0.0 allows it to repeat concepts if relevant.
     */
    private void presencePenaltyExample(ChatClient.Builder builder, OpenAiChatOptions options) {
        log.info("=== Presence Penalty Setting ===");

        try {
            String prompt = "Write a short poem about the ocean.";

            // Set presence penalty to 1.0 to encourage diversity
            OpenAiChatOptions presencePenaltyOptions = options.copy();
            presencePenaltyOptions.setPresencePenalty(2.0);

            ChatClient clientPresencePenalty = createClient(builder, presencePenaltyOptions);

            log.info("Poem with Presence Penalty:");
            executePrompt(clientPresencePenalty, prompt);

        } catch (Exception e) {
            log.error("Error in presence penalty example", e);
        }
    }

    /**
     * frequencyPenalty
     * Description: A positive value that discourages the model from generating repeated phrases or words by penalizing tokens based on their frequency in the text so far.
     * Range: Values typically range from 0.0 (no penalty) to 2.0 (high penalty).
     * Usage: Reduces redundancy in the output, ideal for cases where you want a concise, non-repetitive answer.
     * Example: frequencyPenalty = 1.0 limits repetitive phrases, useful in tasks like summarization or response generation where verbosity should be minimized.
     */
    private void frequencyPenaltyExample(ChatClient.Builder builder, OpenAiChatOptions options) {
        log.info("=== Frequency Penalty Setting ===");

        try {
            String prompt = "Explain briefly the importance of regular exercise.";

            // Set frequency penalty to 1.0 to reduce repetition
            OpenAiChatOptions frequencyPenaltyOptions = options.copy();
            frequencyPenaltyOptions.setFrequencyPenalty(2.0);

            ChatClient clientFrequencyPenalty = createClient(builder, frequencyPenaltyOptions);

            log.info("Response with Frequency Penalty:");
            executePrompt(clientFrequencyPenalty, prompt);

        } catch (Exception e) {
            log.error("Error in frequency penalty example", e);
        }
    }

    /**
     * logitBias
     * Description: A map that biases the likelihood of specific tokens appearing in the output. Each token can be associated with a bias score between -100 and 100, where negative values reduce the token's likelihood and positive values increase it.
     * Usage: Useful for steering the model toward or away from specific words or topics. For example, if you want the model to avoid using "negative" words, you can assign a negative bias to those tokens.
     * Example: logitBias = { "positive_token_id": 50, "negative_token_id": -50 } would make "positive" more likely to appear in responses and "negative" less likely.
     */
    private void logitBiasExample(ChatClient.Builder builder, OpenAiChatOptions options) {
        log.info("=== Logit Bias Setting ===");

        try {
            String prompt = "Describe the colors you might see in a sunset.";

            // Set logit bias to prefer certain colors
            OpenAiChatOptions logitBiasOptions = options.copy();
            Map<String, Integer> logitBiasMap = Map.of(
                    "1291", -100, "7805", -100, // red& Red
                    "65681", -100, "64615", -100, // orange & Orange
                    "60139", -100, "67777", -100, // yellow & Yellow
                    "18789", 10, "15957", 2); // blue & Blue
            logitBiasOptions.setLogitBias(logitBiasMap);

            ChatClient clientLogitBias = createClient(builder, logitBiasOptions);

            log.info("Response with Logit Bias:");
            executePrompt(clientLogitBias, prompt);

        } catch (Exception e) {
            log.error("Error in logit bias example", e);
        }
    }

    /**
     * responseFormat
     * Description: Specifies the format of the response output. Options include plain text (AzureOpenAiResponseFormat.TEXT) or JSON (AzureOpenAiResponseFormat.JSON).
     * Usage: Choose JSON format if the response needs to be parsed programmatically, or TEXT if it should be displayed directly to users.
     * Example: responseFormat = AzureOpenAiResponseFormat.JSON ensures the output is structured as JSON, useful for applications needing structured data.
     */
//    private void responseFormatExample(ChatClient.Builder builder, OpenAiChatOptions options) {
//        log.info("=== Response Format Setting ===");
//
//        try {
//            String promptPlain = "Provide information about Pikachu, I expect name, type, weaknesses and some notes.";
//
//            // Plain text format
//            OpenAiChatOptions plainTextOptions = options.copy();
//            plainTextOptions.setResponseFormat(OpenAiResponseFormat.TEXT);
//
//            ChatClient clientWithPlainTextFormat = createClient(builder, plainTextOptions);
//
//            log.info("Plain Text Response:");
//            executePrompt(clientWithPlainTextFormat, promptPlain);
//
//            // JSON format, YOU NEED 'json' IN THE PROMPT or else an exception is thrown
//            String promptJson = "Provide information about Pikachu, I expect name, type, weaknesses and some notes. As a json object.";
//
//            OpenAiChatOptions jsonOptions = options.copy();
//            jsonOptions.setResponseFormat(OpenAiResponseFormat.JSON);
//
//            ChatClient clientWithJSONFormat = createClient(builder, jsonOptions);
//
//            log.info("JSON Response:");
//            executePrompt(clientWithJSONFormat, promptJson);
//
//        } catch (Exception e) {
//            log.error("Error in response format example", e);
//        }
//    }

}
