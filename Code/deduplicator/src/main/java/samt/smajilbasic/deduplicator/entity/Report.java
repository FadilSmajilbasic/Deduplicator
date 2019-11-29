package samt.smajilbasic.deduplicator.entity;

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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.springframework.lang.Nullable;


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

    private Long start;

    @Column(nullable = true)
    private Integer filesScanned;

    @Column(nullable = true)
    private Float averageDuplicateCount;
    
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private AuthenticationDetails user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "report")
    @JsonManagedReference
    private List<File> file;


    public Report() {
    }

    public Report(AuthenticationDetails user) {
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
    public Long getStart() {
        return start;
    }
    /**
     * @param start the start to set
     */
    public void setStart(Long start) {
        this.start = start;
    }

    /**
     * @return the averageDuplicateCount
     */
    public Float getAverageDuplicateCount() {
        return averageDuplicateCount;
    }

    /**
     * @param averageDuplicateCount the averageDuplicateCount to set
     */
    public void setAverageDuplicateCount(Float averageDuplicateCount) {
        this.averageDuplicateCount = averageDuplicateCount;
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
            throw new RuntimeException("[ERROR] Report Username invalid");
    }

    /**
     * @return the file
     */
    public List<File> getFile() {
        return file;
    }

    /**
     * @return the filesScanned
     */
    public Integer getFilesScanned() {
        return filesScanned;
    }

    /**
     * @param filesScanned the filesScanned to set
     */
    public void setFilesScanned(Integer filesScanned) {
        this.filesScanned = (filesScanned >= 0) ? filesScanned : 0;
    }

}