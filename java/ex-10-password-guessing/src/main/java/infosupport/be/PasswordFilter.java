package infosupport.be;

import org.springframework.stereotype.Component;

@Component
public class PasswordFilter {

    private final String password;

    public PasswordFilter(String password) {
        this.password = password;
    }

    public String filterResponse(String response) {
        if (response.contains(password)) {
            return response.replace(password, "****");
        }
        return response;
    }

    public boolean isPasswordInResponse(String response) {
        return response.contains(password);
    }
}
