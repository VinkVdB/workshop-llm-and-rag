package infosupport.be;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
@Slf4j
public class Assistant {

    // Define max characters for jokes, this is to mimic the behaviour from context limiting
    private static final int CHARACTER_LIMIT_FOR_JOKES = 500;

    private final ChatClient chatClient;

    @Autowired
    public Assistant(ChatClient.Builder modelBuilder, ChatMemory chatMemory) {
        // We use the injected ChatClient.Builder from Spring AI to build the ChatClient
        this.chatClient = modelBuilder
                .defaultSystem("""
                        === JOKES BEGIN ===
                        {jokes}
                        === JOKES END ===

                        // TODO advise LLM about which user it's connected to, and the current date
                        """)
                .defaultAdvisors(
                        new PromptChatMemoryAdvisor(chatMemory)
                )
                .build();
    }

    public Flux<String> chat(String chatId, String userMessageContent, List<String> jokes) {
        // Truncate jokes to max characters
        String truncatedJokes = getTruncatedJokes(jokes);

        try {
            return this.chatClient.prompt()
                    .system(s -> {
                    })
                    .user(userMessageContent)
                    .advisors(a -> a
                            .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
                    )
                    .stream()
                    .content();
        } catch (Exception e) {
            log.error("An error occurred while processing the chat. Please try again.", e);
            return Flux.empty();
        }
    }

    private static String getTruncatedJokes(List<String> jokes) {
        // Truncate jokes to max characters (or use https://camel.apache.org/components/4.8.x/others/langchain4j-tokenizer.html instead)
        String joinedJokes = String.join("\n", jokes);
        String truncatedJokes = String.join("\n", joinedJokes)
                .substring(Math.max(0, joinedJokes.length() - CHARACTER_LIMIT_FOR_JOKES));

        log.info("Jokes truncated to: {}", truncatedJokes);
        return truncatedJokes;
    }
}