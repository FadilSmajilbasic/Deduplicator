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
 * La classe ScanManger gestisce le thread di scansione delle cartelle. Usa
 * l'annotazione @{@link Component} per indicare a Sping che alla creazione
 * dell'oggetto ScheduleChecker bisogna anche istanziare gli attributi con
 * l'annotazione @{@link Autowired}.
 */
@Component
public class ScanManager extends Thread implements ScannerThreadListener {

    /**
     * L'attributo fileRepository serve al controller per interfacciarsi con la
     * tabella File del database. Usa l'annotazione @{@link Autowired} per indicare a
     * spring che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    FileRepository fileRepository;

    /**
     * L'attributo gpr serve al controller per interfacciarsi con la tabella
     * GlobalPath del database. Usa l'annotazione @{@link Autowired}4 per indicare a spring
     * che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    GlobalPathRepository gpr;

    /**
     * L'attributo paths contiene tutti i percorsi che si trovano nel database GlobalPath.
     */
    private Iterator<GlobalPath> paths;

    /**
     * L'attributo reportRepository serve al controller per interfacciarsi con la
     * tabella Report del database.
     */
    private ReportRepository reportRepository;

    /**
     * L'attributo reportId contiene l'id del rapporto al quale verranno aggiunti i file trovati.
     */
    private Integer reportId;

    /**
     * L'attributo filesScanned contiene il numero di files scansionati.
     */
    private Integer filesScanned = 0;

    /**
     * L'attributo rootThreads contiene la lista di thread che dovranno essere scanerizzate.
     */
    private List<ScannerThread> rootThreads = new ArrayList<ScannerThread>();

    /**
     * L'attributo DEFAULT_THREAD_COUNT contiene il numero predefinito di thread che possono essere eseguite contemporaneamente.
     */
    private static final Integer DEFAULT_THREAD_COUNT = 10;

    /**
     * L'attributo threadCount contiene il numero di thread che possono essere eseguite contemporaneamente impo
     */
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
        List<String> ignores = new ArrayList<>();

        for (GlobalPath ignorePath : ignorePathsFromRepository) {
            if (ignorePath.isFile())
                ignores.add(ignorePath.getPath());
            else
                ignorePaths.add(ignorePath.getPath());
        }

        try {
            while (paths.hasNext()) {
                ScannerThread thread = new ScannerThread(Paths.get(paths.next().getPath()), this, report,
                        fileRepository, ignorePaths, ignores, monitor);
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