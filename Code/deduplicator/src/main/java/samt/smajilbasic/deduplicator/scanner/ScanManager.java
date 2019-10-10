package samt.smajilbasic.deduplicator.scanner;

import samt.smajilbasic.deduplicator.entity.Duplicate;
import samt.smajilbasic.deduplicator.entity.File;
import samt.smajilbasic.deduplicator.entity.GlobalPath;
import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.exception.InvalidReportException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import samt.smajilbasic.deduplicator.repository.DuplicateRepository;
import samt.smajilbasic.deduplicator.repository.FileRepository;
import samt.smajilbasic.deduplicator.repository.GlobalPathRepository;
import samt.smajilbasic.deduplicator.repository.ReportRepository;

/**
 * ScanManager
 */

@Service
public class ScanManager extends Thread implements ScannerThreadListener {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    GlobalPathRepository gpr;

    private Iterator<GlobalPath> paths;

    private LinkedList<java.io.File> files = new LinkedList<java.io.File>();

    private ReportRepository reportRepository;

    private Integer reportId;

    private Integer filesFound = 0;

    private List<ScannerThread> rootThreads = new ArrayList<ScannerThread>();

    private final Integer THREAD_COUNT = 200;

    private ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);

    private Report report;

    @Autowired
    DuplicateRepository duplicateRepository;

    public ScanManager() {
        super();
    }

    @Override
    public void run() {

        report = getReport();

        paths = gpr.findAll().iterator();

        try {
            while (paths.hasNext()) {
                ScannerThread thread = new ScannerThread(Paths.get(paths.next().getPath()), this);
                rootThreads.add(thread);

                // pool.submit(thread);
                // pool.awaitTermination(timeout, unit);
                thread.start();
                thread.join();
            }

        } catch (InterruptedException ie) {
            System.err.println("[ERROR] Thread interrupted: " + ie.getStackTrace());
        } finally {
            this.work();

            List<Duplicate> duplicates = duplicateRepository.findDuplicates(report);

            report.setDuration((System.currentTimeMillis() - report.getStart().getTime()));
            report.setDuplicateCount(duplicates.size());
            
            reportRepository.save(report);
            System.out.println("[INFO] Done all");

            System.out.println("Writing duplicates");
            for (Duplicate var : duplicates) {
                System.out.println("Path: " + var.getPath());
                System.out.println("Last modified: " + var.getLastModified());
            }
        }

    }

    @Override
    public void fileFound(java.io.File file) {
        System.out.println("Found new file: " + file.getAbsolutePath().toString());
        files.add(file);
    }

    public void work() {
        while (files.peek() != null) {
            java.io.File file = files.poll();

            try {
                String hash = Hasher.getFileHash(Paths.get(file.getAbsolutePath()));
                Long lastModified = file.lastModified();
                int size = (Files.readAllBytes(Paths.get(file.getAbsolutePath().toString()))).length;
                File record = new File(file.getAbsolutePath(), lastModified, hash, size, report);
                fileRepository.save(record);
                filesFound++;

            } catch (NoSuchAlgorithmException nsae) {
                System.err.println("[ERROR] Unable to hash file: " + nsae.getMessage());

            } catch (IOException ioe) {
                System.err.println("[ERROR] Unable to read file: " + ioe.getMessage());
            } catch (NullPointerException npe) {
                System.err.println("[ERROR] Unable to save file: " + npe.getMessage());
            }
        }

        System.out.println("[INFO] Done saving files");

    }

    public void checkDuplicates() {

    }

    public void pauseAll() {
        rootThreads.forEach(rootThread -> rootThread.pause());
    }

    public void resumeAll() {
        rootThreads.forEach(rootThread -> rootThread.resumeScan());
    }

    /**
     * @param reportId the reportId to set
     */
    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    /**
     * @param reportRepository the reportRepository to set
     */
    public void setReportRepository(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * @return the reportId
     */
    public int getReportId() {
        return reportId;
    }

    /**
     * @return the reportRepository
     */
    public ReportRepository getReportRepository() {
        return reportRepository;
    }

    public Report getReport() {
        // TODO: check if null
        if (reportId != null && reportRepository != null) {
            if (reportRepository.existsById(reportId)) {
                return reportRepository.findById(reportId).get();
            } else {
                throw new InvalidReportException("[ERROR] Unable to find report");
            }
        } else {
            throw new InvalidReportException("[ERROR] Report id or report repository not set");
        }
    }

    public void stopScan() {
        rootThreads.forEach(rootThread -> rootThread.stopScan());

    }

}