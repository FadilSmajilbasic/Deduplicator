package samt.smajilbasic.deduplicator.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

/**
 * ScanController
 */

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import samt.smajilbasic.deduplicator.Validator;
import samt.smajilbasic.deduplicator.entity.File;
import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.exception.ErrorMessage;
import samt.smajilbasic.deduplicator.repository.FileRepository;




@RestController
@RequestMapping(path = "/file") 
@Validated
public class FileController {

    @Autowired
    FileRepository fileRepository;

    @GetMapping(value = "/get")
    public @ResponseBody Iterable<File> getPaths() {
        return fileRepository.findAll();
    }

    @GetMapping(value = "/get/{path}")
    public @ResponseBody Object getValueByPath(@PathVariable String path) {
        if(path != null && fileRepository.existsById(path))
            return fileRepository.findById(path).get();
        else
            return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR,"Invalid file path");
    }
    
}
