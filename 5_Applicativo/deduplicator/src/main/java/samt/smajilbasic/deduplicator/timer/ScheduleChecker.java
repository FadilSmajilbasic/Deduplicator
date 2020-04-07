package samt.smajilbasic.deduplicator.timer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import samt.smajilbasic.deduplicator.entity.Scheduler;
import samt.smajilbasic.deduplicator.repository.SchedulerRepository;
import samt.smajilbasic.deduplicator.actions.ActionsManager;

/**
 * La classe ScheduleChecker controlla i scheduler inseriti nel database e se
 * possibile esegue lo scheduler e le sue azioni. Usa
 * l'annotazione @{@link Component} per indicare a Sping che alla creazione
 * dell'oggetto ScheduleChecker bisogna anche istanziare gli attributi con
 * l'annotazione @{@link Autowired}.
 */
@Component
public class ScheduleChecker extends Thread {

    /**
     * L'attributo scheduleRepository permette l'interfacciamento con la tabella
     * Scheduler del database.
     */
    @Autowired
    SchedulerRepository schedulerRepository;

    /**
     * L'attributo context definisce il contesto dell'applicazione. Viene usato per
     * creare un nuovo actionsManager.
     */
    @Autowired
    ApplicationContext context;

    /**
     * L'attributo DEFAULT_TERMINATION_TIMEOUT definisce il timeout per l'esecuzione
     * del applciationManager.
     */
    private static final Integer DEFAULT_TERMINATION_TIMEOUT = 1800;

    /**
     * Metodo costruttore vuoto.
     */
    public ScheduleChecker() {
    }

    public void run() {
        Iterable<Scheduler> result = schedulerRepository.findAll();
        List<Scheduler> schedulers = new ArrayList<Scheduler>();
        result.forEach(schedulers::add);
        ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1);
        schedulers.forEach(schedule -> {
            try {
                BeanDefinitionRegistry factory = (BeanDefinitionRegistry) context.getAutowireCapableBeanFactory();
                ((DefaultListableBeanFactory) factory).destroySingleton("scheduleChecker");
                ActionsManager actionsManager = (ActionsManager) context.getBean("actionsManager");
                Long startDate = schedule.getTimeStart();

                Calendar startCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                startCalendar.setTimeInMillis(startDate);
                if (!schedule.isScheduled()) {
                    if (schedule.getExecutionCounter() == 0 || schedule.isRepeated()) {

                        actionsManager.setActionScheduler(schedule);

                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
                        long delay = startCalendar.getTimeInMillis() - Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
                        Logger.getGlobal().log(Level.INFO, "Action manager of schedule " + schedule.getSchedulerId() + " scheduled to execute on the " + dateFormat.format(startCalendar.getTime()) + " which is in exactly " + delay + "ms from now and it " + (schedule.isRepeated() ? "is" : "isn't") + " repeated");

                        delay = delay < 0 ? 0 : delay;

                        if (schedule.isRepeated()) {

                            long difference = 0;
                            Calendar nextDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

                            long period;
                            if (schedule.getMonthly() != null) {
                                nextDate.set(startCalendar.get(Calendar.YEAR) + 1900, startCalendar.get(Calendar.MONTH),
                                    schedule.getMonthly());
                                difference = Math.abs(nextDate.getTimeInMillis() - startCalendar.getTimeInMillis());
                                Logger.getGlobal().log(Level.INFO, "Schedule is monthly");
                                period = YearMonth.of(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH)).lengthOfMonth();
                            } else if (schedule.getWeekly() != null) {
                                Logger.getGlobal().log(Level.INFO, "Schedule is weekly");
                                difference = Math.abs(schedule.getWeekly() - startCalendar.get(Calendar.DAY_OF_WEEK));
                                period = 7;
                            } else {
                                Logger.getGlobal().log(Level.INFO, "Schedule is daily");
                                period = 1;
                            }
                            long initialDelay = startCalendar.getTimeInMillis() + difference;
                            if (startCalendar.getTime().before(new Date())) {
                                initialDelay = 0;
                                Logger.getGlobal().log(Level.INFO, "Schedule executing now, is scheduled before now");
                            }

                            scheduledExecutor.scheduleAtFixedRate(actionsManager, initialDelay, period, TimeUnit.DAYS);
                            schedule.setScheduled(true);
                            schedulerRepository.save(schedule);

                        } else {
                            scheduledExecutor.schedule(actionsManager, delay, TimeUnit.MILLISECONDS);
                            schedule.setScheduled(true);
                            schedulerRepository.save(schedule);
                        }
                    } else {
                        Logger.getGlobal().log(Level.INFO, "Scheduler already executed");
                    }
                } else {
                    Logger.getGlobal().log(Level.INFO, "Scheduler already scheduled");
                }
            } catch (Exception ex) {
                Logger.getGlobal().log(Level.SEVERE, "An exception occurred: " + ex.getStackTrace()[0]);
                ex.printStackTrace(System.out);
            }
        });
        try {
            scheduledExecutor.awaitTermination(DEFAULT_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
        } catch (
            Exception ex) {
            Logger.getGlobal().log(Level.SEVERE, "An execution exception: " + ex.getStackTrace()[0]);
            ex.printStackTrace(System.out);
        }
    }
}