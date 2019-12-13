package samt.smajilbasic.deduplicator.scanner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.repository.FileRepository;

/**
 * Thread for scanning
 */
public class ScannerThread extends Thread implements ScannerThreadListener {

    private ScannerThreadListener listener;
    private Path rootPath;
    private Report report;
    private int filesScanned = 0;
    private FileRepository fileRepository;

    private List<String> ignorePaths;

    private List<ScannerThread> children = new ArrayList<ScannerThread>();
    LinkedList<Hasher> hashers = new LinkedList<Hasher>();

    private Boolean paused = false;
    private boolean ignoreFound = false;
    Object monitor;
    private ExecutorService pool;
    /**
     * Default timeout for the scanning thread pool given in seconds
     */
    private static final Integer DEFAULT_TERMINATION_TIMEOUT = 1800;

    private static final Integer DEFAULT_THREAD_COUNT = 10;

    public ScannerThread(Path rootPath, ScannerThreadListener listener, Report report, FileRepository fileRepository,
            List<String> ignorePaths, Object monitor) {
        this.listener = listener;
        this.rootPath = rootPath;
        this.report = report;
        this.fileRepository = fileRepository;
        this.ignorePaths = ignorePaths;
        this.monitor = monitor;
        for (String ignorePath : ignorePaths) {
            if (rootPath.toString().startsWith(ignorePath)) {
                ignoreFound = true;
            }

        }
        pool = Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);
    }

    public synchronized void checkPaused() {
        synchronized (monitor) {
            while (isPaused()) {
                try {
                    monitor.wait();
                    System.out.println("[INFO] Threads resumed");
                } catch (InterruptedException e) {
                    System.out.println("[INFO] Interrupted exception on pause: " + e.getStackTrace().toString());
                }
                System.out.println("wait ended");
            }
        }

    }

    @Override
    public void run() {
        LinkedList<File> files = new LinkedList<File>();
        try {
            if (!ignoreFound) {

                File[] list = new File(rootPath.toString()).listFiles();

                for (File file : list) {
                    if (!Thread.interrupted()) {
                        checkPaused();

                        if (file.isFile()) {
                            files.add(file);
                        } else if (file.isDirectory()) {
                            System.out.println("[INFO] New directory found: " + file.getAbsolutePath());
                            ScannerThread thread = new ScannerThread(Paths.get(file.getAbsolutePath()), listener,
                                    report, fileRepository, ignorePaths, monitor);
                            children.add(thread);
                            if (!Thread.interrupted()) {
                                thread.start();
                                thread.join();
                            }
                        }
                    } else {
                        stopScan();
                    }
                }

            } else {
                System.out.println("[INFO] Path not scanned, set to ignore: " + rootPath.toString());
            }
        } catch (InterruptedException ie) {
            System.err.println("[ERROR] Scan thread interrupted: " + ie.getStackTrace().toString());
        } finally {

            if (files.size() > 0) {
                while (files.peek() != null) {

                    Hasher hasher = new Hasher(files.poll(), report, this, fileRepository, monitor);
                    hashers.add(hasher);
                    pool.execute(hasher);
                }
                pool.shutdown();
                try {
                    pool.awaitTermination(DEFAULT_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
                } catch (InterruptedException ie) {
                    System.err.println("[ERROR] Thread interrupted: " + ie.getStackTrace().toString());
                    pool.shutdownNow();
                } finally {
                    pool.shutdownNow();
                }
            }
            listener.addFilesScanned(filesScanned);
        }
    }

    public synchronized void pause() {
        if (isAlive()) {
            this.paused = true;
            System.out.println("[INFO] Paused thread with root path: " + rootPath);
        }
        children.forEach(child -> {
            child.pause();
        });
        hashers.forEach(hasher -> {
            hasher.pause();
        });
    }

    /**
     * @return the paused
     */
    public synchronized boolean isPaused() {
        return this.paused;
    }

    public void resumeScan() {
        this.paused = false;
        children.forEach(child -> {
            child.resumeScan();
        });
    }

    public synchronized void stopScan() {
        System.out.println("[INFO] Stopped thread" + getId());
        pool.shutdownNow();
        if (!Thread.interrupted())
            interrupt();
        children.forEach(child -> child.stopScan());
    }

    @Override
    public void addFilesScanned(int num) {
        filesScanned++;
    }

}