package samt.smajilbasic.deduplicator.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import samt.smajilbasic.deduplicator.entity.Scheduler;
import samt.smajilbasic.deduplicator.repository.SchedulerRepository;
import samt.smajilbasic.deduplicator.worker.ActionsManager;

/**
 * RemindTask
 */
@Component
public class ScheduleChecker {

    @Autowired
    SchedulerRepository schedulerRepository;

    @Autowired
    ActionsManager actionsManager;

    @Autowired
    ApplicationContext context;

    public ScheduleChecker() {
        super();
    }

    public void check() {
        Iterable<Scheduler> schedulers = schedulerRepository.findAll();

        schedulers.forEach(schedule -> {

            actionsManager = (ActionsManager) context.getBean("actionsManager");
            Date startDate = schedule.getDateStart();

            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            Timer timer = new Timer();

            if (!(schedule.getExecutonCounter() < 1 && schedule.isRepeated())) {
                synchronized (timer) {
                    try {
                        actionsManager.setActionScheduler(schedule);
                        actionsManager.setTimer(timer);

                        timer.schedule(actionsManager, startDate);
                        System.out.println("Scheduled");
                    } catch (IllegalStateException ise) {
                        System.out.println(
                                "[INFO] Unable to set timer: " + ise.getMessage() + " >> " + schedule.getSchedulerId());
                    }
                }
            } else {
                System.out.println("[INFO] Scheduler already executed");

            }
        });
    }
}