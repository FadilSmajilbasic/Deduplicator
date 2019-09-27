package samt.smajilbasic.deduplicator.entities;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Scheduler
 */
@Entity
public class Scheduler {

    @Id
    private Integer scheduler_id;

    private Integer monthly;

    private Integer weekly;

    private Integer hour;
    private boolean repeated;
    private Date date;
}