package samt.smajilbasic.deduplicator.actions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import javax.validation.constraints.NotNull;

/**
 * ActionsManager
 */
@Component
public class ActionsManager implements Runnable {

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private GlobalPathRepository globalPathRepository;
    @Autowired
    private SchedulerRepository schedulerRepository;

    @Autowired
    private ApplicationContext context;

    private Scheduler actionScheduler;

    private List<Action> actions = new ArrayList<>();

    private AuthenticationDetails user;

    public ActionsManager() {
    }

    @Override
    public void run() {
        Logger.getGlobal().log(Level.INFO, "WORKER STARTED");
        if (actionScheduler != null) {
            actions = actionRepository.findActionsFromScheduler(actionScheduler);
        } else {
            Logger.getGlobal().log(Level.WARNING, "Unable to find actions, actionScheduler not set ");
        }

        if (actions != null) {
            if (actions.size() > 0) {
                Logger.getGlobal().log(Level.INFO, "actions size: " + actions.size());
                for (Action action : actions) {
                    Logger.getGlobal().log(Level.INFO, "Executing action: " + action.getActionType());

                    if (!action.isExecuted()) {
                        boolean executed = false;
                        if (action.getActionType().equals(ActionType.MOVE)) {

                            if (this.move(action.getFilePath(), action.getNewFilePath())) {
                                Logger.getGlobal().log(Level.INFO, "File moved successfully from " + action.getFilePath() + " to " + action.getNewFilePath());
                            } else {
                                Logger.getGlobal().log(Level.SEVERE, "Unable to move file: " + action.getFilePath() + " to destination: " + action.getNewFilePath());
                            }
                            executed = true;
                        } else if (action.getActionType().equals(ActionType.DELETE)) {
                            if (this.delete(action.getFilePath())) {
                                Logger.getGlobal().log(Level.INFO, "File deleted successfully: " + action.getFilePath());
                            } else {
                                Logger.getGlobal().log(Level.SEVERE, "Unable to delete file: " + action.getFilePath());
                            }
                            executed = true;
                        } else if (action.getActionType().equals(ActionType.IGNORE)) {
                            if (!globalPathRepository.existsById(action.getFilePath())) {
                                GlobalPath path = new GlobalPath(action.getFilePath(), true);
                                globalPathRepository.save(path);
                            } else {
                                Logger.getGlobal().log(Level.WARNING, "Path already present in global_path: " + action.getFilePath());
                            }

                            if (globalPathRepository.findById(action.getFilePath()).isPresent()) {
                                Logger.getGlobal().log(Level.INFO, "File set on ignored list successfully: " + action.getFilePath());
                            } else {
                                Logger.getGlobal().log(Level.SEVERE, "Unable to ignore file: " + action.getFilePath());
                            }
                            executed = true;
                        } else if (action.getActionType().equals(ActionType.SCAN)) {
                            ScanController controller = (ScanController) context.getBean("scanController");
                            controller.start(null);
                            executed = true;
                        } else {
                            Logger.getGlobal().log(Level.SEVERE, "Invalid action type");
                        }
                        if (executed) {
                            action.setExecuted();
                            actionScheduler.executed();
                        }
                    } else {
                        Logger.getGlobal().log(Level.INFO, "Action " + action.getId() + " already executed - " + action.getActionType());
                    }
                }
                if (actionScheduler != null) {
                    actionScheduler.executed();
                    schedulerRepository.save(actionScheduler);
                }

            } else {
                Logger.getGlobal().log(Level.WARNING, "No action to execute set");

            }
        } else {
            Logger.getGlobal().log(Level.WARNING, "Action is null");
        }
        Logger.getGlobal().log(Level.INFO, "Actions manager finished");

    }

    private boolean move(String oldPath, String newPath) {
        try {
            if (oldPath != null) {
                if (newPath != null) {
                    Path old = Paths.get(oldPath);
                    Path newP = Paths.get(newPath + old.getFileName());
                    Files.move(old, newP, StandardCopyOption.REPLACE_EXISTING);
                    return true;
                } else {
                    Logger.getGlobal().log(Level.SEVERE, "newPath is null");
                    return false;
                }
            } else {
                Logger.getGlobal().log(Level.SEVERE, "oldPath is null");
                return false;
            }
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "IOException while moving file from " + oldPath + " to " + newPath);
            return false;
        }
    }

    private boolean delete(String path) {
        try {
            return Files.deleteIfExists(Paths.get(path));
        } catch (Exception ex) {
            Logger.getGlobal().log(Level.SEVERE, "Unable to delete file: " + path + " reason: " + ex.getMessage());
            return false;
        }
    }

    /**
     * @param actionScheduler the actionScheduler to set
     */
    public void setActionScheduler(Scheduler actionScheduler) {
        this.actionScheduler = actionScheduler;
    }

    public Scheduler getActionScheduler() {
        return this.actionScheduler;
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

}