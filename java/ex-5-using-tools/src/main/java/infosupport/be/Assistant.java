package infosupport.be;

import infosupport.be.config.AIConfig;
import infosupport.be.config.ToolConfig;
import infosupport.be.services.PasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
@Slf4j
public class Assistant {

    private final ChatClient chatClient;
    private final PasswordFilter passwordFilter;

    @Autowired
    public Assistant(
            ChatClient.Builder modelBuilder,
            ChatMemory chatMemory,
            @Qualifier("chatConfig") AIConfig aiConfig,
            PasswordService passwordService,
            PasswordFilter passwordFilter){
        this.chatClient = modelBuilder
                .defaultSystem(aiConfig.SYSTEM_PROMPT)
                .defaultAdvisors(
                        // new LoggingAdvisor(),
                        new PromptChatMemoryAdvisor(chatMemory)
                )
                // Add tools, see config/ToolConfig.java
                .build();
        this.passwordFilter = passwordFilter;
    }

    public Flux<String> chat(String chatId, String userMessageContent) {
        try {
            return this.chatClient.prompt()
                    .user(userMessageContent)
                    .advisors(a -> a
                            .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
                    )
                    // Add tools here, perhaps with some conditions
                    .stream()
                    .content()
                    .map(passwordFilter::filterResponse);
        } catch (Exception e) {
            log.error("An error occurred while processing the chat. Please try again.", e);
            return Flux.empty();
        }
    }
}
