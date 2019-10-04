package samt.smajilbasic.deduplicator.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * ActionType
 */
@Entity
public class ActionType {
    @Id
    private String type;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "actionType")
    private List<Action> action;

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