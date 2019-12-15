package samt.smajilbasic.deduplicator.scanner;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import samt.smajilbasic.deduplicator.entity.Duplicate;
import samt.smajilbasic.deduplicator.entity.GlobalPath;
import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.repository.DuplicateRepository;
import samt.smajilbasic.deduplicator.repository.FileRepository;
import samt.smajilbasic.deduplicator.repository.GlobalPathRepository;
import samt.smajilbasic.deduplicator.repository.ReportRepository;

/**
 * ScanManager
 */

@Component
public class ScanManager extends Thread implements ScannerThreadListener {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    GlobalPathRepository gpr;

    private Iterator<GlobalPath> paths;

    private ReportRepository reportRepository;

    private Integer reportId;

    private Integer filesScanned = 0;

    private List<ScannerThread> rootThreads = new ArrayList<ScannerThread>();

    private static final Integer DEFAULT_THREAD_COUNT = 10;

    private Integer threadCount = ScanManager.DEFAULT_THREAD_COUNT;

    private ExecutorService pool;

    private Report report;

    private boolean paused = false;

    private ScanListener listener;

    /**
     * Default timeout for the scanning thread pool given in seconds
     */
    private static final Integer DEFAULT_TERMINATION_TIMEOUT = 1800;

    /**
     * An optional timeout for the scanning thread pool
     */
    private Integer terminationTimeout = ScanManager.DEFAULT_TERMINATION_TIMEOUT;

    private final Object monitor = new Object();

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

        List<GlobalPath> ignorePathsFromRepository = gpr.findIgnored();

        List<String> ignorePaths = new ArrayList<>();
        List<String> ignoreFiles = new ArrayList<>();

        for (GlobalPath ignorePath : ignorePathsFromRepository) {
            if (ignorePath.isFile())
                ignoreFiles.add(ignorePath.getPath());
            else
                ignorePaths.add(ignorePath.getPath());
        }

        try {
            while (paths.hasNext()) {
                ScannerThread thread = new ScannerThread(Paths.get(paths.next().getPath()), this, report,
                        fileRepository, ignorePaths, ignoreFiles, monitor);
                rootThreads.add(thread);
                pool.execute(thread);
            }

            pool.shutdown();
            pool.awaitTermination(terminationTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            System.err.println("[ERROR] Thread interrupted: " + ie.getStackTrace().toString());
            pool.shutdownNow();
        } finally {
            pool.shutdownNow();

            List<Duplicate> duplicates = duplicateRepository.findDuplicatesFromReport(report);

            report.setAverageDuplicateCount((float) duplicates.size() / (float) filesScanned);
            report.setDuration((System.currentTimeMillis() - report.getStart()));
            reportRepository.save(report);

            System.out.println("[INFO] Scan manager Finished");
            if (listener != null)
                listener.scanFinished();
        }
    }

    @Override
    public synchronized void addFilesScanned(int num) {
        filesScanned += num;
        report.setFilesScanned(filesScanned);
        reportRepository.save(report);
    }

    public void pauseAll() {
        if (!paused)
            paused = true;
        rootThreads.forEach(rootThread -> rootThread.pause());
    }

    public void resumeAll() {
        paused = false;
        rootThreads.forEach(rootThread -> rootThread.resumeScan());
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }

    public void stopScan() {
        pool.shutdownNow();
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
        if (threadCount != null)
            this.threadCount = threadCount > 0 ? threadCount : DEFAULT_THREAD_COUNT;
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

    /**
     * @return the paths
     */
    public Iterator<GlobalPath> getPaths() {
        return paths;
    }

    /**
     * @param listener the listener to set
     */
    public void setListener(ScanListener listener) {
        this.listener = listener;
    }

}