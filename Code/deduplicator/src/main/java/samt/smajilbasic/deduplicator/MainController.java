package samt.smajilbasic.deduplicator;

/**
 * MainController
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import samt.smajilbasic.deduplicator.entities.*;
import samt.smajilbasic.deduplicator.repository.*;



@Controller
@RequestMapping(path = "/main") 
public class MainController {
    @Autowired 
    private GlobalPathRepository pathRepository;
    
    @Autowired 
    private ActionTypeRepository actionTypeRepository;


    @GetMapping(path = "/getGlobalPath")
    public @ResponseBody Iterable<GlobalPath> getGlobalPath() {
        return pathRepository.findAll();
    }

    @GetMapping(value="/getActionType")
    public @ResponseBody Iterable<ActionType> getActionTypes() {
        return actionTypeRepository.findAll();
    }
    @GetMapping(value="/getAction")
    public @ResponseBody Iterable<ActionType> getAction() {
        return actionTypeRepository.findAll();
    }

    @GetMapping(value="/getFile")
    public @ResponseBody Iterable<ActionType> getFile() {
        return actionTypeRepository.findAll();
    }
    
}
