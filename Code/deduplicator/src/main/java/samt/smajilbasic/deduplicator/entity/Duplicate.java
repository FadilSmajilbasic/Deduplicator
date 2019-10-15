package samt.smajilbasic.deduplicator.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Duplicate
 */
@Entity
public class Duplicate {

    @Id
    private String hash;
    private Integer size;
    private Integer count;
    

    public Duplicate() {
        super();
    }

    public Duplicate(Integer size,String hash,Integer count) {
        this.size = size;
        this.hash = hash;
        this.count = count;
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

    /**
     * @return the count
     */
    public Integer getCount() {
        return count;
    }
}