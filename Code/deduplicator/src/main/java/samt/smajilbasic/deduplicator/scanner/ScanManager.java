package samt.smajilbasic.deduplicator.scanner;

import samt.smajilbasic.deduplicator.entity.File;
import samt.smajilbasic.deduplicator.entity.GlobalPath;
import samt.smajilbasic.deduplicator.entity.Report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import samt.smajilbasic.deduplicator.repository.FileRepository;
import samt.smajilbasic.deduplicator.repository.GlobalPathRepository;

/**
 * Logger
 */

@Service
public class ScanManager extends Thread implements ScannerThreadListener {

    @Autowired
    FileRepository fr;

    @Autowired
    GlobalPathRepository gpr;

    private Iterator<GlobalPath> paths;

    private LinkedList<java.io.File> files = new LinkedList<java.io.File>();

    private Report report;

    public ScanManager(Report report) {
        this.report = report;
    }

    @Override
    public void run() {

        System.out.println("count: " +  gpr.count());
        paths = gpr.findAll().iterator();

        try {
            while (paths.hasNext()) {
                ScannerThread thread = new ScannerThread(Paths.get(paths.next().getPath()), this);

                thread.join();

            }

            work();
        } catch (InterruptedException ie) {
            System.out.println("[ERROR] Thread interrupted: " + ie.getStackTrace());
        } finally {
            work();
        }

    }

    @Override
    public void fileFound(java.io.File file) {
        files.add(file);
    }

    private void work() {
        while (files.peek() != null) {
            java.io.File file = files.poll();
            try {
                String hash = Hasher.getFileHash(Paths.get(file.getAbsolutePath()));
                Timestamp lastModified = new Timestamp(file.lastModified());
                int size = (Files.readAllBytes(Paths.get(file.getAbsolutePath().toString()))).length;
                File record = new File(file.getAbsolutePath(), lastModified, hash, size, report);
                fr.save(record);
            } catch (NoSuchAlgorithmException nsae) {
                System.err.println("[ERROR] Unable to hash file: " + nsae.getMessage());

            } catch (IOException ioe) {
                System.err.println("[ERROR] Unable to read file: " + ioe.getMessage());
            } finally {
                System.err.println("[ERROR] Unable to save file: " + file.getAbsolutePath());
            }
        }

    }

    /**
     * @return the report
     */
    public Report getReport() {
        return report;
    }

}