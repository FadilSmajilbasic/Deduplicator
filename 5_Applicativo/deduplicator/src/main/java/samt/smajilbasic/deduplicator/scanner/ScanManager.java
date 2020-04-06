package samt.smajilbasic.deduplicator.scanner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
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
 * La classe ScanManger gestisce le thread di scansione delle cartelle. Usa
 * l'annotazione @{@link Component} per indicare a Sping che alla creazione
 * dell'oggetto ScheduleChecker bisogna anche istanziare gli attributi con
 * l'annotazione @{@link Autowired}.
 */
@Component
public class ScanManager extends Thread implements ScannerWorkerListener {

    /**
     * L'attributo fileRepository serve al controller per interfacciarsi con la
     * tabella File del database. Usa l'annotazione @{@link Autowired} per indicare
     * a spring che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    FileRepository fileRepository;

    /**
     * L'attributo gpr serve al controller per interfacciarsi con la tabella
     * GlobalPath del database. Usa l'annotazione @{@link Autowired}4 per indicare a
     * spring che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    GlobalPathRepository gpr;

    /**
     * L'attributo paths contiene tutti i percorsi che si trovano nel database
     * GlobalPath.
     */
    private List<String> paths = new ArrayList<String>();

    /**
     * L'attributo reportRepository serve al controller per interfacciarsi con la
     * tabella Report del database.
     */
    private ReportRepository reportRepository;

    /**
     * L'attributo reportId contiene l'id del rapporto al quale verranno aggiunti i
     * file trovati.
     */
    private Integer reportId;

    /**
     * L'attributo filesScanned contiene il numero di files scansionati.
     */
    public Integer totalFiles = 0;

    public float scanProgress = 0f;
    /**
     * L'attributo DEFAULT_THREAD_COUNT contiene il numero predefinito di thread che
     * possono essere eseguite contemporaneamente.
     */
    private static final Integer DEFAULT_THREAD_COUNT = 10;

    /**
     * L'attributo threadCount contiene il numero di thread che possono essere
     * eseguite contemporaneamente impo
     */
    private Integer threadCount = ScanManager.DEFAULT_THREAD_COUNT;

    private PausableExecutor pool;

    private final long POLLING_DELAY = 100;

    private Report report;

    private boolean paused = false;

    private ScanListener listener;
    private FilesScanner scanner;
    private Thread statusThread;
    private int unsuccessfulSaves = 0;
    /**
     * Default timeout for the scanning thread pool given in seconds
     */
    private static final Integer DEFAULT_TERMINATION_TIMEOUT = 600;

    /**
     * An optional timeout for the scanning thread pool
     */
    private Integer terminationTimeout = ScanManager.DEFAULT_TERMINATION_TIMEOUT;

    private final Object statusMonitor = new Object();

    @Autowired
    DuplicateRepository duplicateRepository;

    public ScanManager() {
        super();

        statusThread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted() && scanProgress < 1f && scanProgress != -1f && scanProgress >= 0f) {
                        synchronized (statusMonitor) {
                            if (paused) {
                                statusMonitor.wait();
                            }
                        }
                        System.out.print("\rProgress: " + calcuateProgress() + "%");
                        synchronized (this) {
                            this.wait(POLLING_DELAY);
                        }

                    }
                } catch (InterruptedException ie) {
                    System.out.print("\rProgress: " + calcuateProgress() + "%\n");
                }
            }
        };
    }

    @Override
    public void run() {
        report = getReport();

        gpr.findAll().iterator().forEachRemaining(path -> {
            paths.add(path.getPath());
        });

        List<GlobalPath> ignorePathsFromRepository = gpr.findIgnored();

        LinkedList<String> ignorePaths = new LinkedList<>();

        ignorePathsFromRepository.forEach(globalPath -> {
            ignorePaths.add(globalPath.getPath());
        });

        try {

            scanner = new FilesScanner(paths, ignorePaths);

            scanner.start();
            synchronized (scanner) {
                scanner.wait();
            }

            totalFiles = scanner.getSize();
            ArrayBlockingQueue<ScannerWorker> queue = new ArrayBlockingQueue<ScannerWorker>(totalFiles);
            pool = new PausableExecutor(threadCount, terminationTimeout, TimeUnit.SECONDS, queue);
            statusThread.start();
            while (scanner.hasNext()) {
                ScannerWorker thread = new ScannerWorker(scanner.getNextFile(), fileRepository, report, this);
                pool.submit(thread);
            }
            pool.shutdown();

            statusThread.join();
        } catch (InterruptedException ie) {
            System.err.println("[ERROR] Scan Manager interrupted");
            pool.shutdownNow();
            statusThread.interrupt();
        } finally {
            pool.shutdownNow();
            List<Duplicate> duplicates = duplicateRepository.findDuplicatesFromReport(report);

            System.out.println("Scanned files: " + fileRepository.findByReport(report));

            totalFiles = fileRepository.findByReport(report);

            if (totalFiles == 0) {
                report.setAverageDuplicateCount(0f);
            } else {
                report.setAverageDuplicateCount(((float) duplicates.size() / (float) totalFiles));
            }
            report.setDuration((System.currentTimeMillis() - report.getStart()));
            report.setFilesScanned(totalFiles);
            reportRepository.save(report);

            System.out.println("[INFO] Scan manager Finished");
            if (listener != null)
                listener.scanFinished();
        }
    }

    private String calcuateProgress() {
        if (totalFiles != 0) {
            scanProgress = (1f
                - (((float) totalFiles - (float) fileRepository.findByReport(report) - getUnsuccessfulSaves())
                / (float) totalFiles));
        } else {
            scanProgress = -1;
        }
        return String.format(java.util.Locale.getDefault(), "%.2f", scanProgress * 100f);
    }

    public void pauseScan() {
        if (!paused) {
            paused = true;
            System.out.println("pause invoked");
            pool.pause();
        }
    }

    public void resumeScan() {
        paused = false;
        pool.resume();
        synchronized (statusMonitor) {
            statusMonitor.notifyAll();
        }
    }

    public void stopScan() {
        pool.shutdownNow();
        scanner.interrupt();
        this.interrupt();
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
    public List<String> getPaths() {
        return paths;
    }

    /**
     * @param listener the listener to set
     */
    public void setListener(ScanListener listener) {
        this.listener = listener;
    }

    @Override
    public synchronized void fileNotSaved() {
        this.unsuccessfulSaves++;
    }

    /**
     * @return the unsuccessfulSaves
     */
    public synchronized int getUnsuccessfulSaves() {
        return unsuccessfulSaves;
    }

    /**
     * @return the totalFiles
     */
    public Integer getTotalFiles() {
        return totalFiles;
    }

}