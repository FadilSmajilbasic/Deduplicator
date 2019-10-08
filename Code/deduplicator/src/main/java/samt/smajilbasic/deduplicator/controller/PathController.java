package samt.smajilbasic.deduplicator.controller;

import samt.smajilbasic.deduplicator.exception.*;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

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

import samt.smajilbasic.deduplicator.PathType;
import samt.smajilbasic.deduplicator.Validator;
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
        try {
            if (Validator.getPathType(path) != PathType.Invalid) {
                return gpr.findById(path.replaceAll("&#47;", File.separator).trim()).get();
            } else {
                throw new NoSuchElementException();
            }
        } catch (NoSuchElementException nsee) {
            throw new PathException("Invalid path: " + path + " --> " + path.replaceAll("&#47;", File.separator).trim() );
        }
    }

    @PostMapping(value = "/put")
    public @ResponseBody GlobalPath insert(@RequestParam String path, @RequestParam String ignoreFile)
            throws IOException {
        try {
            if (Validator.getPathType(path) != PathType.Invalid) {
                gpr.save(new GlobalPath(path.replaceAll("&#47;", File.separator).trim(), (ignoreFile.equals("true"))));
                return getValueByPath(path);
            } else {
                throw new NoSuchElementException();
            }
        } catch (NoSuchElementException nsee) {
            throw new PathException("Invalid path: " + path + " --> " + path.replaceAll("&#47;", File.separator).trim() );
        }
    }

    @PostMapping(value = "/remove")
    public @ResponseBody GlobalPath remove(@RequestParam String path) {

        GlobalPath entry = gpr.findById(path.replaceAll("&#47;", File.separator).trim()).get();
        if (entry != null)
            gpr.delete(entry);
        return entry;
    }

    @ExceptionHandler({ PathException.class })
    public @ResponseBody ErrorMessage invalidPathException(PathException ex) {
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid path " + ex.getMessage());
    }

    @ExceptionHandler({ DuplicatePathException.class })
    public @ResponseBody ErrorMessage duplicateException(DuplicatePathException dpe) {
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "Duplicate path " + dpe.getMessage());
    }
}