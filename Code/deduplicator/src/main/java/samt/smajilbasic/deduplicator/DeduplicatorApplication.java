package samt.smajilbasic.deduplicator;

import java.security.NoSuchAlgorithmException;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import samt.smajilbasic.deduplicator.Timer.ScheduleChecker;
import samt.smajilbasic.deduplicator.entity.AuthenticationDetails;
import samt.smajilbasic.deduplicator.repository.AuthenticationDetailsRepository;

// (scanBasePackages = { "samt.smajilbasic.deduplicator.controller", "samt.smajilbasic.deduplicator.repository","samt.smajilbasic.deduplicator.entity"})
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
		if(!adr.existsById("admin"))
			adr.save(new AuthenticationDetails("admin","admin"));
		else
			adr.save(new AuthenticationDetails("scheduler",null));

		ScheduleChecker checker = new ScheduleChecker();
		checker.start();
	}

}
