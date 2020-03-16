package samt.smajilbasic.deduplicator;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import samt.smajilbasic.deduplicator.timer.ScheduleChecker;
import samt.smajilbasic.deduplicator.entity.AuthenticationDetails;
import samt.smajilbasic.deduplicator.logger.MyLogger;
import samt.smajilbasic.deduplicator.repository.AuthenticationDetailsRepository;

@SpringBootApplication
public class DeduplicatorApplication {

	/**
     * L'attributo context contiene il contesto dell'applicazione. Viene usato per
     * trovare l'utente attualmente collegato.
     */
    @Autowired
    private ApplicationContext context;

	@Autowired
	AuthenticationDetailsRepository adr;


	public static void main(String[] args) {
		SpringApplication.run(DeduplicatorApplication.class, args);
	}

	@PostConstruct
	void started() throws NoSuchAlgorithmException {
		TimeZone.setDefault(TimeZone.getDefault());
		if (!adr.existsById("admin"))
			adr.save(new AuthenticationDetails("admin", "administrator"));
		if (!adr.existsById("scheduler"))
			adr.save(new AuthenticationDetails("scheduler", "scheduler"));
		try{
			MyLogger.setup();
		}catch(IOException ioe){
			System.out.println("Unable to setup logger");
			ioe.printStackTrace();
		}
	}

	@EventListener(ApplicationReadyEvent.class)
	public void checkSchedulerAfterStartup() {
		ScheduleChecker checker = (ScheduleChecker) context.getBean("scheduleChecker");
		checker.start();
	}

}
