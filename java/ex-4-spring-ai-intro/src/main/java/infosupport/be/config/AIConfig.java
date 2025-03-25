package infosupport.be.config;

import java.nio.file.Files;
import java.nio.file.Path;

public class AIConfig {

    public final String SYSTEM_PROMPT;

    public AIConfig(Path promptPath) {
        try{
            SYSTEM_PROMPT = Files.readString(promptPath);
        } catch (Exception e) {
            throw new RuntimeException("Error reading system prompt file", e);
        }
    }
}
