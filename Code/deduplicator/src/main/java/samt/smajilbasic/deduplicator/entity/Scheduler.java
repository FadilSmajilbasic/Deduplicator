package samt.smajilbasic.deduplicator.entity;

import java.sql.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;


/**
 * Scheduler
 */
@Entity
public class Scheduler {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer schedulerId;

    private Integer monthly;

    private Integer weekly;

    private Integer minutes;
    private Boolean repeated;
    private Date dateStart;
    private Integer executonCounter = 0;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "scheduler")
    private List<Action> action;

    /**
     * @return the schedulerId
     */
    public Integer getSchedulerId() {
        return schedulerId;
    }

    /**
     * @return the monthly
     */
    public Integer getMonthly() {
        return monthly;
    }

    /**
     * @param monthly the monthly to set
     */
    public void setMonthly(Integer monthly) {
        this.monthly = monthly;
    }

    /**
     * @return the weekly
     */
    public Integer getWeekly() {
        return weekly;
    }

    /**
     * @param weekly the weekly to set
     */
    public void setWeekly(Integer weekly) {
        this.weekly = weekly;
    }

    /**
     * @return the minutes
     */
    public Integer getMinutes() {
        return minutes;
    }

    /**
     * @param minutes the minutes to set
     */
    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    /**
     * @return the repeated
     */
    public boolean isRepeated() {
        return repeated;
    }

    /**
     * @param repeated the repeated to set
     */
    public void setRepeated(boolean repeated) {
        this.repeated = repeated;
    }

    /**
     * @return the date_start
     */
    public Date getDateStart() {
        return dateStart;
    }

    /**
     * @param dateStart the dateStart to set
     */
    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    /**
     * @return the executonCounter
     */
    public Integer getExecutonCounter() {
        return executonCounter;
    }

    public void executed(){
        executonCounter++;
    }
}