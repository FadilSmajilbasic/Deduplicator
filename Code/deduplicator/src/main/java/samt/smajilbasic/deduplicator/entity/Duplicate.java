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
    private Integer size;
    private String hash;


    public Duplicate(String path, Long lastModified,Integer size,String hash) {
        this.path = path;
        this.lastModified = lastModified;
        this.size = size;
        this.hash = hash;
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
    /**
     * @return the size
     */
    public Integer getSize() {
        return size;
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

}