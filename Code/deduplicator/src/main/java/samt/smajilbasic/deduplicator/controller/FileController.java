package samt.smajilbasic.deduplicator.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

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
import samt.smajilbasic.deduplicator.exception.ErrorMessage;
import samt.smajilbasic.deduplicator.repository.FileRepository;
import samt.smajilbasic.deduplicator.repository.ReportRepository;

@RestController
@RequestMapping(path = "/file")
public class FileController {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    ReportRepository reportRepository;

    @GetMapping(value = "/get")
    public @ResponseBody Iterable<File> getFiles() {
        return fileRepository.findAll();
    }

    @GetMapping(value = "/get/{id}")
    public @ResponseBody Object getFilesFromReport(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if (intId != null && reportRepository.existsById(intId))
            return reportRepository.findById(intId).get().getFile();
        else
            return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid report id");
    }

    

}
