package deduplicatorGUI.entity;

import java.sql.Timestamp;
import java.util.List;



/**
 * Report
 */
public class Report {

    private Integer reportId;

    private Long duration;

    private Timestamp start;

    private Integer filesScanned;

    private Float averageDuplicateCount;
    
    private AuthenticationDetails user;

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