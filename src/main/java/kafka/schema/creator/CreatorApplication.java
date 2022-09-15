package kafka.schema.creator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CreatorApplication {
	public static void mail(String ...args) {
		SpringApplication.run(CreatorApplication.class, args);
	}
}
