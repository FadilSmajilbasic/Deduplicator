package samt.smajilbasic.deduplicator.controller;

import samt.smajilbasic.deduplicator.entity.AuthenticationDetails;
import samt.smajilbasic.deduplicator.exception.Response;
import samt.smajilbasic.deduplicator.repository.AuthenticationDetailsRepository;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * AccountController si occupa di gestire le richieste in entrata che hanno come
 * primo pezzo del percorso "/account". Usa l'annotazione @RestController per
 * indicare a spring che questa classe è un controller e che dovrà essere
 * inizializzata all'avvio dell'applicazione.
 * 
 * @author Fadil Smajilbasic
 */
@RestController
@RequestMapping(path = "/account")
public class AccountController {

    /**
     * L'attributo USERNAME_LENGTH definische la lunghezza minima del username di un
     * utente
     */
    private static final int USERNAME_LENGTH = 4;
    /**
     * L'attributo PASSWORD_LENGTH definische la lunghezza minima della password
     */
    private static final int PASSWORD_LENGTH = 8;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * L'attributo adr serve al controller per interfacciarsi con la tabella
     * AuthenticationDetails del database. Usa l'annotazione @Autowired per indicare
     * a spring che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    private AuthenticationDetailsRepository adr;

    @GetMapping()
    public AuthenticationDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthenticationDetails internalUser = adr.findById(authentication.getName()).get();

