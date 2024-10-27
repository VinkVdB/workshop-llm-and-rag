package infosupport.be;

import infosupport.be.config.LoggingAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
@Slf4j
public class Assistant {

    private final ChatClient chatClient;

    @Autowired
    public Assistant(ChatClient.Builder modelBuilder, ChatMemory chatMemory) {
        // We use the injected ChatClient.Builder from Spring AI to build the ChatClient
        this.chatClient = modelBuilder
                .defaultSystem("""
                        Greet the user and ask for their name.
                        If they have no password stored using getPassword, ask them to provide one and use upsertPassword to save it.
                        If they provide a password, verify it with verifyPassword; if correct, tell a friendly joke. if incorrect, ask them to try again or admit they forgot it.
                        Always be polite, clear, and keep interactions secure and private.
                        """)
                .defaultAdvisors(
                        // new LoggingAdvisor(),
                        new PromptChatMemoryAdvisor(chatMemory)
                )
                .build();
    }

    public Flux<String> chat(String chatId, String userMessageContent) {
        // Attempt to process the chat request safely
        try {
            return this.chatClient.prompt()
                    .user(userMessageContent)
                    .advisors(a -> a
                            // The PromptChatMemoryAdvisor saves the chat history per chatId, default is "default"
                            .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                            // The number of chat messages to retrieve from the chat history, default is "100"
                            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
                    )
                    .stream()
                    .content();
        } catch (Exception e) {
            // Log and handle any exceptions that occur during chat processing
            log.error("An error occurred while processing the chat. Please try again.", e);
            return Flux.empty();
        }
    }
}