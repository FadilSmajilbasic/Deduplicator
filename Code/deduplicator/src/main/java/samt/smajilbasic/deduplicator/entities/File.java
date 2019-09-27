package samt.smajilbasic.deduplicator.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * File
 */
@Entity
public class File {

    @Id
    private String path;

    private long lastModified;

    private String hash;
    
    private Integer size;

    private Integer reportId;

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
    public long getLastModified() {
        return lastModified;
    }

    /**
     * @param lastModified the lastModified to set
     */
    public void setLastModified(long lastModified) {
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
    public Integer getReportId() {
        return reportId;
    }

    /**
     * @param reportId the reportId to set
     */
    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

}