package samt.smajilbasic.deduplicator;

/**
 * MainController
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import samt.smajilbasic.deduplicator.entities.GlobalPath;
import samt.smajilbasic.deduplicator.repository.GlobalPathRepository;


@Controller
@RequestMapping(path = "/scan") 
public class MainController {
    @Autowired 
    private GlobalPathRepository pathRepository;

    @GetMapping(path = "/getPaths")
    public @ResponseBody Iterable<GlobalPath> getAllUsers() {
        // This returns a JSON or XML with the users
        return pathRepository.findAll();
    }
}
