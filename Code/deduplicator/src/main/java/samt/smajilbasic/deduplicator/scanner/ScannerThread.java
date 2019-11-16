package samt.smajilbasic.deduplicator.scanner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.repository.FileRepository;

/**
 * Thread for scanning
 */
public class ScannerThread extends Thread {

    private ScannerThreadListener listener;
    private Path rootPath;
    private Report report;
    private FileRepository fileRepository;

    private List<String> ignorePaths;

    private List<ScannerThread> children = new ArrayList<ScannerThread>();

    private Boolean paused = false;
    private boolean ignoreFound = false;
    Object monitor;

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
    }

    public synchronized void look() {
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
                        look();

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
                    }else{
                        stopScan();
                    }
                }

            } else {
                System.out.println("[INFO] Path not scanned, set to ignore: " +
                rootPath.toString());
            }
        } catch (InterruptedException ie) {
            System.err.println("[ERROR] Scan thread interrupted: " + ie.getStackTrace().toString());
        } finally {

            if (files.size() > 0) {
                Hasher hasher = new Hasher(files, report, listener, fileRepository);
                hasher.start();
                try {
                    hasher.join();
                } catch (InterruptedException e) {
                    System.err.println("[ERROR] Interrupted exception on join hasher " + e.getMessage());
                }
            }else{
            }
            this.paused = null;


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
        if(!Thread.interrupted())
            interrupt();
        children.forEach(child -> child.stopScan());
    }

}