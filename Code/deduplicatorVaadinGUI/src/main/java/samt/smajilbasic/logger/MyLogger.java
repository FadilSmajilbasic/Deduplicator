package samt.smajilbasic.logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import samt.smajilbasic.configuration.ConfigProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MyLogger
 */
@Component
public class MyLogger {

    @Autowired
    private ConfigProperties props;

    public void setup() throws IOException {
        String logPath = props.getLogPath();
        System.out.println("logPAth: " + logPath);
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        Path p = Paths.get(logPath);
        File logFolder = new File(p.toAbsolutePath().toString());
        if(!logFolder.exists()){
                if(Files.isWritable(Paths.get(logFolder.getParent()))){
                    if(!logFolder.mkdir()){
                        logPath = "";
                    }else{
                        Logger.getGlobal().log(Level.SEVERE,"Unable to make log directory");
                        Logger.getGlobal().log(Level.INFO,"Writing logs to current working directory");
                    }
                }else {
                    Logger.getGlobal().log(Level.SEVERE,"Unable to write to parent directory");
                    Logger.getGlobal().log(Level.INFO,"Writing logs to current working directory");
                }
        }
        FileHandler fileHandler = new FileHandler(logPath + "log." + System.currentTimeMillis() + ".txt");
        fileHandler.setFormatter(new LogFormatter());
        logger.addHandler(fileHandler);
    }
}