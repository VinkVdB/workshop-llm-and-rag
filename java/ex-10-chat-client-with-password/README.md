# Module 10: Chat Client with Password

## Objective
Create a chat client that requires a password to access. The user will need to guess the password, and if they get it right, the runner ends.

## Contents
- Implementing a chat client with password functionality.
- Adding a method to check the user's guess against the stored password.
- Making the process harder to guess the password to prevent prompt injection.

## Instructions

1. **Create the Directory Structure:**
   - Create a new directory `java/ex-10-chat-client-with-password`.
   - Add a `README.md` file in the new directory with instructions for the module.
   - Create a new Java class `ModuleTenApplication` in `java/ex-10-chat-client-with-password/src/main/java/infosupport/be/ModuleTenApplication.java`.

2. **Implement the Chat Client with Password Functionality:**
   - Implement the chat client with password functionality in the new Java class.
   - Add a method to check the user's guess against the stored password.

3. **Make Prompt Injection More Difficult:**
   - To make it harder for the password to be given, consider the following strategies:
     - Use a more complex password that is not easily guessable.
     - Implement rate limiting to prevent brute force attacks.
     - Add a delay between failed attempts to slow down guessing.
     - Log and monitor failed attempts to detect suspicious activity.
     - Use multi-factor authentication (MFA) for an additional layer of security.
     - Regularly rotate passwords and update the stored password accordingly.

## Example Code

Here is an example of how you might implement the chat client with password functionality:

```java
package infosupport.be;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class ModuleTenApplication {

    private static final String STORED_PASSWORD = "MATTENTAART";

    public static void main(String[] args) {
        SpringApplication.run(ModuleTenApplication.class, args).close();
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to the Chat Client with Password!");

            while (true) {
                System.out.print("Enter the password: ");
                String userInput = scanner.nextLine();

                if (checkPassword(userInput)) {
                    System.out.println("Password is correct. Access granted!");
                    break;
                } else {
                    System.out.println("Password is incorrect. Try again.");
                }
            }
        };
    }

    private boolean checkPassword(String userInput) {
        return STORED_PASSWORD.equals(userInput);
    }
}
```

## Notes
- Ensure that the password is stored securely and not hardcoded in the code.
- Consider using environment variables or a secure vault to store the password.
- Regularly review and update the security measures to stay ahead of potential threats.
