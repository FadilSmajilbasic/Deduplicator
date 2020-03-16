package samt.smajilbasic.deduplicator.logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * MyLogger
 */
public class MyLogger {

    static private FileHandler fileHandler;

    static public void setup() throws IOException {
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        fileHandler = new FileHandler("log.txt");
        fileHandler.setFormatter(new LogFormatter());
        logger.addHandler(fileHandler);
    }
}