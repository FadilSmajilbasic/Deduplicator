package samt.smajilbasic.deduplicator.worker;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;

import samt.smajilbasic.deduplicator.ActionType;
import samt.smajilbasic.deduplicator.entity.Action;
import samt.smajilbasic.deduplicator.repository.ActionRepository;

/**
 * ActionsManager
 */
public class ActionsManager extends Thread {

    @Autowired
    ActionRepository actionRepository;

    @Override
    public void run() {
        Iterable<Action> actions = actionRepository.findAll();

        for (Action action : actions) {
            if (action.getActionType() == ActionType.MOVE) {
                if(this.move(action.getFilePath(), action.getNewFilePath())){
                    System.out.println("[INFO] File moved succesfully: " + action.getFilePath());
                }else{
                    System.out.println("[ERROR] Unable to move file: " + action.getFilePath() + " to destination: " + action.getNewFilePath());
                }
                
            }else if (action.getActionType() == ActionType.DELETE){
                if(this.delete(action.getFilePath())){
                    System.out.println("[INFO] File deleted succesfully: " + action.getFilePath());
                }else{
                    
                }
            }
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
        try{
            return Files.deleteIfExists(Paths.get(path));
        }catch(Exception ex){
            System.out.println("[ERROR] Unable to delete file: " + path + " reason: " + ex.getMessage());
            return false;
        }
    }
}