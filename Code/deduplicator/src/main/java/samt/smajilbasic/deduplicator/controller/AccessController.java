package samt.smajilbasic.deduplicator.controller;

import samt.smajilbasic.deduplicator.exception.Response;
import samt.smajilbasic.deduplicator.repository.AuthenticationDetailsRepository;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * LoginController si occupa di gestire le richieste in entrata che hanno come
 * primo pezzo del percorso "/login". Usa l'annotazione @RestController per
 * indicare a spring che questa classe è un controller e che dovrà essere
 * inizializzata all'avvio dell'applicazione.
 * 
 * @author Fadil Smajilbasic
 */
@RestController
@RequestMapping(path = "/access")
public class AccessController {

    /**
     * L'attributo USERNAME_LENGTH definische la lunghezza minima del username di un
     * utente
     */
    private static final int USERNAME_LENGTH = 4;
    /**
     * L'attributo PASSWORD_LENGTH definische la lunghezza minima della password
     */
    private static final int PASSWORD_LENGTH = 8;

    /**
     * L'attributo adr serve al controller per interfacciarsi con la tabella
     * AuthenticationDetails del database. Usa l'annotazione @Autowired per indicare
     * a spring che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    private AuthenticationDetailsRepository adr;

    /**
     * Il metodo checkLogin risponde alla richiesta di qualisasi tipo
     * (GET,POST,PUT,DELETE,...) sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/login</b>(localhost:8080/login/).
     * 
     * @return il messaggio che l'utente è autenticato, altimenti riceve un
     *         messaggio d'errore da Spring.
     */
    @RequestMapping("/login")
    public ResponseEntity<Response> checkLogin() {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "User logged in");
        return new ResponseEntity<Response>(new Response("User authenticated successfully"), HttpStatus.OK);

    }

    @RequestMapping("/logout/sucess")
    public ResponseEntity<Response> logoutSucess() {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "User logged out");
        return new ResponseEntity<Response>(new Response("Logged out"), HttpStatus.OK);
    }

}