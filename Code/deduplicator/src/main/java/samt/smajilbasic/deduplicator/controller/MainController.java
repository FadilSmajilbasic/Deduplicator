package samt.smajilbasic.deduplicator.controller;

import javax.validation.Valid;

/**
 * MainController
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import samt.smajilbasic.deduplicator.entity.*;
import samt.smajilbasic.deduplicator.repository.*;



@Controller
@RequestMapping(path = "/") 
public class MainController {

    @Autowired 
    private ActionTypeRepository actionTypeRepository;

    @GetMapping(value="/actionType")
    public @ResponseBody Iterable<ActionType> getActionTypes() {
        return actionTypeRepository.findAll();
    }
    @PostMapping(value="/actionType")
    public ActionType createAction(@Valid @RequestBody ActionType actionType) {
        return actionTypeRepository.save(actionType);
    }

    @GetMapping(value="/getFile")
    public @ResponseBody Iterable<ActionType> getFile() {
        return actionTypeRepository.findAll();
    }
    
}
