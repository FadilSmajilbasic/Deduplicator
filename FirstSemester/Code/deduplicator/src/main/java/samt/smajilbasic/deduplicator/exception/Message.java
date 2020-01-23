package samt.smajilbasic.deduplicator.exception;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * La classe che definisce un messaggio d'errore.
 * Viene creata per dare una risposta al client in caso d'errore Durante l'esecuzione.
 */
public class Message extends ResponseEntity<String>{

    /**
     * L'attributo timestamp definisce la data e ora di qunado si è verificato l'errore.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * L'attributo message definisce il messaggio d'errore. Il messaggio cambia a dipendenza dell'errore.
     */
    private String message;

    /**
     * Metodo costruttore che imposta lo stato della risposta. 
     * @see org.springframework.http.HttpStatus
     * @param status lo stato da impostare in base all'errore.
     */
    public Message(HttpStatus status) {
        super(status);
        timestamp = LocalDateTime.now();
    }

    /**
     * Metodo costruttore che imposta lo stato e il messaggio d'errore. 
     * @param status lo stato della riposta.
     * @param message il messaggio d'errore della riposta.
     */
    public Message(HttpStatus status, String message) {
        this(status);
        this.message = message;
    }

    /**
     * Metodo getter per l'attributo message.
     * @return Il messaggio d'errore impostato.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Metodo setter per l'attributo timestamp.
     * @param time la data e ora da impostare
     */
    public void setTimestamp(LocalDateTime time){
        this.timestamp = time;
    }
}