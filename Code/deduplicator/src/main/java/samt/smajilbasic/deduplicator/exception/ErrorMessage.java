package samt.smajilbasic.deduplicator.exception;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.springframework.http.HttpStatus;

/**
 * ErrorMessage
 */
public class ErrorMessage {

    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;

    private ErrorMessage() {
        timestamp = LocalDateTime.now();
    }

    public ErrorMessage(HttpStatus status) {
        this();
        this.status = status;
    }

    public ErrorMessage(HttpStatus status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    /**
     * @return the status
     */
    public HttpStatus getStatus() {
        return status;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}