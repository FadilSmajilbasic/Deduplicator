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
     * L'attributo size specifica la grandeza del file in Byte.
     */
    private Long size;

    /**
     * L'attributo report specifica in quale report è stato trovato il file. Se più
     * report trovano lo stesso file verrà mantenuto quello dell'ultimo report. Usa
     * l'annotazione @ManyToOne per indicare che l'attributo è una foreign key e
     * l'opzione FetchType.LAZY significa che l'inizializzazione deve essere ritardata più tempo possibile.
     * Usa l'annotazione @JoinColumn per impostare il nome della colonna nel database.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report")
    @JsonBackReference
    private Report report;

    public File() {
    }

    public File(String path, Long lastModified, String hash, Long size, Report report) {
        setPath(path);
        setLastModified(lastModified);
        setHash(hash);
        setSize(size);
        setReport(report);
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
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
     * @return the lastModified
     */
    public Long getLastModified() {
        return lastModified;
    }

    /**
     * @param lastModified the lastModified to set
     */
    private void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash the hash to set
     */
    private void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return the size
     */
    public Long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    private void setSize(Long size) {
        this.size = size;
    }

    /**
     * @return the report
     */
    public Report getReport() {
        return report;
    }

    /**
     * @param report the report to set
     */
    public void setReport(Report report) {
        this.report = report;
    }

}