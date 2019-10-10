package samt.smajilbasic.deduplicator.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Duplicate
 */
@Entity
public class Duplicate {

    @Id
    private String path;
    private Long lastModified;

    public Duplicate(String path, Long lastModified) {
        this.path = path;
        this.lastModified = lastModified;
    }


    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the lastModified
     */
    public Long getLastModified() {
        return lastModified;
    }

}