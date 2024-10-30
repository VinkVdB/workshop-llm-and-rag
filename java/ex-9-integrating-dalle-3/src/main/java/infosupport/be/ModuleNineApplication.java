package infosupport.be;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class ModuleNineApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleNineApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(
            ImageModel imageClient) {
        return args -> {
            var prompt = "A painting of a beautiful sunset over a calm lake.";

//            log.info("Response: {}", response.getResult().getOutput().getUrl());
        };
    }
}