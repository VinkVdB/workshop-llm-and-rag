package infosupport.be;

import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;

import java.lang.reflect.Field;

public class ChatOptionsUtil {

    public static ChatOptions cloneAndModifyOptions(ChatClient.Builder builder, ChatOptionsModifier modifier) {
        try {
            // Access the protected defaultRequest field from the builder
            Field defaultRequestField = ChatClient.Builder.class.getDeclaredField("defaultRequest");
            defaultRequestField.setAccessible(true);

            // Get the current default ChatOptions
            ChatOptions currentOptions = (ChatOptions) defaultRequestField.get(builder);

            // Clone and modify options
            ChatOptions modifiedOptions = modifier.modify(new AzureOpenAiChatOptions());
            return modifiedOptions;

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access default ChatOptions", e);
        }
    }

    // Functional interface to apply modifications to ChatOptions
    @FunctionalInterface
    public interface ChatOptionsModifier {
        ChatOptions modify(ChatOptions options);
    }
}
