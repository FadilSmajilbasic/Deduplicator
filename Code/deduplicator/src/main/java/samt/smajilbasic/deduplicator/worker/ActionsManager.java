package samt.smajilbasic.deduplicator.worker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import samt.smajilbasic.deduplicator.ActionType;
import samt.smajilbasic.deduplicator.entity.Action;
import samt.smajilbasic.deduplicator.entity.GlobalPath;
import samt.smajilbasic.deduplicator.entity.Scheduler;
import samt.smajilbasic.deduplicator.repository.ActionRepository;
import samt.smajilbasic.deduplicator.repository.GlobalPathRepository;

/**
 * ActionsManager
 */
public class ActionsManager extends Thread {

    @Autowired
    ActionRepository actionRepository;
    
    @Autowired
    GlobalPathRepository globalPathRepository;
    

    Scheduler actionScheduler;

    public ActionsManager(Scheduler actionScheduler) {
        setActionScheduler(actionScheduler);
    }

    @Override
    public void run() {
        if (actionScheduler != null) {
            List<Action> actions = actionRepository.findActionsFromScheduler(actionScheduler);

            for (Action action : actions) {
                if (!action.isExecuted()) {
                    boolean executed = false;                    
                    if (action.getActionType() == ActionType.MOVE) {
                        
                        if (this.move(action.getFilePath(), action.getNewFilePath())) {
                            System.out.println("[INFO] File moved succesfully: " + action.getFilePath());
                            executed = true;
                        } else {
                            System.out.println("[ERROR] Unable to move file: " + action.getFilePath()
                                    + " to destination: " + action.getNewFilePath());
                        }
                    } else if (action.getActionType() == ActionType.DELETE) {
                        if (this.delete(action.getFilePath())) {
                            System.out.println("[INFO] File deleted succesfully: " + action.getFilePath());
                            executed = true;
                        } else {
                            System.out.println("[ERROR] Unable to delete file: " + action.getFilePath());
                        }
                    }else if(action.getActionType() == ActionType.IGNORE){
                        GlobalPath path = new GlobalPath(action.getFilePath(),true);
                        globalPathRepository.save(path);

                        if (globalPathRepository.findById(path.getPath()).get() != null) {
                            System.out.println("[INFO] File set on ignored list succesfully: " + action.getFilePath());
                            executed = true;
                        } else {
                            System.out.println("[ERROR] Unable to delete file: " + action.getFilePath());
                        }
                    }

                    action.setExecuted(executed);
                }
            }
        } else {
            System.out.println("[ERROR] Unable to find actions, actionScheduler not set");
        }
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

}