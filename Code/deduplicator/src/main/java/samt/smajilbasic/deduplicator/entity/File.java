package samt.smajilbasic.deduplicator.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import samt.smajilbasic.deduplicator.PathType;
import samt.smajilbasic.deduplicator.Validator;

/**
 * La classe File descrive un'file trovato. Usa l'annotazione @Entity per
 * segnalare a Spring che si tratta di una classe che descrive una Tabella del
 * database.
 * 
 */
@Entity
public class File {

    /**
     * L'attributo path contiene il percorso del file. Utilizza l'annotazione @Id
     * per indicare che è una chiave primaria della tabella.
     */
    @Id
    private String path;

    /**
     * L'attributo lastModified contiene la data dell'ultima modifica del file.
     */
    private Long lastModified;
    // TODO: add last modified to gui

    /**
     * L'attributo hash contiene il hash del contenuto del file.
     */
    private String hash;

    /**
     * L'attributo size specifica la grandezza del file in Byte.
     */
    private Long size;

    /**
     * L'attributo report specifica in quale report è stato trovato il file. Se più
     * report trovano lo stesso file verrà mantenuto quello dell'ultimo report. Usa
     * l'annotazione @ManyToOne per indicare che l'attributo è una foreign key e
     * l'opzione FetchType.LAZY significa che l'inizializzazione deve essere
     * ritardata al più lungo possibile. Usa l'annotazione {@link JoinColumn} per
     * impostare il nome della colonna nel database. Usa l'annotazione
     * {@link JsonBackReference} per indicare a Spring che {@link Report} contiene
     * una referenza a questo oggetto e che quindi non deve risolvere tutto il json
     * per evitare di creare un json infinito.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report")
    @JsonBackReference
    private Report report;

    /**
     * Costruttore vuoto
     */
    public File() {
    }

    /**
     * Costruttore che accetta tutti i parametri
     * @param path il percorso del file.
     * @param lastModified la data in millisecondi dall'ultima modifica.
     * @param hash il hash del contenuto del file.
     * @param size la grandezza del file.
     * @param report il rapporto al quale è assocciato.
     */
    public File(String path, Long lastModified, String hash, Long size, Report report) {
        setPath(path);
        setLastModified(lastModified);
        setHash(hash);
        setSize(size);
        setReport(report);
    }

    /**
     * Metodo getter per l'attributo path.
     * @return il percorso del file.
     */
    public String getPath() {
        return path;
    }

    /**
     * Metodo setter per l'attributo path.
     * @param path il percorso da impostare.
     */
    private void setPath(String path) {
        PathType type = Validator.getPathType(path);
        if (type == PathType.File) {
            this.path = path;
        } else if (type == PathType.Directory) {
            throw new RuntimeException("[ERROR] Path is not a file: " + path);
        } else {
            throw new RuntimeException("[ERROR] Invalid path: " + path);
        }
    }

    /**
     * Metodo getter per l'attributo lastModified.
     * @return la data dell'ultima modifica del file.
     */
    public Long getLastModified() {
        return lastModified;
    }

    /**
     * Metodo setter per l'attributo lastModified.
     * @param lastModified la data in millisecondi dell'ultima modifica da impostare.
     */
    private void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Metodo getter per l'attributo hash.
     * @return il hash del contenuto del file.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Metodo setter per l'attributo hash.
     * @param hash il hash del file da impostare.
     */
    private void setHash(String hash) {
        this.hash = hash;
    }
    //TODO: Add check size of hash

    /**
     * Metodo getter per l'attributo size.
     * @return la grandezza del file in Byte.
     */
    public Long getSize() {
        return size;
    }

    /**
     * Metodo setter per l'attributo size.
     * @param size la grandezza del file da impostare
     */
    private void setSize(Long size) {
        this.size = size;
    }
    //TODO: Add check

    /**
     * Metodo getter per l'attributo report.
     * @return il rapporto al quale è assocciato il file.
     */
    public Report getReport() {
        return report;
    }

    /**
     * Metodo setter per l'attributo report.
     * @param report il rapporto da impostare.
     */
    public void setReport(Report report) {
        this.report = report;
    }

}