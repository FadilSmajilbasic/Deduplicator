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

    public ScannerThread(Path rootPath, ScannerThreadListener listener, Report report, FileRepository fileRepository,
            List<String> ignorePaths) {
        this.listener = listener;
        this.rootPath = rootPath;
        this.report = report;
        this.fileRepository = fileRepository;
        this.ignorePaths = ignorePaths;

        for (String ignorePath : ignorePaths) {
            if (rootPath.toString().contains(ignorePath)) {
                ignoreFound = true;
            }
        }
    }

    @Override
    public void run() {

    
        if (!ignoreFound) {
            File[] list = new File(rootPath.toString()).listFiles();
            LinkedList<File> files = new LinkedList<File>();

            for (File file : list) {

                if (isPaused()) {

                    children.forEach(child -> {
                        child.pause();
                    });

                    try {
                        this.wait();
                        System.out.println("[INFO] Paused thread with root path: " + rootPath);
                    } catch (InterruptedException e) {

                    }

                }

                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    System.out.println("Directory " + file.getAbsolutePath());
                    ScannerThread thread = new ScannerThread(Paths.get(file.getAbsolutePath()), listener, report,
                            fileRepository, ignorePaths);
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
                System.err.println("[ERROR] Interrupted exception: Unable to hash files " + e.getMessage());
            }

            System.out.println("[INFO] Done scanning and working ThreadId: " + this.getId());
        } else {
            System.out.println("[INFO] Path not scanned, set to ignore: " + rootPath.toString());
        }
    }

    public void pause() {
        this.paused = true;
    }

    /**
     * @return the paused
     */
    public boolean isPaused() {
        return paused;
    }

    public void resumeScan() {
        this.paused = false;
        this.notifyAll();
        children.forEach(child -> {
            child.resumeScan();
        });
    }

    public void stopScan() {
        this.interrupt();
        children.forEach(child -> child.stopScan());
    }

}