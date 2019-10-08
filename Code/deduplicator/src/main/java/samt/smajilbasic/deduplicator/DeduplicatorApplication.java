package samt.smajilbasic.deduplicator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// (scanBasePackages = { "samt.smajilbasic.deduplicator.controller", "samt.smajilbasic.deduplicator.repository","samt.smajilbasic.deduplicator.entity"})
@SpringBootApplication
public class DeduplicatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeduplicatorApplication.class, args);
	}

}
