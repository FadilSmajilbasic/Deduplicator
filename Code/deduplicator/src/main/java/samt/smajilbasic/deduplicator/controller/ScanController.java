package samt.smajilbasic.deduplicator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.exception.Message;
import samt.smajilbasic.deduplicator.repository.GlobalPathRepository;
import samt.smajilbasic.deduplicator.repository.ReportRepository;
import samt.smajilbasic.deduplicator.scanner.ScanManager;

/**
 * ScanController
 */
@RestController
@RequestMapping(path = "/scan")
public class ScanController {

    @Autowired
    ReportRepository reportRepository;


    ScanManager currentScan;

    @Autowired
    GlobalPathRepository gpr;

    @Autowired
    ApplicationContext context;

    @PostMapping("/start")
    public @ResponseBody Object start(@RequestParam(required = false) Integer threadCount) {

        if (gpr.count() > 0) {

            currentScan = (ScanManager) context.getBean("scanManager");
            Report report = new Report();
            reportRepository.save(report);

            currentScan.setReportRepository(reportRepository);
            currentScan.setReportId(report.getId());
            currentScan.setThreadCount(threadCount);
            currentScan.start();

            return report;
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "No path to scan set");
        }
    }

    @PostMapping("/stop")
    public @ResponseBody Object stop() {
        if (currentScan != null) {
            currentScan.stopScan();

            System.out.println("Waiting finish");
            while (currentScan.isAlive()) {
                long time = System.currentTimeMillis();
                if (System.currentTimeMillis() - time > 500) {
                    System.out.print(".");
                }
            }
            System.out.println("dooone");

            Report report = currentScan.getReport();

            BeanDefinitionRegistry factory = (BeanDefinitionRegistry) context.getAutowireCapableBeanFactory();

            ((DefaultListableBeanFactory) factory).destroySingleton("scanManager");

            System.out.println(currentScan.toString());

            return report;
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "No scan currently runnin");
        }
    }

    @PostMapping("/pause")
    public @ResponseBody Message pause() {
        if (currentScan != null) {
            if (!currentScan.isPaused()) {
                currentScan.pauseAll();
                return new Message(HttpStatus.OK, "Scan paused");
            } else {
                return new Message(HttpStatus.OK, "Scan already paused");
            }
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "No scan currently runnin");
        }
    }

    @PostMapping("/resume")
    public @ResponseBody Message resume() {
        if (currentScan != null) {
            if (currentScan.isPaused())
                currentScan.resumeAll();
            return new Message(HttpStatus.OK, "Scan resumed");
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "No scan currently runnin");
        }
    }

}
