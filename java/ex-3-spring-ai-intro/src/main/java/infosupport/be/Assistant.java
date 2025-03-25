package infosupport.be;

import infosupport.be.config.AIConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
@Slf4j
public class Assistant {

    private final ChatClient chatClient;

    @Autowired
    public Assistant(ChatClient.Builder modelBuilder, ChatMemory chatMemory, @Qualifier("chatConfig") AIConfig aiConfig) {
        // We use the injected ChatClient.Builder from Spring AI to build the ChatClient
        this.chatClient = modelBuilder
                .defaultSystem(aiConfig.SYSTEM_PROMPT)
                .defaultAdvisors(
                        // Order does matter and is set in the Advisor
                        // LoggingAdvisor to view additional information
                         new LoggingAdvisor(),
                        // PromptChatMemoryAdvisor to remember chat history, using the bean from ChatMemoryConfig
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
                            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 5)
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