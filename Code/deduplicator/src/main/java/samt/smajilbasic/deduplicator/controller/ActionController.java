package samt.smajilbasic.deduplicator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * ScanController
 */

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import samt.smajilbasic.deduplicator.ActionType;
import samt.smajilbasic.deduplicator.PathType;
import samt.smajilbasic.deduplicator.Validator;
import samt.smajilbasic.deduplicator.entity.Action;
import samt.smajilbasic.deduplicator.exception.Message;
import samt.smajilbasic.deduplicator.repository.ActionRepository;
import samt.smajilbasic.deduplicator.repository.ReportRepository;

@RestController
@RequestMapping(path = "/action")
public class ActionController {

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    ReportRepository reportRepository;

    @GetMapping()
    public @ResponseBody Iterable<Action> getFiles() {
        return actionRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public @ResponseBody Object getFilesFromReport(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if (intId != null && reportRepository.existsById(intId))
            return reportRepository.findById(intId).get().getFile();
        else
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid report id");
    }

    @PutMapping()
    public @ResponseBody Object addAction(@RequestParam String type, @RequestParam String path,@RequestParam String newPath ){
        

        type = getType(type);
        if (type == null) 
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR,"Action type invalid");
        
        if(type.equals(ActionType.MOVE) && (newPath.trim().equals("") || newPath == null))
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR,"New path not set while having type = MOVE");
        
        if(Validator.getPathType(path) != PathType.File)
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR,"File path invalid");

        if(Validator.getPathType(newPath) != PathType.Directory )
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR,"New path is invalid or not a directory");


        Action action = new Action(type, path, newPath);
        actionRepository.save(action);

        return action;
    }

    private String getType(String type){
        if(type.toUpperCase().equals(ActionType.DELETE) || type.toUpperCase().equals(ActionType.MOVE) || type.toUpperCase().equals(ActionType.DELETE) || type.toUpperCase().equals(ActionType.IGNORE) || type.toUpperCase().equals(ActionType.NONE))
            return type.toUpperCase();
        else
            return null;
    }
    

}
