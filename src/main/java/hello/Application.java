package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Application {
    public static void main(String... args) throws IOException {
        SpringApplication application = new SpringApplication(Application.class);
        application.run(args);
    }
}
