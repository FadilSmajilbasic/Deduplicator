package samt.smajilbasic.deduplicator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
import samt.smajilbasic.deduplicator.worker.ActionsManager;

@RestController
@RequestMapping(path = "/action")
public class ActionController {

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    AuthenticationDetailsRepository adr;

    @GetMapping()
    public @ResponseBody Iterable<Action> getActions() {
        return actionRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public @ResponseBody Object getActions(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if (intId != null && actionRepository.existsById(intId))
            return actionRepository.findById(intId).get();
        else
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid action id");
    }

    @PostMapping("/execute/")
    public @ResponseBody Message executeActions() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUser = authentication.getName();

        AuthenticationDetails internalUser = adr.findById(authenticatedUser).get();

        new ActionsManager(actionRepository.findActionsFromUser(internalUser), internalUser);
        return new Message(HttpStatus.OK);
    }

    @PutMapping()
    public @ResponseBody Object addAction(@RequestParam String type, @RequestParam String path,
            @RequestParam String newPath, @RequestParam(required = false) String user) {

        if (adr.existsById(user)) {
            AuthenticationDetails internalUser = adr.findById(user).get();
            type = getType(type);
            if (type == null)
                return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Action type invalid");

            if (type.equals(ActionType.MOVE) && (newPath.trim().equals("") || newPath == null))
                return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "New path not set while having type = MOVE");

            if (Validator.getPathType(path) != PathType.File)
                return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "File path invalid");

            if (Validator.getPathType(newPath) != PathType.Directory)
                return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "New path is invalid or not a directory");

            Action action = new Action(type, path, newPath, internalUser);
            actionRepository.save(action);

            return action;
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "User invalid");
        }
    }

    private String getType(String type) {
        if (type.equalsIgnoreCase(ActionType.DELETE) || type.equalsIgnoreCase(ActionType.MOVE)
                || type.equalsIgnoreCase(ActionType.DELETE) || type.equalsIgnoreCase(ActionType.IGNORE)
                || type.equalsIgnoreCase(ActionType.NONE))
            return type.toUpperCase();
        else
            return null;
    }

}
