package samt.smajilbasic.deduplicator.entity;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;


/**
 * La classe Scheduler descrive evento pianificato per l'esecuzione o già eseguito 
 */
@Entity
public class Scheduler {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer schedulerId;

    private Integer monthly;

    private Integer weekly;

    private Boolean repeated;
    private Long timeStart;
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
     * @return the timeStart
     */
    public Long getTimeStart() {
        return timeStart;
    }

    /**
     * @param timeStart the timeStart to set
     */
    public void setTimeStart(Long timeStart) {
        this.timeStart = timeStart;
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