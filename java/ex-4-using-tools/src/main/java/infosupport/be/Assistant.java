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
        this.chatClient = modelBuilder
                .defaultSystem("""
                        // TODO tell the model to use the tools you provide
                        """)
                .defaultAdvisors(
                        // new LoggingAdvisor(),
                        new PromptChatMemoryAdvisor(chatMemory)
                )
                .build();
    }

    public Flux<String> chat(String chatId, String userMessageContent) {
        try {
            return this.chatClient.prompt()
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
}