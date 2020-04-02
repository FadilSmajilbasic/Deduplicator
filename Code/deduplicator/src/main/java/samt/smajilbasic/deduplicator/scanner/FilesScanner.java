package samt.smajilbasic.deduplicator.scanner;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FilesScanner
 */
public class FilesScanner extends Thread {

    private List<String> paths;
    private List<String> ignorePaths;
    private LinkedList<String> scanPaths;

    public FilesScanner(List<String> paths, List<String> ignorePaths) {
        super();
        this.ignorePaths = ignorePaths;
        this.paths = paths;
        this.scanPaths = new LinkedList<String>();
    }

    @Override
    public void run() {

        LinkedList<String> pathsLinkedList = new LinkedList<>();

        paths.iterator().forEachRemaining(pathsLinkedList::add);

        boolean test;
        long start = System.currentTimeMillis();
        try {
            while (pathsLinkedList.peek() != null) {
                String path = pathsLinkedList.poll();
                File file = new File(path);
                Path p = Paths.get(path);
                test = true;

                for (String ignorePath : ignorePaths) {
                    if (path.startsWith(ignorePath) || path.equals(ignorePath)) {
                        test = false;
                    }
                }
                if (test) {
                    if (!Files.notExists(p) && Files.isReadable(p)) {
                        if (file.isFile()) {

                            scanPaths.add(path);

                        } else {
                            try {
                                for (File internalFile : file.listFiles()) {
                                    pathsLinkedList.addLast(internalFile.getAbsolutePath());
                                }
                            } catch (NullPointerException npe) {
                                System.out.println("File not found: " + path);
                            }

                        }
                    } else {
                        Logger.getGlobal().log(Level.SEVERE,"FileScanner unable to read file: " + path);
                    }
                }
            }
        } catch (OutOfMemoryError oome) {
            Logger.getGlobal().log(Level.SEVERE,"FileScanner too many files in the paths specified: " + scanPaths.size());

        }

        System.out.println("File scanner finished in " + (System.currentTimeMillis() - start) + "ms");
        System.out.println("Found " + scanPaths.size() + " files");

        synchronized (this) {
            this.notifyAll();
        }

    }

    public synchronized File getNextFile() {
        return new File(scanPaths.poll());
    }

    public synchronized boolean hasNext() {
        return (scanPaths.peek() != null);
    }

    public synchronized int getSize() {
        return scanPaths.size();
    }

}