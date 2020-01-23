package samt.smajilbasic.deduplicator.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.springframework.lang.Nullable;

/**
 * La classe Report rappresenta un rapporto di una scansione.
 */
@Entity
public class Report {

    /**
     * L'attributo path conteine il percorso assoluto del file o cartella. Usa
     * l'annotazione @Id per indicare che è una chiave primaria della tabella.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer reportId;

    /**
     * L'attributo duration indica la durata della scansione in millisecondi. Usa
     * l'annotazione @Nullable per indicare a Spring che il valore può essere null.
     */
    @Nullable
    private Long duration;

    /**
     * L'attributo start indica la data e ora d'inizio della scansione in formato
     * timestamp.
     */
    private Long start;

    /**
     * L'attributo filesScanned indica il numero di file scansionati. Usa
     * l'annotazione @Nullable per indicare a Spring che il valore può essere null.
     */
    @Nullable
    private Integer filesScanned;

    /**
     * L'attributo averageDuplicateCount indica il rapporto tra il numero di
     * duplicati e il numero di file scansionati. Usa l'annotazione @Nullable per
     * indicare a Spring che il valore può essere null.
     */
    @Nullable
    private Float averageDuplicateCount;

    /**
     * L'attributo user indica l'utente che ha iniziato la scansione. Usa
     * l'annotazione @JoinColumn per impostare il nome della colonna nel database.
     * Usa l'annotazione @ManyToOne per indicare che l'attributo è una foreign key e
     * l'opzione FetchType.LAZY significa che l'inizializzazione deve essere
     * ritardata al più tardi possibile. L'annotazione @JsonIgnoreProperties serve
     * per evitare di creare un json infonito perchè {@link AuthenticationDetails}
     * contiene un riferimento a Report.
     */
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private AuthenticationDetails user;

    /**
     * L'attributo file contiene tutti i file scansionati in questo rapporto. Usa
     * l'annotazione @OneToMany per indicare che l'attributo è una foreign key:
     * l'opzione FetchType.LAZY significa che l'inizializzazione deve essere
     * ritardata al più lungo possibile, L'annotazione {@link JsonManagedReference}
     * indica a Spring che la proprità file fa parte di una referenza incrociata tra
     * due attributi.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "report")
    @JsonManagedReference
    private List<File> file;

    /**
     * Metodo costruttore vuoto.
     */
    public Report() {
    }

    /**
     * Metodo costruttore che accetta come parametro l'utente.
     */
    public Report(AuthenticationDetails user) {
        setUser(user);
    }

    /**
     * Metodo getter per la variabile id.
     * 
     * @return l'id del rapporto.
     */
    public Integer getId() {
        return reportId;
    }

    /**
     * Metodo getter della variabile duration.
     * 
     * @return la durata della scansione in millisecondi.
     */
    public Long getDuration() {
        return duration;
    }

    /**
     * Metodo setter per la variabile duration.
     * 
     * @param duration la durata della scansione da impostare in millisecondi.
     */
    public void setDuration(Long duration) {
        this.duration = duration;
    }

    /**
     * Metodo getter per l'attributo start;
     * 
     * @return la data e ora d'avvio della scansione in formato timestamp.
     */
    public Long getStart() {
        return start;
    }

    /**
     * Metodo setter per l'attributo start.
     * 
     * @param start la data e ora d'avvio della scansione in formato timestamp.
     */
    public void setStart(Long start) {
        this.start = start;
    }

    /**
     * Metodo getter per l'attributo averageDuplicateCount.
     * 
     * @return il rapporto tra il numero di duplicati e il numero di file
     *         scansionati.
     */
    public Float getAverageDuplicateCount() {
        return averageDuplicateCount;
    }

    /**
     * Metodo setter per l'attributo averageDuplicateCount.
     * @param averageDuplicateCount il rapportdo dei duplicati e dei file trovati da impostare.
     */
    public void setAverageDuplicateCount(Float averageDuplicateCount) {
        this.averageDuplicateCount = averageDuplicateCount;
    }

    /**
     * Metodo getter per l'attributo user.
     * @return l'utente che ha inizializzato la sansione.
     */
    public AuthenticationDetails getUser() {
        return user;
    }

    /**
     * Metodo setter per l'attributo user.
     * @param user l'utente da impostare.
     */
    private void setUser(AuthenticationDetails user) {
        if (user != null)
            this.user = user;
        else
            throw new RuntimeException("[ERROR] Report Username invalid");
    }

    /**
     * Metodo getter per l'attributo file.
     * @return la lista dei file scansionati.
     */
    public List<File> getFile() {
        return file;
    }

    /**
     * Metodo getter per l'attributo filesScanned.
     * @return il numero di file scansionati.
     */
    public Integer getFilesScanned() {
        return filesScanned;
    }

    /**
     * Metodo setter per l'attributo filesScanned.
     * @param filesScanned il numero di file che sono stati scansionati.
     */
    public void setFilesScanned(Integer filesScanned) {
        this.filesScanned = (filesScanned >= 0) ? filesScanned : 0;
    }

}