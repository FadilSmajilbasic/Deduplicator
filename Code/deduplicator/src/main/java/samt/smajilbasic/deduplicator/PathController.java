package samt.smajilbasic.deduplicator;

import samt.smajilbasic.deduplicator.exception.PathException;
import samt.smajilbasic.deduplicator.exception.DuplicatePathException;
import samt.smajilbasic.deduplicator.exception.ErrorMessage;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import samt.smajilbasic.deduplicator.entities.GlobalPath;
import samt.smajilbasic.deduplicator.repository.GlobalPathRepository;

/**
 * PathController
 */

@Controller
@RequestMapping(path = "/path") 
public class PathController {

    @Autowired
    GlobalPathRepository gpr;

    @GetMapping(value="/get")
    public @ResponseBody Iterable<GlobalPath> getPaths() {
        return gpr.findAll();
    }

    @GetMapping(value="/get/{path}")
    public @ResponseBody GlobalPath getValueByPath(@PathVariable String path) {
        return gpr.findById(path).get();
    }
    
    @PostMapping(value="/put")
    public ModelAndView insert(@RequestParam String path,@RequestParam String ignoreFile) {
        gpr.save(new GlobalPath(path,(ignoreFile.equals("true"))));
        return new ModelAndView("redirect:/get/"+path);
    }

    @PostMapping(value="/remove")
    public @ResponseBody GlobalPath remove(@RequestParam String path) {

        GlobalPath entry = gpr.findById(path).get();
        gpr.delete(entry);

        return entry;
    }

    @ExceptionHandler({ PathException.class })
    public @ResponseBody ErrorMessage invalidPathException() {
        return new ErrorMessage(HttpStatus.NOT_ACCEPTABLE, "Invalid path");
    }

    @ExceptionHandler({ DuplicatePathException.class })
    public @ResponseBody ErrorMessage duplicateException() {
        return new ErrorMessage(HttpStatus.NOT_ACCEPTABLE, "Path already set");
    }
}