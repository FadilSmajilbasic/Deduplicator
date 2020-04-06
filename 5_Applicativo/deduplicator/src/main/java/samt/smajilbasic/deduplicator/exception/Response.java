package samt.smajilbasic.deduplicator.exception;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * La classe che definisce un messaggio d'errore.
 * Viene creata per dare una risposta al client in caso d'errore Durante l'esecuzione.
 */
public class Response{

    /**
     * L'attributo timestamp definisce la data e ora di qunado si è verificato l'errore.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * L'attributo message definisce il messaggio d'errore. Il messaggio cambia a dipendenza dell'errore.
     */
    private String message;


    /**
     * Metodo costruttore che imposta il messaggio d'errore.
     * @param message il messaggio d'errore della riposta.
     */
    public Response(String message) {
        this.message = message;
        timestamp = LocalDateTime.now();
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