        return internalUser;
    }

    @GetMapping("/all")
    public Iterable<AuthenticationDetails> getAll() {

        return adr.findAll();
    }

    /**
     * Il metodo insert risponde alla richiesta di tipo PUT sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/login/user</b>(localhost:8080/login/user/).
     * 
     * @param username il username del utente da inserire, il parametro deve essere
     *                 nella parte body della richiesta.
     * @param password la password del utente da inserire, il parametro deve essere
     *                 nella parte body della richiesta.
     * @return L'utente inserito nel database oppure un messaggio d'errore in base
     *         al errore riscontrato.
     */
    @PutMapping()
    public Object insert(@RequestParam String username, @RequestParam String password) {
        username = username.trim();
        password = password.trim();
        if (!username.equals("") && username.length() >= USERNAME_LENGTH) {
            if (!password.equals("") && password.length() >= PASSWORD_LENGTH) {
                if (!adr.existsById(username)) {
                    try {
                        adr.save(new AuthenticationDetails(username, password));
                        return adr.findById(username).get();
                    } catch (NoSuchAlgorithmException e) {
                        return new ResponseEntity<Response>(new Response("BCrypt algorithm not available on server"),
                                HttpStatus.INTERNAL_SERVER_ERROR);

                    }
                } else {
                    return new ResponseEntity<Response>(new Response("Username already taken"),
                            HttpStatus.INTERNAL_SERVER_ERROR);

                }
            } else {
                return new ResponseEntity<Response>(
                        new Response("Password too short, should be at least " + PASSWORD_LENGTH + " charaters long"),
                        HttpStatus.BAD_REQUEST);

            }
        } else {
            return new ResponseEntity<Response>(
                    new Response("Username too short, should be at least" + USERNAME_LENGTH + "charaters long"),
                    HttpStatus.BAD_REQUEST);

        }

    }

    @PutMapping("/password")
    public Object updatePassword(String oldPassword, String newPassword) {
        if (!oldPassword.isBlank()) {
            if (!newPassword.isBlank()) {
                if (newPassword.length() >= PASSWORD_LENGTH) {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    AuthenticationDetails internalUser = adr.findById(authentication.getName()).get();
                    if (internalUser != null) {

                        if (encoder.matches(oldPassword, internalUser.getPassword())) {
                            try {
                                internalUser = new AuthenticationDetails(internalUser.getUsername(), newPassword);
                                adr.save(internalUser);
                                Logger.getGlobal().log(Level.INFO,
                                        "Password updated successfully for user " + internalUser.getUsername());
                                ModelAndView view = new ModelAndView("redirect:/logout");
                                return view;
                            } catch (NoSuchAlgorithmException e) {
                                Logger.getGlobal().log(Level.SEVERE, "BCrypt algorithm not available on server");

                                return new ResponseEntity<Response>(
                                        new Response("BCrypt algorithm not available on server"),
                                        HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                        } else {
                            Logger.getGlobal().log(Level.WARNING, "oldPassword parameter doesn't match the current password");
                            return new ResponseEntity<Response>(
                                    new Response("oldPassword parameter doesn't match the current password"),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    } else {
                        Logger.getGlobal().log(Level.WARNING, "Invalid user in database");

                        return new ResponseEntity<Response>(new Response("Invalid user in database"),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    Logger.getGlobal().log(Level.WARNING, "New password too short");
                    return new ResponseEntity<Response>(
                            new Response("New password too short, please provide a password of at least "
                                    + PASSWORD_LENGTH + " characters."),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }

            } else {
                Logger.getGlobal().log(Level.WARNING, "New password invalid");
                return new ResponseEntity<Response>(new Response("New password invalid"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            Logger.getGlobal().log(Level.WARNING, "Old password invalid");
            return new ResponseEntity<Response>(new Response("Old password invalid"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/username")
    public Object updateUsername(String newUsername, String password) {
        if (!newUsername.isBlank()) {
            if (newUsername.length() >= USERNAME_LENGTH) {
                if (!password.isBlank()) {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    AuthenticationDetails internalUser = adr.findById(authentication.getName()).get();
                    if (!internalUser.getUsername().equals("admin")) {
                        if (encoder.matches(password, internalUser.getPassword())) {

                            delete(internalUser.getUsername());
                            insert(newUsername, password);
                            Logger.getGlobal().log(Level.INFO, "Username updated successfully from "
                                    + internalUser.getUsername() + " to " + newUsername);
                            ModelAndView view = new ModelAndView("redirect:/logout");
                            return view;
                        } else {
                            Logger.getGlobal().log(Level.WARNING, "Invalid user in database");
                            return new ResponseEntity<Response>(new Response("Invalid user in database"),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    } else {
                        Logger.getGlobal().log(Level.WARNING, "Unable to change username of admin");

                        return new ResponseEntity<Response>(new Response("Unable to change username of admin"),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }

                } else {
                    Logger.getGlobal().log(Level.WARNING, "Password invalid");

                    return new ResponseEntity<Response>(new Response("Password invalid"),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                Logger.getGlobal().log(Level.WARNING, "Username too short");

                return new ResponseEntity<Response>(
                        new Response("Username too short, should be at least" + USERNAME_LENGTH + "charaters long"),
                        HttpStatus.BAD_REQUEST);
            }
        } else {
            Logger.getGlobal().log(Level.WARNING, "New username invalid");

            return new ResponseEntity<Response>(new Response("New username invalid"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * Il metodo insertUser risponde alla richiesta di tipo DELETE sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/login/user</b>(localhost:8080/login/user/).
     * 
     * @param username il username del utente da eliminare, il parametro deve essere
     *                 nella parte body della richiesta.
     * @return L'utente eliminato oppure il messaggio d'errore se l'utente da
     *         eliminare non esiste.
     */
    @DeleteMapping()
    public Object delete(@RequestParam String username) {
        username = username.trim();

        if (!adr.existsById(username)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            AuthenticationDetails internalUser = adr.findById(authentication.getName()).get();

            if (internalUser.getUsername().equals("admin")) {
                if (!username.equals("admin")) {
                    if (!username.equals(internalUser.getUsername())) {
                        adr.delete(adr.findById(username).get());
                        return adr.findById(username).get();
                    } else {
                        return new ResponseEntity<Response>(new Response("Cannot delete current user" + username),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    return new ResponseEntity<Response>(new Response("Admin cannot be deleted" + username),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<Response>(new Response("Only admin can delete users" + username),
                        HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<Response>(new Response("No user with username" + username), HttpStatus.NOT_FOUND);
        }

    }

}