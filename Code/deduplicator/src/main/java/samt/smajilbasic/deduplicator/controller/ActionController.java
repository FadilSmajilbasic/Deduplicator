package samt.smajilbasic.deduplicator.controller;

import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import samt.smajilbasic.deduplicator.ActionType;
import samt.smajilbasic.deduplicator.PathType;
import samt.smajilbasic.deduplicator.Validator;
import samt.smajilbasic.deduplicator.entity.Action;
import samt.smajilbasic.deduplicator.entity.AuthenticationDetails;
import samt.smajilbasic.deduplicator.exception.Message;
import samt.smajilbasic.deduplicator.repository.ActionRepository;
import samt.smajilbasic.deduplicator.repository.AuthenticationDetailsRepository;
import samt.smajilbasic.deduplicator.repository.SchedulerRepository;
import samt.smajilbasic.deduplicator.timer.ScheduleChecker;
import samt.smajilbasic.deduplicator.worker.ActionsManager;

/**
 * ActionController si occupa di gestire le richieste in entrata che hanno come
 * primo pezzo del percorso "/action". Usa l'annotazione @RestController per
 * indicare a spring che questa classe è un controller e che dovrà essere
 * inizializzata all'avvio dell'applicazione.
 * 
 * @author Fadil Smajilbasic
 */
@RestController
@RequestMapping(path = "/action")
public class ActionController {

    /**
     * L'attributo actionRepository serve al controller per interfacciarsi con la
     * tabella Action del database. Usa l'annotazione @Autowired per indicare a
     * spring che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    private ActionRepository actionRepository;

    /**
     * L'attributo schedulerRepository serve al controller per interfacciarsi con la
     * tabella Scheduler del database. Usa l'annotazione @Autowired per indicare a
     * spring che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    private SchedulerRepository schedulerRepository;

    /**
     * L'attributo adr serve al controller per interfacciarsi con la tabella
     * AuthenticationDetails del database. Usa l'annotazione @Autowired per indicare
     * a spring che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    private AuthenticationDetailsRepository adr;

    /**
     * L'attributo checker controlla se ci sono operazioni da eseguire salvate nella
     * tabella Scheduler. Usa l'annotazione @Autowired per indicare a spring che
     * questo parametro dovrà essere creato come Bean e dovrà essere inizializzato
     * alla creazione della classe.
     */
    @Autowired
    private ScheduleChecker checker;

    /**
     * L'attributo context contiene il contesto dell'applicazione. Viene usato per
     * trovare l'utente attualmente collegato.
     */
    @Autowired
    private ApplicationContext context;

    /**
     * Il metodo getActions risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/action/</b>(localhost:8080/action/).
     * 
     * @return tutte le azioni contenute nella tabella Action del database.
     */
    @GetMapping("/")
    public @ResponseBody Iterable<Action> getActions() {
        return actionRepository.findAll();
    }

    /**
     * Il metodo getActions risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/action/&lt;id&gt;</b> (localhost:8080/action/8).
     * 
     * @param id l'id del record da ritornare
     * @return Se esiste ritorna l'azione richiesta in base all'id passato come
     *         parametro, altrimenti risponde con un messaggio d'errore.
     */
    @GetMapping(value = "/{id}")
    public @ResponseBody Object getActions(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if (intId != null && actionRepository.existsById(intId))
            return actionRepository.findById(intId).get();
        else
            return new Message(HttpStatus.NOT_FOUND, "Invalid action id");
    }

    /**
     * Il metodo executeActions risponde alla richiesta di tipo POST sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/action/execute/all/</b>
     * (localhost:8080/action/execute/all/). Il metodo esegue tutte le azioni che
     * l'utente ha inserito nel database.
     * 
     * @return tutte le azioni dell'utente.
     */
    @PostMapping("/execute/all")
    public @ResponseBody Object executeActions() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUser = authentication.getName();

        AuthenticationDetails internalUser = adr.findById(authenticatedUser).get();

        ActionsManager manager = (ActionsManager) context.getBean("actionsManager");
        manager.setActions(actionRepository.findActionsFromUser(internalUser));
        manager.setUser(internalUser);
        Timer timer = new Timer();
        timer.schedule(manager, 0);

