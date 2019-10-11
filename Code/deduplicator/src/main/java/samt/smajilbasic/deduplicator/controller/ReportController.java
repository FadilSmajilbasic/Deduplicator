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
import samt.smajilbasic.deduplicator.exception.ErrorMessage;
import samt.smajilbasic.deduplicator.repository.DuplicateRepository;
import samt.smajilbasic.deduplicator.repository.ReportRepository;




@RestController
@RequestMapping(path = "/report")
public class ReportController {

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    DuplicateRepository duplicateRepository;

    @GetMapping(value = "/get")
    public @ResponseBody Iterable<Report> getReports() {
        return reportRepository.findAll();
    }

    @GetMapping(value = "/get/{id}")
    public @ResponseBody Object getReportById(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if(intId != null && reportRepository.existsById(intId))
            return reportRepository.findById(intId).get();
        else
            return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR,"Invalid report id");
    }

    @GetMapping(value = "/get/duplicate/{id}") 
    public @ResponseBody Object getDuplicateByReportId(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if(intId != null && reportRepository.existsById(intId))
            return duplicateRepository.findDuplicates((Report)getReportById(id));
        else
            return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR,"Invalid report id");
    }
}
