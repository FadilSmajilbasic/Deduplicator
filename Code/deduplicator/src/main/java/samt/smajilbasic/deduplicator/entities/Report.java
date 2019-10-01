package samt.smajilbasic.deduplicator.entities;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Report
 */
@Entity
public class Report {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer reportId;

    private Integer duration;

    private Timestamp start;

    private Integer duplicateCount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private AuthenticationDetails user;

    /**
     * @return the id
     */
    public Integer getId() {
        return reportId;
    }


    /**
     * @return the duration
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    /**
     * @return the start
     */
    public Timestamp getStart() {
        return start;
    }
    /**
     * @param start the start to set
     */
    public void setStart(Timestamp start) {
        this.start = start;
    }
    /**
     * @return the duplicateCount
     */
    public Integer getDuplicateCount() {
        return duplicateCount;
    }

    /**
     * @param duplicateCount the duplicateCount to set
     */
    public void setDuplicateCount(Integer duplicateCount) {
        this.duplicateCount = duplicateCount;
    }

    /**
     * @return the user
     */
    public AuthenticationDetails getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(AuthenticationDetails user) {
        this.user = user;
    }


    
}