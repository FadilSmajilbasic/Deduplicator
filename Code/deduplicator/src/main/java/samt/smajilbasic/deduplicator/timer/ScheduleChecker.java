package samt.smajilbasic.deduplicator.timer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import samt.smajilbasic.deduplicator.entity.Action;
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
        Iterable<Scheduler> result = schedulerRepository.findAll();
        List<Scheduler> schedulers = new ArrayList<Scheduler>();

        result.forEach(schedulers::add);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(schedulers.size());
        schedulers.forEach(schedule->{
            actionsManager = (ActionsManager) context.getBean("actionsManager");
            Long startDate = schedule.getTimeStart();

            Calendar startCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            startCalendar.setTimeInMillis(startDate);

            if (schedule.getExecutonCounter() == 0 || schedule.isRepeated()) {
                
                actionsManager.setActionScheduler(schedule);

                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                cal.setTimeInMillis(startDate);

                cal.add(Calendar.MONTH, 1); // Calendar month correction
                cal.add(Calendar.HOUR_OF_DAY, 2); // Calendar time correction

                Calendar currCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                System.out.println(
                        "Action manager scheduled: " + cal.get(Calendar.DAY_OF_MONTH) + "." + cal.get(Calendar.MONTH)
                                + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));

                System.out.println(
                        "Current time: " + currCal.get(Calendar.DAY_OF_MONTH) + "." + currCal.get(Calendar.MONTH) + " "
                                + currCal.get(Calendar.HOUR_OF_DAY) + ":" + currCal.get(Calendar.MINUTE));
                ActionsManager actionsManager = new ActionsManager();
                Long delay = cal.getTimeInMillis() - currCal.getTimeInMillis();
                delay = delay < 0 ? 0 : delay;
                actionsManager.setActionScheduler(schedule);
                scheduledExecutorService.schedule(actionsManager, delay, TimeUnit.MILLISECONDS);

            } else {
                System.out.println("[INFO] Scheduler already executed");

            }
        });
    }
}