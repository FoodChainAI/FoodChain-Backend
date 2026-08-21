package com.example.foodchain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FoodchainApplication {

	public static void main(String[] args) {
		loadDotEnvFiles();
		SpringApplication.run(FoodchainApplication.class, args);
	}

	/**
	 * Loads {@code .env} then {@code .env.local} from the working directory and
	 * exposes each KEY=VALUE as a JVM system property (unless already set as a
	 * real environment variable or system property). This lets local runs pick
	 * up secrets like {@code RESEND_API_KEY} without exporting them by hand.
	 * Spring Boot does not read dotenv files on its own.
	 */
	private static void loadDotEnvFiles() {
		for (String fileName : new String[]{".env", ".env.local"}) {
			Path path = Path.of(fileName);
			if (!Files.isReadable(path)) {
				continue;
			}
			try {
				for (String rawLine : Files.readAllLines(path)) {
					String line = rawLine.trim();
					if (line.isEmpty() || line.startsWith("#")) {
						continue;
					}
					if (line.startsWith("export ")) {
						line = line.substring("export ".length()).trim();
					}
					int eq = line.indexOf('=');
					if (eq < 1) {
						continue;
					}
					String key = line.substring(0, eq).trim();
					String value = line.substring(eq + 1).trim();
					if (value.length() >= 2
							&& ((value.startsWith("\"") && value.endsWith("\""))
							|| (value.startsWith("'") && value.endsWith("'")))) {
						value = value.substring(1, value.length() - 1);
					}
					if (System.getProperty(key) == null && System.getenv(key) == null) {
						System.setProperty(key, value);
					}
				}
			} catch (IOException ex) {
				System.err.println("[dotenv] Could not read " + fileName + ": " + ex.getMessage());
			}
		}
	}

}
