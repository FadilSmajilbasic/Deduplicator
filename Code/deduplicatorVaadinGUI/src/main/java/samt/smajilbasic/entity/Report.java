package samt.smajilbasic.entity;

import java.io.File;
import java.util.List;

/**
 * La classe Report rappresenta un rapporto di una scansione.
 */
public class Report {

    /**
     * L'attributo path conteine il percorso assoluto del file o cartella. 
     */
    private Integer reportId;

    /**
     * L'attributo duration indica la durata della scansione in millisecondi. 
     */
    
    private Long duration;

    /**
     * L'attributo start indica la data e ora d'inizio della scansione in formato
     * timestamp.
     */
    private Long start;

    /**
     * L'attributo filesScanned indica il numero di file scansionati.
     */
    
    private Integer filesScanned;

    /**
     * L'attributo averageDuplicateCount indica il rapporto tra il numero di
     * duplicati e il numero di file scansionati.
     */
    
    private Float averageDuplicateCount;

    /**
     * L'attributo user indica l'utente che ha iniziato la scansione.
     */
    private String user;

    /**
     * L'attributo file contiene tutti i file scansionati in questo rapporto.
     */
    private List<File> file;

    /**
     * Metodo costruttore vuoto.
     */
    public Report() {
    }

    /**
     * Metodo costruttore che accetta come parametro l'utente.
     */
    public Report(String user) {
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
    public String getUser() {
        return user;
    }

    /**
     * Metodo setter per l'attributo user.
     * @param user l'utente da impostare.
     */
    private void setUser(String user) {
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