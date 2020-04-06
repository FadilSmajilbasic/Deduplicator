package samt.smajilbasic.logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * LogFormatter
 */
public class LogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();

        builder.append("[").append(record.getLevel().getName()).append("]").append("\t");

        if(record.getLevel().equals(Level.INFO)){
            builder.append("\t");
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(record.getMillis());
        builder.append(sdf.format(cal.getTime())).append(" ");

        builder.append(record.getMessage());
        if (record.getLevel().equals(Level.SEVERE) || record.getLevel().equals(Level.WARNING) ) {
            builder.append(" at ").append(record.getSourceMethodName()).append(" in ").append(record.getSourceClassName());
        }
        builder.append("\n");

        return builder.toString();
    }

}