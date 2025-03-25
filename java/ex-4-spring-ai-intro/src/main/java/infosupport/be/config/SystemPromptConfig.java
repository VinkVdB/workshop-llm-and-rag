package infosupport.be.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class SystemPromptConfig {
    @Bean
    public AIConfig chatConfig(@Value("classpath:system-prompt-chat.txt") Path promptPath) {
        return new AIConfig(promptPath);
    }

    @Bean
    public AIConfig movieConfig(@Value("classpath:system-prompt-movie.txt") Path promptPath) {
        return new AIConfig(promptPath);
    }
}
