package dk.goombah.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PreDestroy;
import java.io.File;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {

	//A simple variable that tells us whether the program is in production environment or development environment.
	private static boolean testMode;
	public static boolean running = true;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	public static String getBackendUrl() {
		return testMode ? "http://localhost:7443/" : "https://backend.goombah.dk/";
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/content/**").allowedOrigins("*");
			}
		};
	}

	public static boolean inDevelopmentMode()
	{
		return testMode;
	}

	public static boolean inProductionMode() {
		return !testMode;
	}

	@Override
	public void run(String... args) throws Exception {
		testMode = new File("testMode.file").exists();
	}

	@PreDestroy
	public void onDestroy() throws Exception {
		running = false;
	}
}
