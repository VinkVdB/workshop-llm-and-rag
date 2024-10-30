package infosupport.be;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
public class ChatMemoryConfig {

    /**
     * This method creates a new instance of the InMemoryChatMemory class. It does not persist the chat history.
     */
    @Bean
    @ConditionalOnProperty(name = "chat.memory.type", havingValue = "inMemory", matchIfMissing = true)
    public ChatMemory inMemoryChatMemory() {
        return new InMemoryChatMemory();
    }

    /**
     * Alternatively you can define your own chat memory implementation. Which could use a db.
     * To demonstrate this, I've used ChatGPT to generate a single-file based chat memory.
     */
    // TODO implement the FileChatMemory bean
}
