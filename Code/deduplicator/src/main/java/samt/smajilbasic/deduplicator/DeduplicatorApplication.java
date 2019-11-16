package samt.smajilbasic.deduplicator;

import java.security.NoSuchAlgorithmException;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import samt.smajilbasic.deduplicator.timer.ScheduleChecker;
import samt.smajilbasic.deduplicator.entity.AuthenticationDetails;
import samt.smajilbasic.deduplicator.repository.AuthenticationDetailsRepository;

@SpringBootApplication
public class DeduplicatorApplication {

	@Autowired
	AuthenticationDetailsRepository adr;

	@Autowired
	ScheduleChecker checker;

	public static void main(String[] args) {
		SpringApplication.run(DeduplicatorApplication.class, args);
	}

	@PostConstruct
	void started() throws NoSuchAlgorithmException {
		TimeZone.setDefault(TimeZone.getDefault());
		if (!adr.existsById("admin"))
			adr.save(new AuthenticationDetails("admin", "admin"));
		if (!adr.existsById("scheduler"))
			adr.save(new AuthenticationDetails("scheduler", "scheduler"));
	}

	@EventListener(ApplicationReadyEvent.class)
	public void checkSchedulerAfterStartup() {
		checker.check();
	}

}
