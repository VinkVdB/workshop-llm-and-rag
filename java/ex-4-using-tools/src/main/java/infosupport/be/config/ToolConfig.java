package infosupport.be.config;

import infosupport.be.services.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class ToolConfig {

    private final PasswordService passwordService;

    @Tool(description = "Get the password for a given username.")
    String getPassword(String username) {
        try {
            String password = passwordService.getPassword(username);
            return Objects.requireNonNullElse(password, "User not found.");
        } catch (Exception e) {
            log.warn("GetPassword error: {}", e.getMessage());
            return "An error occurred while retrieving the password.";
        }

    }

    @Tool(description = "Upsert (add or update) the password for a given username.")
    String upsertPassword(String username, String password) {
        try {
            passwordService.upsertPassword(username, password);
            return "Password upserted successfully for user: " + username;
        } catch (Exception e) {
            log.warn("UpsertPassword error: {}", e.getMessage());
            return "An error occurred while upserting the password.";
        }
    }

    @Tool(description = "Verify if the provided password matches the stored password for the username.")
    String verifyPassword(String username, String password) {
        try {
            boolean isValid = passwordService.verifyPassword(username, password);
            return isValid ? "Password is correct." : "Password is incorrect.";
        } catch (Exception e) {
            log.warn("VerifyPassword error: {}", e.getMessage());
            return "An error occurred while verifying the password.";
        }
    }
}



