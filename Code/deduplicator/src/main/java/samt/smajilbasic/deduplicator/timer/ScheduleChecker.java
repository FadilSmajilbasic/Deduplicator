package samt.smajilbasic.deduplicator.timer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import samt.smajilbasic.deduplicator.entity.Scheduler;
import samt.smajilbasic.deduplicator.repository.SchedulerRepository;
import samt.smajilbasic.deduplicator.worker.ActionsManager;

/**
 * RemindTask
 */
@Component
public class ScheduleChecker extends Thread {

    @Autowired
    SchedulerRepository schedulerRepository;

    @Autowired
    ActionsManager actionsManager;

    @Autowired
    ApplicationContext context;

    /**
     * Default timeout for the scanning thread pool given in seconds
     */
    private static final Integer DEFAULT_TERMINATION_TIMEOUT = 1800;

    public ScheduleChecker() {
        super();
    }

    public void run() {

        Iterable<Scheduler> result = schedulerRepository.findAll();
        List<Scheduler> schedulers = new ArrayList<Scheduler>();

        result.forEach(schedulers::add);

        ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(10);

        schedulers.forEach(schedule -> {
            BeanDefinitionRegistry factory = (BeanDefinitionRegistry) context.getAutowireCapableBeanFactory();
            ((DefaultListableBeanFactory) factory).destroySingleton("scheduleChecker");
            actionsManager = (ActionsManager) context.getBean("actionsManager");
            Long startDate = schedule.getTimeStart();

            Calendar startCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            startCalendar.setTimeInMillis(startDate);

            startCalendar.add(Calendar.MONTH, 1); // Calendar month correction
            startCalendar.add(Calendar.HOUR_OF_DAY, 2); // Calendar time correction

            if (schedule.getExecutonCounter() == 0 || schedule.isRepeated()) {

                actionsManager.setActionScheduler(schedule);

                Calendar currCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                System.out.println("Action manager scheduled: " + startCalendar.get(Calendar.DAY_OF_MONTH) + "."
                        + startCalendar.get(Calendar.MONTH) + " " + startCalendar.get(Calendar.HOUR_OF_DAY) + ":"
                        + startCalendar.get(Calendar.MINUTE));

                System.out.println(
                        "Current time: " + currCal.get(Calendar.DAY_OF_MONTH) + "." + currCal.get(Calendar.MONTH) + " "
                                + currCal.get(Calendar.HOUR_OF_DAY) + ":" + currCal.get(Calendar.MINUTE));
                ActionsManager actionsManager = new ActionsManager();
                Long delay = startCalendar.getTimeInMillis() - currCal.getTimeInMillis();
                delay = delay < 0 ? 0 : delay;
                actionsManager.setActionScheduler(schedule);
                
                if (schedule.isRepeated()) {

                    long difference = 0;
                    Calendar nextDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

                    boolean passed = false;
                    if (startCalendar.getTime().before(new Date())) {
                        passed = true;
                    }
                    boolean monthly = false;
                    if (schedule.getMonthly() != null) {
                        nextDate.set(startCalendar.get(Calendar.YEAR) + 1900, startCalendar.get(Calendar.MONTH),
                                schedule.getMonthly());
                        difference = Math.abs(nextDate.getTimeInMillis() - startCalendar.getTimeInMillis());
                        monthly = true;
                    } else {
                        difference = Math.abs(schedule.getWeekly() - startCalendar.get(Calendar.DAY_OF_WEEK));
                    }
                    long initialDelay = startCalendar.getTimeInMillis() + difference;
                    if (passed)
                        initialDelay = 0;

                    scheduledExecutor.scheduleAtFixedRate(actionsManager, initialDelay, monthly ? 30 : 7,
                            TimeUnit.DAYS);
                }else{
                    scheduledExecutor.schedule(actionsManager, delay, TimeUnit.MILLISECONDS);
                }

            } else {
                System.out.println("[INFO] Scheduler already executed");

            }
        });
        try {
            scheduledExecutor.awaitTermination(DEFAULT_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception ex) {
            System.out.println("execuztion exeption: " + ex.getMessage());
        }
    }
}