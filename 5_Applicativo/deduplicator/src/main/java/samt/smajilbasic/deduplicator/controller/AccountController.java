package samt.smajilbasic.deduplicator.controller;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import samt.smajilbasic.deduplicator.config.SecurityConfig;
import samt.smajilbasic.deduplicator.entity.AuthenticationDetails;
import samt.smajilbasic.deduplicator.exception.Response;
import samt.smajilbasic.deduplicator.repository.AuthenticationDetailsRepository;

import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Optional;
import java.util.function.Function;
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

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @Autowired
    private SecurityConfig securityConfig;
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
     * al errore riscontrato.
     */
    @PutMapping()
    public Object insert(@RequestParam String username, @RequestParam String password) {
        username = username.trim();
        password = password.trim();
        if (!username.equals("") && username.length() >= AccessController.USERNAME_LENGTH) {
            if (!password.equals("") && password.length() >= AccessController.PASSWORD_LENGTH) {
                if (!adr.existsById(username)) {
                    try {
                        AuthenticationDetails ad = new AuthenticationDetails(username, password);
                        adr.save(ad);
                        User.UserBuilder builder = User.builder();
                        if(inMemoryUserDetailsManager.userExists(username)){
                            inMemoryUserDetailsManager.deleteUser(username);
                        }
                        inMemoryUserDetailsManager.createUser(builder.username(username).password(ad.getPassword()).roles("USER").build());
                        return adr.findById(username).get();
                    } catch (NoSuchAlgorithmException e) {
                        Logger.getGlobal().log(Level.SEVERE, "BCrypt algorithm not available on server");
                        return new ResponseEntity<Response>(new Response("BCrypt algorithm not available on server"),
                            HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    Logger.getGlobal().log(Level.SEVERE, "Username already taken");
                    return new ResponseEntity<Response>(new Response("Username already taken"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                Logger.getGlobal().log(Level.SEVERE, "Password too short: " + password);

                return new ResponseEntity<Response>(new Response("Password too short, should be at least "
                    + AccessController.PASSWORD_LENGTH + " charaters long"), HttpStatus.BAD_REQUEST);

            }
        } else {
            Logger.getGlobal().log(Level.SEVERE, "Username too short: " + username);
            return new ResponseEntity<Response>(new Response(
                "Username too short, should be at least" + AccessController.USERNAME_LENGTH + "characters long"),
                HttpStatus.BAD_REQUEST);

        }

    }

    @PutMapping("/password")
    public Object updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        if (!oldPassword.isBlank()) {
            if (!newPassword.isBlank()) {
                if (newPassword.length() >= AccessController.PASSWORD_LENGTH) {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    Optional<AuthenticationDetails> tempUser = adr.findById(authentication.getName());
                    if (tempUser.isPresent()) {
                        AuthenticationDetails internalUser = tempUser.get();
                        if (encoder.matches(oldPassword, internalUser.getPassword())) {
                            try {
                                internalUser = new AuthenticationDetails(internalUser.getUsername(), newPassword);
                                adr.save(internalUser);
                                if (updatePasswordInMemory(internalUser.getUsername(), internalUser.getPassword())) {
                                    Logger.getGlobal().log(Level.INFO,
                                        "Password updated successfully for user " + internalUser.getUsername());
                                    return new ResponseEntity<>(HttpStatus.OK);
                                } else {
                                    Logger.getGlobal().log(Level.SEVERE,
                                        "An error occurred while updating the password of " + internalUser.getUsername());
                                    return new ResponseEntity<Response>(new Response("An error occurred while updating the password"), HttpStatus.INTERNAL_SERVER_ERROR);
                                }
                            } catch (NoSuchAlgorithmException e) {
                                Logger.getGlobal().log(Level.SEVERE, "BCrypt algorithm not available on server");

                                return new ResponseEntity<Response>(
                                    new Response("BCrypt algorithm not available on server"),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                        } else {
                            Logger.getGlobal().log(Level.WARNING,
                                "oldPassword parameter doesn't match the current password");
                            System.out.println(
                                "oldPassword: " + oldPassword + " internal: " + internalUser.getPassword());
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
                            + AccessController.PASSWORD_LENGTH + " characters."),
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
    public ResponseEntity<?> updateUsername(@RequestParam String newUsername, @RequestParam String password) {
        if (!newUsername.isBlank()) {
            if (newUsername.length() >= AccessController.USERNAME_LENGTH) {
                if (!password.isBlank()) {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    AuthenticationDetails internalUser = adr.findById(authentication.getName()).get();
                    if (!internalUser.getUsername().equals("admin")) {
                        if (encoder.matches(password, internalUser.getPassword())) {
                            if (!adr.existsById(newUsername)) {
                                delete(internalUser.getUsername());
                                insert(newUsername, password);
                                if (updateUsernameInMemory(internalUser.getUsername(), newUsername)) {
                                    Logger.getGlobal().log(Level.INFO, "Username updated successfully from "
                                        + internalUser.getUsername() + " to " + newUsername);
                                    return new ResponseEntity<String>(HttpStatus.OK);
                                } else {
                                    Logger.getGlobal().log(Level.SEVERE,
                                        "An error occurred while updating the username of " + internalUser.getUsername());
                                    return new ResponseEntity<Response>(new Response("An error occurred while updating the username of " + internalUser.getUsername()), HttpStatus.INTERNAL_SERVER_ERROR);
                                }
                            }else{
                                Logger.getGlobal().log(Level.SEVERE, "Username already taken");
                                return new ResponseEntity<Response>(new Response("Username already taken"),
                                    HttpStatus.INTERNAL_SERVER_ERROR);
                            }
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

                return new ResponseEntity<Response>(new Response(
                    "Username too short, should be at least" + AccessController.USERNAME_LENGTH + "charaters long"),
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
     * eliminare non esiste.
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
                        AuthenticationDetails user = adr.findById(username).get();
                        adr.delete(user);
                        inMemoryUserDetailsManager.deleteUser(username);
                        return user;
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

    private boolean exists(String username) {
        return inMemoryUserDetailsManager.userExists(username);
    }

    private boolean updatePasswordInMemory(String username, String password) {
        if (exists(username)) {
            inMemoryUserDetailsManager.changePassword(inMemoryUserDetailsManager.loadUserByUsername(username).getPassword(), password);
            return true;
        } else {
            Logger.getGlobal().log(Level.SEVERE, "Unable to update password of nonexistent user: " + username);
            return false;
        }
    }

    private boolean updateUsernameInMemory(String username, String newUsername) {
        if (exists(username)) {
            UserDetails user = inMemoryUserDetailsManager.loadUserByUsername(username);
            if (deleteUser(username)) {
                Function<String, String> passwordEncoder = new Function<String, String>() {
                    @Override
                    public String apply(String password) {
                        return new BCryptPasswordEncoder().encode(password);
                    }
                };
                inMemoryUserDetailsManager.createUser(User.builder().username(username).passwordEncoder(passwordEncoder).password(user.getPassword()).roles("USER").build());
                return true;
            } else {
                Logger.getGlobal().log(Level.SEVERE, "Unable to delete user");
                return false;
            }
        } else {
            Logger.getGlobal().log(Level.SEVERE, "Unable to update username of nonexistent user: " + username);
            return false;
        }
    }

    private boolean deleteUser(String username) {
        if (exists(username)) {
            inMemoryUserDetailsManager.deleteUser(username);
            Logger.getGlobal().log(Level.INFO, "Successfully deleted user " + username);
            return true;
        } else {
            Logger.getGlobal().log(Level.SEVERE, "Unable to delete user");
            return false;
        }
    }

}