package samt.smajilbasic.deduplicator.entity;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.lang.Nullable;

import samt.smajilbasic.deduplicator.exception.InvalidUserException;

/**
 * Report
 */
@Entity
public class Report {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer reportId;

    @Nullable
    private Long duration;

    private Timestamp start;

    @Column(nullable = true)
    private Integer duplicateCount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private AuthenticationDetails user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "report")
    private List<File> file;


    public Report() {
        setStart(new Timestamp(System.currentTimeMillis()));
    }

    public Report(AuthenticationDetails user) {
        this();
        setUser(user);
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return reportId;
    }


    /**
     * @return the duration
     */
    public Long getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(Long duration) {
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
    private void setStart(Timestamp start) {
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
    private void setUser(AuthenticationDetails user) {
        if(user != null)
            this.user = user;
        else
            throw new InvalidUserException();
    }


    
}