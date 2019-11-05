package samt.smajilbasic.deduplicator.Timer;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;

import samt.smajilbasic.deduplicator.entity.Scheduler;
import samt.smajilbasic.deduplicator.repository.SchedulerRepository;
import samt.smajilbasic.deduplicator.worker.ActionsManager;

/**
 * RemindTask
 */
public class ScheduleChecker extends Thread {

    @Autowired
    SchedulerRepository schedulerRepository;

    @Override
    public void run() {
        Iterable<Scheduler> schedulers = schedulerRepository.findAll();

        schedulers.forEach(schedule -> {
            Date startDate = schedule.getDateStart();

            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            Timer timer = new Timer();
            
            timer.schedule(new ActionsManager(schedule,timer), startDate);
        });
    }
}