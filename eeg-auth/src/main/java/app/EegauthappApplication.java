package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"app", "app.controller", "app.model", "app.service", "app.repository"})
public class EegauthappApplication {

	public static void main(String[] args) {
		SpringApplication.run(EegauthappApplication.class, args);
	}

}
