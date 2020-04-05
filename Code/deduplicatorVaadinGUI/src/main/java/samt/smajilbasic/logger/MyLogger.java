package samt.smajilbasic.logger;

import org.springframework.stereotype.Component;
import samt.smajilbasic.properties.Settings;

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

    private Settings settings = new Settings();

    public void setup() throws IOException {
        String logPath = settings.getLogPath();
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        Path p = Paths.get(logPath);
        File logFolder = new File(p.toAbsolutePath().toString());
        if (!logFolder.exists()) {
            if (logFolder.mkdirs()) {
                Logger.getGlobal().log(Level.INFO, "Log directory made");
            } else {
                Logger.getGlobal().log(Level.SEVERE, "Unable to make log directory");
                Logger.getGlobal().log(Level.INFO, "Writing logs to current working directory");
                logPath = "";
            }
        }
        FileHandler fileHandler = new FileHandler(logPath + "log." + System.currentTimeMillis() + ".txt");
        fileHandler.setFormatter(new LogFormatter());
        logger.addHandler(fileHandler);
    }
}