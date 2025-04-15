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
