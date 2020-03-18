package samt.smajilbasic.deduplicator.entity;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * La classe AuthenticationDetails descrive un utente che poi verrà usato per fare l'autenticazione.
 * Usa L'annotazione @Entity per segnalare a Spring che si tratta di una classe che descrive una Tabella del database.
 */
@Entity
public class AuthenticationDetails {

    /**
     * L'attributo username descrive lo username del utente.
     * Utilizza l'annotazione @Id per indicare che è una chiave primaria della tabella.
     */
    @Id
    private String username;

    /**
     * L'attributo password descrive la password del utente.
     * La password è salvata nella tabella usando la crittografia BCrypt.
     */
    private String password;

    /**
     * L'attributo report contiene tutti i report creati dall'utente.
     * Usa l'annotazione @OneToMany per indicare che l'attributo è una foreign key:
     * l'opzione FetchType.LAZY significa che l'inizializzazione deve essere ritardata al più lungo possibile,
     * l'opzione mappedBy indica il nome della colonna che si riferisce a questo oggetto
     * @see Report#user
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Report> report;

    /**
     * L'attributo action contiene tutti i report creati dall'utente.
     * Usa l'annotazione @OneToMany per indicare che l'attributo è una foreign key:
     * l'opzione FetchType.LAZY significa che l'inizializzazione deve essere ritardata al più lungo possibile,
     * l'opzione mappedBy indica il nome della colonna che si riferisce a questo oggetto
     * @see Action#user
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Action> action;

    /**
     * Costruttore che prende come parametro username e password dell'utente.
     * @param username lo username del utente
     * @param password la password del utente
     * @throws NoSuchAlgorithmException eccezione sollevata se sulla macchina host non esiste la crittografia BCrypt (min java version 1.8).
     */
    public AuthenticationDetails(String username, String password) throws NoSuchAlgorithmException {
        this.username = username;
        setPassword(password);
    }

    /**
     * Costruttore vuoto
     */
    public AuthenticationDetails() {
        super();
    }

    /**
     * Metodo getter per la variabile username.
     * @return lo username del utente
     */
    public String getUsername() {
        return username;
    }

    /**
     * Metodo getter per la variabile password.
     * @return la password del utente criptata con BCrypt
     */
    public String getPassword() {

        return password;
    }

    /**
     * Metodo setter per la variabile password.
     * @param password la password da impostare in chiaro
     * @throws NoSuchAlgorithmException eccezione sollevata se sulla macchina host non esiste la crittografia BCrypt (min java version 1.8).
     */
    private void setPassword(String password) throws NoSuchAlgorithmException {
        if(password != null){
            this.password = new BCryptPasswordEncoder().encode(password);
        }else{
            this.password = "";
        }
    }

}