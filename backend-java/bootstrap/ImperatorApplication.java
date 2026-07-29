package imperator.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "imperator")
public class ImperatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImperatorApplication.class, args);
    }
}