        return actionRepository.findActionsFromUser(internalUser);
    }

    /**
     * Il metodo executeActions risponde alla richiesta di tipo PUT sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/action</b> (localhost:8080/action/). Il metodo
     * inserisce una nuova azione nel database Action.
     * 
     * @param type      il tipo dell'azione (DELETE,MOVE,IGNORE o SCAN).
     * @param path      il percorso del file nel caso che l'azione sia DELETE, MOVE
     *                  o IGNORE.
     * @param newPath   il nuovo percorso del file nel case che l'azione sia MOVE.
     * @param scheduler l'id dello scheduler al quale legare la azione.
     * @return l'azione inserita oppure un messaggio d'errore in base al errore
     *         riscontrato.
     */
    @PutMapping("/")
    public @ResponseBody Object addAction(@RequestParam String type, @RequestParam(required = false) String path,
            @RequestParam(required = false) String newPath,String scheduler) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

        Integer schedulerIdInt = Validator.isInt(scheduler);
        if (!schedulerRepository.existsById(schedulerIdInt != null ? schedulerIdInt : -1))
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Scheduler id invalid");
        type = getType(type);
        if (type == null)
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Action type invalid");

        if (type.equals(ActionType.MOVE) && (newPath.trim().equalsIgnoreCase("") || newPath == null))
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "New path not set while having type = MOVE");

        if (Validator.getPathType(path) != PathType.File && !type.equals(ActionType.SCAN))
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "File path invalid");

        if (Validator.getPathType(newPath) != PathType.Directory && !type.equals(ActionType.SCAN))
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "New path is invalid or not a directory");

        Action action = new Action(type, path, newPath, adr.findById(currentUser).get(),
                schedulerRepository.findById(schedulerIdInt).get());
        actionRepository.save(action);
        checker.check();
        return action;

    }

    /**
     * Il metodo getType serve a identificare il tipo di azione passato come
     * parametro.
     * 
     * @param type la stringa da controllare
     * @return il tipo con tutte le lettere in maiuscolo oppure null se il tipo
     *         passato come parametro non è valido.
     */
    private String getType(String type) {
        if (type.equalsIgnoreCase(ActionType.DELETE) || type.equalsIgnoreCase(ActionType.MOVE)
                || type.equalsIgnoreCase(ActionType.IGNORE) || type.equalsIgnoreCase(ActionType.SCAN))
            return type.toUpperCase();
        else
            return null;
    }

    /**
     * Il metodo deleteAction risponde alla richiesta di tipo DELETE sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/action/&lt;id&gt;</b> (localhost:8080/action/7).
     * Il metodo elimina un'azione dal database Action in funzione nel id passato.
     * 
     * @param id l'id della azione da eliminare.
     * @return L'azione elimianta oppure un messaggio d'errore.
     */
    @DeleteMapping("/{id}")
    public @ResponseBody Object deleteAction(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if (intId != null) {
            if (actionRepository.existsById(intId)) {
                Action action = actionRepository.findById(intId).get();
                actionRepository.delete(action);
                return action;
            } else {
                return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to find action with id: " + id);
            }
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid parameter: " + id);
        }
    }

    /**
     * Il metodo checkPath risponde alla richiesta di tipo POST sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/action/path&gt;</b> (localhost:8080/action/path). Il
     * metodo controlla se il path passato come parametro è valido oppure no. Il
     * metodo veine usato dalla gui per verificare la validità di un percorso quando
     * l'utente sceglie di muovere un duplicato in una nuova posizione. Il percorso
     * passato deve essere una cartella che si trova sul disco.
     * 
     * @param path il percorso da controllare, passato come parametro del body della
     *             richiesta.
     * @return true se il percorso è valido, false altrimenti.
     */
    @PostMapping("/path")
    public @ResponseBody Message checkPath(@RequestParam String path) {
        return new Message(HttpStatus.OK, String.valueOf(Validator.getPathType(path).equals(PathType.Directory)));
    }

}
