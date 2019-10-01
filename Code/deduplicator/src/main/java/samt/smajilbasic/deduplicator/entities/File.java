package samt.smajilbasic.deduplicator.entities;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * File
 */
@Entity
public class File {

    @Id
    private String path;

    private Timestamp lastModified;

    private String hash;
    
    private Integer size;

    // @ManyToOne
    // @JoinColumn(name="reportId")
    private String report;

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the lastModified
     */
    public Timestamp getLastModified() {
        return lastModified;
    }

    /**
     * @param lastModified the lastModified to set
     */
    public void setLastModified(Timestamp lastModified) {
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
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return the size
     */
    public Integer getSize() {
        return size;
    }


    /**
     * @param size the size to set
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * @return the reportId
     */
    public String getReport() {
        return report;
    }

    /**
     * @param report_id the reportId to set
     */
    public void setReport(String report) {
        this.report = report;
    }

}