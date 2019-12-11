package samt.smajilbasic.deduplicator.worker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import samt.smajilbasic.deduplicator.ActionType;
import samt.smajilbasic.deduplicator.controller.ScanController;
import samt.smajilbasic.deduplicator.entity.Action;
import samt.smajilbasic.deduplicator.entity.AuthenticationDetails;
import samt.smajilbasic.deduplicator.entity.GlobalPath;
import samt.smajilbasic.deduplicator.entity.Scheduler;
import samt.smajilbasic.deduplicator.repository.ActionRepository;
import samt.smajilbasic.deduplicator.repository.GlobalPathRepository;
import samt.smajilbasic.deduplicator.repository.SchedulerRepository;

/**
 * ActionsManager
 */
@Component
public class ActionsManager extends TimerTask {

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    GlobalPathRepository globalPathRepository;
    @Autowired
    SchedulerRepository schedulerRepository;

    @Autowired
    ApplicationContext context;

    Scheduler actionScheduler;

    Timer timer;

    List<Action> actions;

    AuthenticationDetails user;

    public ActionsManager() {
        super();
    }

    @Override
    public void run() {
        System.out.println("WORKER STARTED");

        if (actionScheduler != null) {
            actions = actionRepository.findActionsFromScheduler(actionScheduler);
        } else {
            System.out.println("[INFO] Unable to find actions, actionScheduler not set");
        }
        if (actions != null) {
            for (Action action : actions) {
                System.out.println("executing action: " + action.getActionType());

                if (!action.isExecuted()) {
                    boolean executed = false;
                    if (action.getActionType().equals(ActionType.MOVE)) {

                        if (this.move(action.getFilePath(), action.getNewFilePath())) {
                            System.out.println("[INFO] File moved succesfully: " + action.getFilePath());
                            executed = true;
                        } else {
                            System.out.println("[ERROR] Unable to move file: " + action.getFilePath()
                                    + " to destination: " + action.getNewFilePath());
                        }
                    } else if (action.getActionType().equals(ActionType.DELETE)) {
                        if (this.delete(action.getFilePath())) {
                            System.out.println("[INFO] File deleted succesfully: " + action.getFilePath());
                            executed = true;
                        } else {
                            System.out.println("[ERROR] Unable to delete file: " + action.getFilePath());
                        }
                    } else if (action.getActionType().equals(ActionType.IGNORE)) {
                        GlobalPath path = new GlobalPath(action.getFilePath(), true);
                        globalPathRepository.save(path);

                        if (globalPathRepository.findById(path.getPath()).get() != null) {
                            System.out.println("[INFO] File set on ignored list succesfully: " + action.getFilePath());
                            executed = true;
                        } else {
                            System.out.println("[ERROR] Unable to delete file: " + action.getFilePath());
                        }
                    } else if (action.getActionType().equals(ActionType.SCAN)) {
                        System.out.println("scan");
                        ScanController controller = (ScanController) context.getBean("scanController");
                        controller.start(null);
                        timer.cancel();

                        long difference = 0;
                        Calendar nextDate = Calendar.getInstance();

                        Calendar startCalendar = Calendar.getInstance();

                        if (actionScheduler != null) {
                            System.out.println("sched");
                            if (actionScheduler.isRepeated()) {
                                if (actionScheduler.getMonthly() != null) {
                                    nextDate.set(startCalendar.get(Calendar.YEAR) + 1900,
                                            startCalendar.get(Calendar.MONTH), actionScheduler.getMonthly());
                                    // get time in
                                    // milliseconds until
                                    // next execution
                                    difference = Math.abs(nextDate.getTimeInMillis() - startCalendar.getTimeInMillis());

                                } else if (actionScheduler.getWeekly() != null) {
                                    difference = Math
                                            .abs(actionScheduler.getWeekly() - startCalendar.get(Calendar.DAY_OF_WEEK));
                                }
                                ActionsManager actionsManager = (ActionsManager) context.getBean("actionsManager");
                                actionsManager.setActionScheduler(actionScheduler);
                                actionsManager.setTimer(timer);

                                timer.schedule(actionsManager,
                                        new Date(startCalendar.getTime().getTime() + difference));

                            }
                        }
                        executed = true;

                    }

                    action.setExecuted();

                    actionScheduler.executed();

                    actionRepository.save(action);
                    schedulerRepository.save(actionScheduler);
                }else{
                    System.out.println("[INFO] Action "+action.getId()+" already executed ");
                }
            }
        } else {
            System.out.println("[ERROR] No action to execute set");
        }

        System.out.println("finished working");
    }

    private boolean move(String oldPath, String newPath) {
        try {
            Files.move(Paths.get(oldPath), Paths.get(newPath), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean delete(String path) {
        try {
            return Files.deleteIfExists(Paths.get(path));
        } catch (Exception ex) {
            System.out.println("[ERROR] Unable to delete file: " + path + " reason: " + ex.getMessage());
            return false;
        }
    }

    /**
     * @param actionScheduler the actionScheduler to set
     */
    public void setActionScheduler(Scheduler actionScheduler) {
        this.actionScheduler = actionScheduler;
    }

    /**
     * @param actions the actions to set
     */
    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    /**
     * @return the actions
     */
    public List<Action> getActions() {
        return actions;
    }

    /**
     * @param user the user to set
     */
    public void setUser(AuthenticationDetails user) {
        this.user = user;
    }

    /**
     * @return the user
     */
    public AuthenticationDetails getUser() {
        return user;
    }

    /**
     * @param timer the timer to set
     */
    public void setTimer(Timer timer) {
        this.timer = timer;
    }

}