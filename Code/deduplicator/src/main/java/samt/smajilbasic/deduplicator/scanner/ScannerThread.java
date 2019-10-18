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

    private boolean paused = false;
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
            while(isPaused()) {
                try {
                    System.out.println("enterd wait");
                    monitor.wait();
                    System.out.println("Resumed");
                } catch (InterruptedException e) {
                    System.out.println("[INFO] Interrupted exception on pause: " + e.getStackTrace());
                }
                System.out.println("wait ended");
            }
        }

    }

    @Override
    public void run() {

        if (!ignoreFound) {

            File[] list = new File(rootPath.toString()).listFiles();
            LinkedList<File> files = new LinkedList<File>();

            for (File file : list) {

                look();

                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    System.out.println("[INFO] New directory found: " + file.getAbsolutePath());
                    ScannerThread thread = new ScannerThread(Paths.get(file.getAbsolutePath()), listener, report,
                            fileRepository, ignorePaths, monitor);
                    children.add(thread);
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException ie) {
                        System.err.println("[ERROR] Scan thread interrupted: " + ie.getStackTrace());
                    }

                }
            }
            Hasher hasher = new Hasher(files, report, listener, fileRepository);
            hasher.start();
            try {
                hasher.join();
            } catch (InterruptedException e) {
                System.err.println("[ERROR] Interrupted exception on join hasher " + e.getMessage());
            }

        } else {
            System.out.println("[INFO] Path not scanned, set to ignore: " + rootPath.toString());
        }
    }

    public synchronized void pause() {
        // if(isAlive()){
        this.paused = true;
        System.out.println("[INFO] Paused thread with root path: " + rootPath);
        children.forEach(child -> {
            child.pause();
        });
        // }
    }

    /**
     * @return the paused
     */
    public synchronized boolean isPaused() {
        return this.paused;

    }

    public void resumeScan() {
        // System.out.println("[INFO] Scan resumed ");
        this.paused = false;
        // notifyAll();
        // this.notify();
        System.out.println("notify all");
        children.forEach(child -> {
            child.resumeScan();
        });
    }

    public void stopScan() {
        this.interrupt();
        children.forEach(child -> child.stopScan());
    }

}