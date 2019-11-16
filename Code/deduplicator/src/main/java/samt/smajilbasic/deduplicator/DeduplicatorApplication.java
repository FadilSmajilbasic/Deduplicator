package samt.smajilbasic.deduplicator;

import java.security.NoSuchAlgorithmException;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import samt.smajilbasic.deduplicator.Timer.ScheduleChecker;
import samt.smajilbasic.deduplicator.entity.AuthenticationDetails;
import samt.smajilbasic.deduplicator.repository.AuthenticationDetailsRepository;

// @ComponentScan(basePackages = "samt.smajilbasic.deduplicator.config")
@SpringBootApplication
public class DeduplicatorApplication {

	@Autowired
	AuthenticationDetailsRepository adr;

	public static void main(String[] args) {
		SpringApplication.run(DeduplicatorApplication.class, args);
	}

	@PostConstruct
	void started() throws NoSuchAlgorithmException {
		TimeZone.setDefault(TimeZone.getDefault());
		adr.save(new AuthenticationDetails("admin", "admin"));
		adr.save(new AuthenticationDetails("scheduler", "scheduler"));

		// ScheduleChecker checker = new ScheduleChecker();
		// checker.start();
	}

}
