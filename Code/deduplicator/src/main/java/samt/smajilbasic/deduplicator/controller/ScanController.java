package samt.smajilbasic.deduplicator.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * ScanController
 */

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.exception.ErrorMessage;
import samt.smajilbasic.deduplicator.exception.InvalidUserException;
import samt.smajilbasic.deduplicator.repository.ReportRepository;
import samt.smajilbasic.deduplicator.scanner.ScanManager;




@RestController
@RequestMapping(path = "/scan") 
public class ScanController {

    @Autowired
    ReportRepository rr;

    ScanManager currentScan;


    @PostMapping("/start")
    public @ResponseBody Report start(){
        // TODO: check authentication

        Report report = new Report();
        rr.save(report);

        currentScan = new ScanManager( rr.findById(report.getId()).get());
        currentScan.start();

        report.setDuration( (System.currentTimeMillis() - report.getStart().getTime()));

        return report;
    }

    @PostMapping("/stop")
    public @ResponseBody Report stop(){
        // TODO: check authentication

        currentScan.interrupt();
        Report report = currentScan.getReport();
        report.setDuration( (System.currentTimeMillis() - report.getStart().getTime()));
        return report;
    }

    @GetMapping(value="/getProgress")
    public @ResponseBody String getFile() {
        return "Progress";
    }


    @ExceptionHandler({ InvalidUserException.class })
    public @ResponseBody ErrorMessage invalidUser() {
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid user set in report");
    }
    
}
