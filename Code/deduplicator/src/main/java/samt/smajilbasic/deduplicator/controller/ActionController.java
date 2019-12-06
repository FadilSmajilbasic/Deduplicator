package samt.smajilbasic.deduplicator.controller;

import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;

/**
 * ScanController
 */

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import samt.smajilbasic.deduplicator.ActionType;
import samt.smajilbasic.deduplicator.PathType;
import samt.smajilbasic.deduplicator.Validator;
import samt.smajilbasic.deduplicator.entity.Action;
import samt.smajilbasic.deduplicator.entity.AuthenticationDetails;
import samt.smajilbasic.deduplicator.exception.Message;
import samt.smajilbasic.deduplicator.repository.ActionRepository;
import samt.smajilbasic.deduplicator.repository.AuthenticationDetailsRepository;
import samt.smajilbasic.deduplicator.repository.SchedulerRepository;
import samt.smajilbasic.deduplicator.timer.ScheduleChecker;
import samt.smajilbasic.deduplicator.worker.ActionsManager;

@RestController
@RequestMapping(path = "/action")
public class ActionController {

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    SchedulerRepository schedulerRepository;

    @Autowired
    AuthenticationDetailsRepository adr;

    @Autowired
    ScheduleChecker checker;
    @Autowired
    ApplicationContext context;

    @GetMapping("/")
    public @ResponseBody Iterable<Action> getActions() {
        return actionRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public @ResponseBody Object getActions(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if (intId != null && actionRepository.existsById(intId))
            return actionRepository.findById(intId).get();
        else
            return new Message(HttpStatus.NOT_FOUND, "Invalid action id");
    }

    @PostMapping("/execute/all")
    public @ResponseBody Object executeActions() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUser = authentication.getName();

        AuthenticationDetails internalUser = adr.findById(authenticatedUser).get();

        ActionsManager manager = (ActionsManager) context.getBean("actionsManager");
        manager.setActions(actionRepository.findActionsFromUser(internalUser));
        manager.setUser(internalUser);
        Timer timer = new Timer();
        timer.schedule(manager, 0);

        return actionRepository.findActionsFromUser(internalUser);
    }

    @PutMapping("/")
    public @ResponseBody Object addAction(@RequestParam String type, @RequestParam(required = false) String path,
            @RequestParam(required = false) String newPath, @RequestParam(required = true) String scheduler) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();

        Integer schedulerIdInt = Validator.isInt(scheduler);
        if (schedulerRepository.existsById(schedulerIdInt != null ? schedulerIdInt : -1)) {
            type = getType(type);
            if (type == null)
                return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Action type invalid");

            if (type.equals(ActionType.MOVE) && (newPath.trim().equalsIgnoreCase("") || newPath == null))
                return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "New path not set while having type = MOVE");

            if (Validator.getPathType(path) != PathType.File && !type.equals(ActionType.SCAN))
                return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "File path invalid");

            if (Validator.getPathType(newPath) != PathType.Directory && !type.equals(ActionType.SCAN))
                return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "New path is invalid or not a directory");

            Action action = new Action(type, path, newPath, adr.findById(currentUser).get(),
                    schedulerRepository.findById(schedulerIdInt).get());
            actionRepository.save(action);
            checker.check();
            return action;
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Scheduler id invalid");
        }

    }

    private String getType(String type) {
        if (type.equalsIgnoreCase(ActionType.DELETE) || type.equalsIgnoreCase(ActionType.MOVE)
                || type.equalsIgnoreCase(ActionType.DELETE) || type.equalsIgnoreCase(ActionType.IGNORE)
                || type.equalsIgnoreCase(ActionType.NONE) || type.equalsIgnoreCase(ActionType.SCAN))
            return type.toUpperCase();
        else
            return null;
    }
    
    @DeleteMapping("/{id}")
    public @ResponseBody Object deleteAction(@PathVariable String id){
        Integer intId = Validator.isInt(id);
        if(intId != null){
            if(actionRepository.existsById(intId)){
                Action action = actionRepository.findById(intId).get();
                actionRepository.delete(action);
                return action;
            }else{
                return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to find action with id: " + id);
            }
        }else{
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid parameter: " + id );
        }
    }

    @PostMapping("/path")
    public @ResponseBody boolean checkPath(@RequestParam String path){
        if(Validator.getPathType(path).equals(PathType.Directory)){
            return true;
        }else{
            return false;
        }
    }

}
