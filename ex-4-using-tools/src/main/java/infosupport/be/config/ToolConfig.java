package infosupport.be.config;

import infosupport.be.services.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.Objects;
import java.util.function.Function;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ToolConfig {

    private final PasswordService passwordService;

    // Request and Response records
    public record GetPasswordRequest(String username) {}
    public record UpsertPasswordRequest(String username, String password) {}
    public record VerifyPasswordRequest(String username, String password) {}

    @Bean
    @Description("Get the password for a given username.")
    public Function<GetPasswordRequest, String> getPassword() {
        return request -> {
            try {
                String password = passwordService.getPassword(request.username());
                return Objects.requireNonNullElse(password, "User not found.");
            } catch (Exception e) {
                log.warn("GetPassword error: {}", e.getMessage());
                return "An error occurred while retrieving the password.";
            }
        };
    }

    @Bean
    @Description("Upsert (add or update) the password for a given username.")
    public Function<UpsertPasswordRequest, String> upsertPassword() {
        return request -> {
            try {
                passwordService.upsertPassword(request.username(), request.password());
                return "Password upserted successfully for user: " + request.username();
            } catch (Exception e) {
                log.warn("UpsertPassword error: {}", e.getMessage());
                return "An error occurred while upserting the password.";
            }
        };
    }

    @Bean
    @Description("Verify if the provided password matches the stored password for the username.")
    public Function<VerifyPasswordRequest, String> verifyPassword() {
        return request -> {
            try {
                boolean isValid = passwordService.verifyPassword(request.username(), request.password());
                if (isValid) {
                    return "Password is correct.";
                } else {
                    return "Password is incorrect.";
                }
            } catch (Exception e) {
                log.warn("VerifyPassword error: {}", e.getMessage());
                return "An error occurred while verifying the password.";
            }
        };
    }
}



