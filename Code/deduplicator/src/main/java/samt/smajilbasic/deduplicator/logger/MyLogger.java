package samt.smajilbasic.deduplicator.logger;

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
public class MyLogger {

    private static FileHandler fileHandler;
    private static String logFilePath = "log/";
    static public void setup() throws IOException {
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        Path logPath = Paths.get(logFilePath);
        File logFolder = new File(logPath.toAbsolutePath().toString());
        if(!logFolder.exists()){
                if(Files.isWritable(Paths.get(logFolder.getParent()))){
                    if(!logFolder.mkdir()){
                        logFilePath = "";
                    }else{
                        Logger.getGlobal().log(Level.SEVERE,"Unable to make log directory");
                        Logger.getGlobal().log(Level.INFO,"Writing logs to current working directory");
                    }
                }else {
                    Logger.getGlobal().log(Level.SEVERE,"Unable to write to parent directory");
                    Logger.getGlobal().log(Level.INFO,"Writing logs to current working directory");
                }
        }
        fileHandler = new FileHandler(logFilePath +"log."+System.currentTimeMillis()+".txt");
        fileHandler.setFormatter(new LogFormatter());
        logger.addHandler(fileHandler);
    }
}