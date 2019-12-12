package samt.smajilbasic.deduplicator.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.lang.Nullable;

import samt.smajilbasic.deduplicator.ActionType;

/**
 * La classe Action descrive un'azione da fare su un determinato file che è
 * stato rilevato come duplicato.
 * Usa l'annotazione @Entity per segnalare a
 * Spring che si tratta di una classe che descrive una Tabella del database.
 * 
 */
@Entity
public class Action {
    /**
     * L'attributo actionId specifica l'id del di un'azione. Utilizza
     * l'annotazione @Id per indicare che è una chiave primaria della tabella.
     * Utilizza l'annotazione @GeneratedValue per indicare che il valore sarà
     * generato automaicamente (equivalente a auto_increment in SQL)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer actionId;

    /**
     * L'attributo filePath specifica il percorso del file sul quale verra fatta un
     * operazione.
     */
    private String filePath;

    /**
     * L'attributo newFilePath specifica il nuovo percorso del file dove verrà
     * salvato nel caso della operazione MOVE. Usa l'annotazione @Nullable per
     * indicare che può essere null.
     */
    @Nullable
    private String newFilePath;
    
    /**
     * L'attributo actionType specifica il tipo di azione. Può avere uno dei
     * segeunti valori: DELETE, MOVE, IGNORE, SCAN.
     * 
     * {@link samt.smajilbasic.deduplicator.ActionType}
     */
    private String actionType;

    /**
     * L'attributo actionType indica se l'azione è stata eseguita.
     */
    private boolean executed;

    /**
     * L'attributo actionType specifica la data d'aggiunta dell'azione.
     */
    private Long dateAdded;

    /**
     * L'attributo user specifica l'utente che ha inserito l'azione. Usa
     * l'annotazione @ManyToOne per indicare che l'attributo è una foreign key e
     * l'opzione FetchType.EAGER significa che l'inizializzazione deve occorre al
     * momento della creazione della classe. Usa l'annotazione @JoinColumn per
     * impostare il nome della colonna nel database.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    private AuthenticationDetails user;

    /**
     * L'attributo scheduler specifica llo scheduler al quale è assocciata l'azione,
     * cioè quando verrà eseguita. Usa l'annotazione @ManyToOne per indicare che
     * l'attributo è una foreign key e l'opzione FetchType.EAGER significa che
     * l'inizializzazione deve occorre al momento della creazione della classe. Usa
     * l'annotazione @JoinColumn per impostare il nome della colonna nel database.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scheduler")
    private Scheduler scheduler;

    /**
     * Costruttore della classe Action che riceve tutti i parametri.
     * 
     * @param type      Il tipo di azione.
     * @param path      Il percorso del file.
     * @param newPath   Il nuovo percorso (nel caso che type sia MOVE).
     * @param user      L'utente che ha aggiunto l'azione.
     * @param scheduler Lo scheduler di quando verrà eseguita l'azione.
     */
    public Action(String type, String path, String newPath, AuthenticationDetails user, Scheduler scheduler) {
        setActionType(type);
        setFilePath(path);
        setNewFilePath(newPath);
        this.dateAdded = System.currentTimeMillis();
        this.user = user;
        executed = false;
        setScheduler(scheduler);
    }

    /**
     * Costruttore della classe Action che non riceve il nuovo percorso.
     * 
     * @param type      Il tipo di azione.
     * @param path      Il percorso del file.
     * @param user      L'utente che ha aggiunto l'azione.
     * @param scheduler Lo scheduler di quando verrà eseguita l'azione.
     */
    public Action(String type, String path, AuthenticationDetails user, Scheduler scheduler) {
        this(type, path, null, user, scheduler);
    }

    /**
     * Costruttore vuoto.
     */
    public Action() {
    }

    /**
     * Metodo getter per l'attributo actionType.
     * 
     * @return il tipo di azione.
     */
    public String getActionType() {
        return actionType;
    }

    /**
     * Metodo setter per l'attributo actionType.
     * 
     * @param actionType il tipo da impostare
     */
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    /**
     * Metodo getter per l'attributo dateAdded.
     * 
     * @return la data d'inserimento.
     */
    public Long getDateAdded() {
        return dateAdded;
    }

    /**
     * Metodo getter per l'attributo executed.
     * @return true se l'azione è stata eseguira, false altrimenti
     */
    public boolean isExecuted() {
        return executed;
    }

    /**
     * Metodo setter per l'attributo executed.
     * Imposta l'attributo executed a true.
     */
    public void setExecuted() {
        this.executed = true;
    }

    /**
     * Metodo getter per l'attributo filePath.
     * @return il percorso fel file.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Metodo setter per l'attributo filePath.
     * @param filePath il percorso del file da impostare.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Metodo getter per l'attributo id.
     * @return l'id dell'azione.
     */
    public Integer getId() {
        return actionId;
    }

    /**
     * Metodo getter per l'attributo newFilePath.
     * @return il nuovo percorso, oppure null se il tipo di azione è diverso da MOVE.
     */
    public String getNewFilePath() {
        return newFilePath;
    }

    /**
     * Metodo setter per l'attributo newFilePath.
     * newFilePath verrà impostato su null nel caso che l'actionType sia diverso da null.
     * @param newFilePath il nuovo percorso da impostare.
     */
    public void setNewFilePath(String newFilePath) {
        if(getActionType() == ActionType.MOVE)
            this.newFilePath = newFilePath;
        else
            this.newFilePath = null;
    }

    /**
     * Metodo getter per l'attributo scheduler.
     * @return lo scheduler.
     */
    public Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * Metodo setter per l'attributo scheduler.
     * @param scheduler lo scheduler da impostare.
     */
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Metodo getter per l'attributo user.
     * @return lo user che ha inserito l'azione.
     */
    public AuthenticationDetails getUser() {
        return user;
    }

}