package samt.smajilbasic.deduplicator.controller;

import samt.smajilbasic.deduplicator.exception.*;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import samt.smajilbasic.deduplicator.entity.GlobalPath;
import samt.smajilbasic.deduplicator.repository.GlobalPathRepository;

/**
 * PathController
 */

@RestController
@RequestMapping(path = "/path")
public class PathController {

    @Autowired
    GlobalPathRepository gpr;

    @GetMapping(value = "/get")
    public @ResponseBody Iterable<GlobalPath> getPaths() {
        return gpr.findAll();
    }

    @GetMapping(value = "/get/{path}")
    public @ResponseBody GlobalPath getValueByPath(@PathVariable String path) {
        return gpr.findById(path.replaceAll("&#47;", File.separator).trim()).get();
    }

    @PostMapping(value = "/put")
    public @ResponseBody GlobalPath insert(@RequestParam String path, @RequestParam String ignoreFile, HttpServletResponse response) throws IOException {
        gpr.save(new GlobalPath(path,(ignoreFile.equals("true"))));
        return getValueByPath(path);
    }

    @PostMapping(value="/remove")
    public @ResponseBody GlobalPath remove(@RequestParam String path) {
        
        GlobalPath entry = gpr.findById(path.replaceAll("&#47;", File.separator).trim()).get();
        if(entry != null)
            gpr.delete(entry);
        return entry;
    }
    @ExceptionHandler({ PathException.class })
    public @ResponseBody ErrorMessage invalidPathException() {
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid path");
    }

    @ExceptionHandler({ DuplicatePathException.class })
    public @ResponseBody ErrorMessage duplicateException() {
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "Path already set");
    }
}