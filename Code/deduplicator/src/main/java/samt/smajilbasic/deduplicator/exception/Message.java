package samt.smajilbasic.deduplicator.exception;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * ErrorMessage
 */
public class Message extends ResponseEntity<String>{

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    
    private String message;

    public Message(HttpStatus status) {
        super(status);
        timestamp = LocalDateTime.now();
    }

    public Message(HttpStatus status, String message) {
        this(status);
        this.message = message;
    }

    /**
     * @return the status
     */
    public HttpStatus getStatus() {
        return getStatusCode();
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    public void setTimestamp(LocalDateTime time){
        this.timestamp = time;
    }
}