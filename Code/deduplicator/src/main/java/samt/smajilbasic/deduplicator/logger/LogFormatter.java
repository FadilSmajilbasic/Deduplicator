package samt.smajilbasic.deduplicator.logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * LogFormatter
 */
public class LogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();

        builder.append("[" + record.getLevel().getName() +"] ");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(record.getMillis());
        builder.append(sdf.format(cal.getTime())+ " ");
        
        builder.append(record.getMessage()+" at ");
        builder.append(record.getSourceClassName() + " at " + record.getSourceMethodName());

        return builder.toString();
    }

    
    
    
}