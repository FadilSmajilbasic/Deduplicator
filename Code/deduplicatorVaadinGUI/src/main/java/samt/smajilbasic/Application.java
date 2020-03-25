package samt.smajilbasic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import samt.smajilbasic.logger.MyLogger;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void startup(){
        try{
            MyLogger.setup();
        }catch(IOException ioe){
            System.out.println("Unable to setup logger");
            ioe.printStackTrace();
        }
    }

}
