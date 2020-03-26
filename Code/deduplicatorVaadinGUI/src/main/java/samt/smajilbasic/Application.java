package samt.smajilbasic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import samt.smajilbasic.logger.MyLogger;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
@ComponentScan
public class Application extends SpringBootServletInitializer {

    @Autowired
    ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void startup(){
        try{
            MyLogger logger = (MyLogger) context.getBean("myLogger");
            logger.setup();
        }catch(IOException ioe){
            System.out.println("Unable to setup logger");
            ioe.printStackTrace();
        }
    }

}
