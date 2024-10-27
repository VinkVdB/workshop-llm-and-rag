package infosupport.be.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PasswordService {

    private final ConcurrentHashMap<String, String> userPasswords = new ConcurrentHashMap<>() {{
        put("John Doe", "MATTENTAART");
    }};

    /**
     * Retrieves the password for a given username.
     *
     * @param username the username whose password is to be retrieved
     * @return the password if the user exists, null otherwise
     */
    public String getPassword(String username) {
        var password = userPasswords.get(username);

        log.info("Retrieved password '{}' for user: {}", password, username);

        return password;
    }

    /**
     * Upserts (adds or updates) the password for a given username.
     *
     * @param username the username whose password is to be upserted
     * @param password the plaintext password to be stored
     */
    public void upsertPassword(String username, String password) {
        var result = userPasswords.put(username, password);

        if(result != null){
            log.info("Password for user: {} updated from '{}' to '{}'", username, result, password);
        } else {
            log.info("Password for user: {} added with value '{}'", username, password);
        }
    }

    /**
     * Verifies if the provided password matches the stored password for the username.
     *
     * @param username the username whose password is to be verified
     * @param password the plaintext password to verify
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String username, String password) {
        String storedPassword = userPasswords.get(username);

        if (storedPassword == null) {
            log.info("User: {} not found", username);
            return false;
        }

        var result = password.equals(storedPassword);

        log.info("Password '{}' for user: {} is {}", password, username, result ? "valid" : "invalid");

        return result;
    }
}

