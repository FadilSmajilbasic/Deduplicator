package samt.smajilbasic.deduplicator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * ScanController
 */

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.exception.ErrorMessage;
import samt.smajilbasic.deduplicator.repository.GlobalPathRepository;
import samt.smajilbasic.deduplicator.repository.ReportRepository;
import samt.smajilbasic.deduplicator.scanner.ScanManager;

@RestController
@RequestMapping(path = "/scan")
public class ScanController {

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    ScanManager currentScan;

    @Autowired
    GlobalPathRepository gpr;

    @PostMapping("/start")
    public @ResponseBody Object start(@RequestParam(required = false) Integer threadCount) {

        if (gpr.count() > 0) {

            Report report = new Report();
            reportRepository.save(report);

            currentScan.setReportRepository(reportRepository);
            currentScan.setReportId(report.getId());
            currentScan.setThreadCount(threadCount);
            
            currentScan.start();

            return report;
        } else {
            return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "No path to scan set");
        }
    }

    @PostMapping("/stop")
    public @ResponseBody Report stop() {
        // TODO: check authentication

        currentScan.stopScan();
        try {
            currentScan.join();
        } catch (InterruptedException ie) {

        }

        Report report = currentScan.getReport();
        report.setDuration((System.currentTimeMillis() - report.getStart().getTime()));
        return report;
    }

    @PostMapping("/pause")
    public @ResponseBody ErrorMessage pause() {
        // TODO: check authentication

        currentScan.pauseAll();
        return new ErrorMessage(HttpStatus.OK, "Scan paused");
    }

    @PostMapping("/resume")
    public @ResponseBody ErrorMessage resume() {
        // TODO: check authentication

        currentScan.resumeAll();
        return new ErrorMessage(HttpStatus.OK, "Scan resumed");
    }

    @ExceptionHandler({ RuntimeException.class })
    public @ResponseBody ErrorMessage invalidReport(RuntimeException ex) {
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

}
