package samt.smajilbasic.deduplicator.timer;

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
    public ScheduleChecker(){}

    public void run() {

        Iterable<Scheduler> result = schedulerRepository.findAll();
        List<Scheduler> schedulers = new ArrayList<Scheduler>();
        result.forEach(schedulers::add);
        ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1); // pool di
                                                                                                            // esecuzione
                                                                                                            // delle
                                                                                                            // thread
        schedulers.forEach(schedule -> {
            try {
                BeanDefinitionRegistry factory = (BeanDefinitionRegistry) context.getAutowireCapableBeanFactory();
                ((DefaultListableBeanFactory) factory).destroySingleton("scheduleChecker");
                ActionsManager actionsManager = (ActionsManager) context.getBean("actionsManager");
                Long startDate = schedule.getTimeStart();

                Calendar startCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                Calendar currCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                startCalendar.setTimeInMillis(startDate);

                if (schedule.getExecutionCounter() == 0 || schedule.isRepeated()) {
                    actionsManager.setActionScheduler(schedule);
                    System.out.println("inside action manager: " + (actionsManager.getActionScheduler() == null));

                    System.out.println("Action manager scheduled: " + startCalendar.get(Calendar.DAY_OF_MONTH) + "."
                        + startCalendar.get(Calendar.MONTH) + " " + startCalendar.get(Calendar.HOUR_OF_DAY) + ":"
                        + startCalendar.get(Calendar.MINUTE));

                    System.out.println(
                        "Current time: " + currCal.get(Calendar.DAY_OF_MONTH) + "." + currCal.get(Calendar.MONTH) + " "
                            + currCal.get(Calendar.HOUR_OF_DAY) + ":" + currCal.get(Calendar.MINUTE));

                    long delay = startCalendar.getTimeInMillis() - currCal.getTimeInMillis();
                    delay = delay < 0 ? 0 : delay;
                    System.out.println("delay: " + delay + " is repeated: " + schedule.isRepeated());

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
                    } else {
                        Logger.getGlobal().log(Level.INFO,"scheduling one off scheduler");
                        scheduledExecutor.schedule(actionsManager, delay, TimeUnit.MILLISECONDS);
                    }
                } else {
                    Logger.getGlobal().log(Level.INFO,"Scheduler already executed" );
                }
            }catch (Exception ex){
                Logger.getGlobal().log(Level.SEVERE,"An exception occurred: " + ex.getMessage());
            }
        });
        try {
            scheduledExecutor.awaitTermination(DEFAULT_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception ex) {
            Logger.getGlobal().log(Level.SEVERE,"An execution exeption: " + ex.getMessage());

            System.out.println("execution exeption: " + ex.getMessage());
        }
    }
}