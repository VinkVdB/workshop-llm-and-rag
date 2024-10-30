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

    // TODO upsert password

    // TODO verify password
}



