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

    // TODO: introduce tools to upsertPassword and verifyPassword

    // TODO: Extra, add some more tools that you deem worth having
}



