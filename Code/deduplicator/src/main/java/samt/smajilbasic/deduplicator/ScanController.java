package samt.smajilbasic.deduplicator;


/**
 * ScanController
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;




@Controller
@RequestMapping(path = "/scan") 
public class ScanController {


    @GetMapping(value="/getProgress")
    public @ResponseBody String getFile() {
        return "Progress";
    }
    
}
