package infosupport.be;

import infosupport.be.config.LoggingAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Assistant(ChatClient.Builder modelBuilder, VectorStore vectorStore, ChatMemory chatMemory) {

        this.chatClient = modelBuilder
                .defaultSystem("""
                        You are a Pokédex, you can help users find information about Pokemon.
                        You can provide information about a Pokemon's type and evolutions.
                        
                        Today's date is {current_date}.
                        
                        Always be polite, clear, and keep interactions secure and private.
                        Use emojis to make the conversation more engaging and for the different types of Pokemon.
                        Use parallel function calling if useful or required.
                        """)
                .defaultAdvisors(
                        new LoggingAdvisor(),
                        new PromptChatMemoryAdvisor(chatMemory),
                        // QuestionAnswerAdvisor to retrieve information from the vector store that is relevant to the user's request
                        new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults().withTopK(3))
                )
                .build();
    }

    public Flux<String> chat(String chatId, String userMessageContent) {
        try {
            return this.chatClient.prompt()
                    .system(s -> s.param("current_date", LocalDate.now().toString()))
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