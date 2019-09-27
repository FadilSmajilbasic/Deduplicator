package samt.smajilbasic.deduplicator.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * ActionType
 */
@Entity
public class ActionType {
    @Id
    @OneToMany(mappedBy = "actionType")
    private String type;

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
}