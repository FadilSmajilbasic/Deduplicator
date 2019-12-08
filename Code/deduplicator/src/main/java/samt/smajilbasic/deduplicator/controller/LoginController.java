package samt.smajilbasic.deduplicator.controller;

import samt.smajilbasic.deduplicator.entity.AuthenticationDetails;
import samt.smajilbasic.deduplicator.exception.Message;
import samt.smajilbasic.deduplicator.repository.AuthenticationDetailsRepository;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * LoginController si occupa di gestire le richieste in entrata
 * che hanno come primo pezzo del percorso "/login". Usa
 * l'annotazione @RestController per indicare a spring che questa classe è un
 * controller e che dovrà essere inizializzata all'avvio dell'applicazione.
 * 
 * @author Fadil Smajilbasic
 */
@RestController
@RequestMapping(path = "/login")
public class LoginController {

    /**
     * L'attributo USERNAME_LENGTH definische la lunghezza minima del username di un utente
     */
    private static final int USERNAME_LENGTH = 4;
    /**
     * L'attributo PASSWORD_LENGTH definische la lunghezza minima della password
     */
    private static final int PASSWORD_LENGTH = 8;

    /**
     * L'attributo adr serve al controller per interfacciarsi con la
     * tabella AuthenticationDetails del database. Usa l'annotazione @Autowired per indicare a
     * spring che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    private AuthenticationDetailsRepository adr;

    /**
     * Il metodo getFiles risponde alla richiesta di qualisasi tipo (GET,POST,PUT,DELETE,...) sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/login</b>(localhost:8080/login/).
     * 
     * @return il messaggio che l'utente è autenticato, altimenti riceve un messaggio d'errore da Spring.
     */
    @RequestMapping("/")
    public @ResponseBody Message checkLogin() {
        return new Message(HttpStatus.OK, "User authenticated sucessfuly");
    }

    /**
     * Il metodo insertUser risponde alla richiesta di tipo PUT sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/login/user</b>(localhost:8080/login/user/).
     * @param username il username del utente da inserire, il parametro deve essere nella parte body della richiesta.
     * @param password la password del utente da inserire, il parametro deve essere nella parte body della richiesta.
     * @return L'utente inserito nel database oppure un messaggio d'errore in base al errore riscontrato.
     */
    @PutMapping("/user")
    public @ResponseBody Object insertUser(@RequestParam String username, @RequestParam String password) {
        username = username.trim();
        password = password.trim();
        if (!username.equals("") && username.length() >= USERNAME_LENGTH) {
            if (!password.equals("") && password.length() >= PASSWORD_LENGTH) {
                if (!adr.existsById(username)) {
                    try {
                        adr.save(new AuthenticationDetails(username, password));
                        return adr.findById(username).get();
                    } catch (NoSuchAlgorithmException e) {
                        return new Message(HttpStatus.INTERNAL_SERVER_ERROR,
                                "BCrypt algorithm not available on server");
                    }
                } else {
                    return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Username already taken");
                }
            } else {
                return new Message(HttpStatus.BAD_REQUEST,
                        "Password too short, should be at least " + PASSWORD_LENGTH + " charaters long");
            }
        } else {
            return new Message(HttpStatus.BAD_REQUEST,
                    "Username too short, should be at least" + USERNAME_LENGTH + "charaters long");
        }

    }

    /**
     * Il metodo insertUser risponde alla richiesta di tipo DELETE sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/login/user</b>(localhost:8080/login/user/).
     * @param username il username del utente da eliminare, il parametro deve essere nella parte body della richiesta.
     * @return L'utente eliminato oppure il messaggio d'errore se l'utente da eliminare non esiste.
     */
    @DeleteMapping("/user")
    public @ResponseBody Object deleteUser(@RequestParam String username) {
        username = username.trim();

        if (!adr.existsById(username)) {
            adr.delete(adr.findById(username).get());
            return adr.findById(username).get();
        } else {
            return new Message(HttpStatus.NOT_FOUND, "No user with username" + username);
        }

    }

}