package samt.smajilbasic.deduplicator.scanner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import samt.smajilbasic.deduplicator.entity.GlobalPath;

/**
 * FilesScanner
 */
public class FilesScanner extends Thread {

    private List<String> paths;
    private List<String> ignorePaths;
    private LinkedList<String> scanPaths;
    private Object monitor;
    private boolean paused;

    public FilesScanner(List<String> paths, List<String> ignorePaths, List<String> ignoreFiles, Object monitor) {
        super();
        // this.report = report;
        this.monitor = monitor;
        this.ignorePaths = ignorePaths;
        this.monitor = monitor;
        this.paths = paths;
        this.scanPaths = new LinkedList<String>();
    }

    @Override
    public void run() {
        for (String path : paths) {

            for (String ignorePath : ignorePaths) {
                if (path.contains(ignorePath)) {
                    paths.remove(path);
                } else {
                    System.out.println("Path to ignore:" + path);
                }
            }
        }

        LinkedList<String> tempList = new LinkedList<>();
        paths.forEach(path->{tempList.add(path);});

        long start = System.currentTimeMillis();
        while(tempList.peek()!= null) {
            String path = tempList.poll();
            File file = new File(path);
            if (file.isFile()) {
                scanPaths.add(path);
            } else {
                for (File internalFile : file.listFiles()) { //TODO: check if it works
                    tempList.addLast(internalFile.getAbsolutePath());
                };
            }
        }
        System.out.println("File scanner finished in " + (System.currentTimeMillis() - start) +"ms");
        System.out.println("Found " + scanPaths.size() + "files");
        synchronized(this){
            this.notifyAll();
        }

    }

    public synchronized void checkPaused() {
        synchronized (monitor) {
            while (isPaused()) {
                try {
                    monitor.wait();
                    System.out.println("[INFO] Thread resumed");
                } catch (InterruptedException e) {
                    System.out.println("[INFO] Interrupted exception on pause: " + e.getStackTrace().toString());
                }
                System.out.println("Wait ended");
            }
        }

    }

    public synchronized File getNextFile() {
        return new File(scanPaths.poll());
    }

    public synchronized boolean hasNext() {
        return (scanPaths.peek() != null);
    }

    public synchronized boolean isPaused() {
        return paused;
    }

    public synchronized void setPaused(boolean paused) {
        this.paused = paused;
    }

    public synchronized int getSize(){
        return scanPaths.size();
    }

}