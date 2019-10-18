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
import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.exception.Message;
import samt.smajilbasic.deduplicator.repository.DuplicateRepository;
import samt.smajilbasic.deduplicator.repository.FileRepository;
import samt.smajilbasic.deduplicator.repository.ReportRepository;

@RestController
@RequestMapping(path = "/report")
public class ReportController {

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    DuplicateRepository duplicateRepository;

    @Autowired
    FileRepository fileRepository;

    @GetMapping(value = "/get")
    public @ResponseBody Iterable<Report> getReports() {
        return reportRepository.findAll();
    }

    @GetMapping(value = "/get/{id}")
    public @ResponseBody Object getReportById(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if (intId != null && reportRepository.existsById(intId))
            return reportRepository.findById(intId).get();
        else
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid report id");
    }

    @GetMapping(value = "/get/duplicates/{id}")
    public @ResponseBody Object getDuplicateByReportId(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if (intId != null && reportRepository.existsById(intId))
            return duplicateRepository.findDuplicatesFromReport((Report) getReportById(id));
        else
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid report id");
    }

    @GetMapping(value = "/get/duplicates/{id}/{hash}")
    public @ResponseBody Object getFileByHash(@PathVariable String id, @PathVariable String hash) {
        Integer intId = Validator.isInt(id);
        if (hash != null && fileRepository.existsByHash(hash) && hash.length() == 32 && intId != null
                && reportRepository.existsById(intId)) {
            return fileRepository.findFilesFromHashAndReport(reportRepository.findById(intId).get(), hash);
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid file path");
        }
    }
}
