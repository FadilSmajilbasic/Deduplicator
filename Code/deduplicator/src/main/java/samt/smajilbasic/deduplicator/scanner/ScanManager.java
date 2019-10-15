package samt.smajilbasic.deduplicator.scanner;

import samt.smajilbasic.deduplicator.entity.Duplicate;
import samt.smajilbasic.deduplicator.entity.File;
import samt.smajilbasic.deduplicator.entity.GlobalPath;
import samt.smajilbasic.deduplicator.entity.Report;

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
import java.util.concurrent.TimeUnit;

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

    private Integer filesScanned = 0;

    private List<ScannerThread> rootThreads = new ArrayList<ScannerThread>();

    private static final Integer DEFAULT_THREAD_COUNT = 200;

    private Integer threadCount = ScanManager.DEFAULT_THREAD_COUNT;

    private ExecutorService pool;

    private Report report;

    private boolean paused = false;

    /**
     * Default timeout for the scanning thread pool given in seconds
     */
    private static final Integer DEFAULT_TERMINATION_TIMEOUT = 1800;

    /**
     * An optional timeout for the scanning thread pool
     */
    private Integer terminationTimeout = ScanManager.DEFAULT_TERMINATION_TIMEOUT;

    @Autowired
    DuplicateRepository duplicateRepository;

    public ScanManager() {
        super();
    }

    @Override
    public void run() {
        pool = Executors.newFixedThreadPool(threadCount);
        report = getReport();

        paths = gpr.findAll().iterator();

        try {
            while (paths.hasNext()) {
                ScannerThread thread = new ScannerThread(Paths.get(paths.next().getPath()), this);
                rootThreads.add(thread);
                pool.execute(thread);
            }
            pool.shutdown();

            pool.awaitTermination(terminationTimeout, TimeUnit.SECONDS);

        } catch (InterruptedException ie) {
            System.err.println("[ERROR] Thread interrupted: " + ie.getStackTrace());
        } finally {

            this.work();

            List<Duplicate> duplicates = duplicateRepository.findDuplicatesFromReport(report);

            report.setFilesScanned(filesScanned);
            report.setAverageDuplicateCount((float) duplicates.size() / (float) filesScanned);
            report.setDuration((System.currentTimeMillis() - report.getStart().getTime()));
            reportRepository.save(report);

            System.out.println("[INFO] Done all");
        }

    }

    @Override
    public void fileFound(java.io.File file) {
        System.out.println("Found new file: " + file.getAbsolutePath().toString());
        files.add(file);
    }

    public void work() {
        System.out.println("Files found: " + files.size());
        while (files.peek() != null) {
            java.io.File file = files.poll();

            try {
                String hash = Hasher.getFileHash(Paths.get(file.getAbsolutePath()));
                Long lastModified = file.lastModified();
                int size = (Files.readAllBytes(Paths.get(file.getAbsolutePath().toString()))).length;
                File record = new File(file.getAbsolutePath(), lastModified, hash, size, report);
                fileRepository.save(record);
                filesScanned++;

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

    public void pauseAll() {
        paused = true;
        rootThreads.forEach(rootThread -> rootThread.pause());
    }

    public void resumeAll() {
        rootThreads.forEach(rootThread -> rootThread.resumeScan());
    }

    public void stopScan() {
        rootThreads.forEach(rootThread -> rootThread.stopScan());

    }

    /**
     * @param reportId the reportId to set
     */
    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    /**
     * @return the reportId
     */
    public int getReportId() {
        return reportId;
    }

    /**
     * @param reportRepository the reportRepository to set
     */
    public void setReportRepository(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * @return the reportRepository
     */
    public ReportRepository getReportRepository() {
        return reportRepository;
    }

    /**
     * @param terminationTimeout the terminationTimeout to set
     */
    public void setTerminationTimeout(Integer terminationTimeout) {
        if (terminationTimeout > 0)
            this.terminationTimeout = terminationTimeout;
        else
            this.terminationTimeout = DEFAULT_TERMINATION_TIMEOUT;
    }

    /**
     * @param threadCount the threadCount to set
     */
    public void setThreadCount(Integer threadCount) {
        if (threadCount > 0)
            this.threadCount = threadCount;
        else
            this.threadCount = DEFAULT_THREAD_COUNT;
    }

    public Report getReport() {
        if (reportId != null && reportRepository != null) {
            if (reportRepository.existsById(reportId)) {
                return reportRepository.findById(reportId).get();
            } else {
                throw new RuntimeException("[ERROR] Unable to find report");
            }
        } else {
            throw new RuntimeException("[ERROR] Report id or report repository not set");
        }
    }

    /**
     * @return the paused
     */
    public boolean isPaused() {
        return paused;
    }

}