package samt.smajilbasic.deduplicator.scanner;

import samt.smajilbasic.deduplicator.entity.File;
import samt.smajilbasic.deduplicator.entity.GlobalPath;
import samt.smajilbasic.deduplicator.entity.Report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import samt.smajilbasic.deduplicator.repository.FileRepository;
import samt.smajilbasic.deduplicator.repository.GlobalPathRepository;
import samt.smajilbasic.deduplicator.repository.ReportRepository;

/**
 * ScanManager
 */

@Service
public class ScanManager extends Thread implements ScannerThreadListener {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    GlobalPathRepository gpr;

    private Iterator<GlobalPath> paths;

    private LinkedList<java.io.File> files = new LinkedList<java.io.File>();

    private ReportRepository reportRepository;

    private int reportId;

    private int filesFound;

    private List<ScannerThread> rootThreads = new ArrayList<ScannerThread>();

    public ScanManager() {
        super();
    }

    @Override
    public void run() {

        paths = gpr.findAll().iterator();

        try {
            while (paths.hasNext()) {
                ScannerThread thread = new ScannerThread(Paths.get(paths.next().getPath()), this);

                rootThreads.add(thread);
                thread.join();
                thread.start();
            }

        } catch (InterruptedException ie) {
            System.err.println("[ERROR] Thread interrupted: " + ie.getStackTrace());
        } finally {
            System.out.println("[INFO] Sending work command");
            work();
        }

    }

    @Override
    public void fileFound(java.io.File file) {      
        System.out.println("Found new file: " + file.getAbsolutePath().toString());
        files.add(file);
    }

    private void work() {
        Report report = getReport();
        while (files.peek() != null) {
            java.io.File file = files.poll();
            try {
                if(!fileRepository.existsById(file.getAbsolutePath())){
                    String hash = Hasher.getFileHash(Paths.get(file.getAbsolutePath()));
                    Timestamp lastModified = new Timestamp(file.lastModified());
                    int size = (Files.readAllBytes(Paths.get(file.getAbsolutePath().toString()))).length;
                    File record = new File(file.getAbsolutePath(), lastModified, hash, size, report);
                    
                    fileRepository.save(record);
                    filesFound++;
                }
            } catch (NoSuchAlgorithmException nsae) {
                System.err.println("[ERROR] Unable to hash file: " + nsae.getMessage());

            } catch (IOException ioe) {
                System.err.println("[ERROR] Unable to read file: " + ioe.getMessage());
            }
        }

        //TODO: call method for duplicate check
        System.out.println("Done");
        report.setDuration((System.currentTimeMillis() - getReport().getStart().getTime()));
        report.setDuplicateCount(filesFound);
        reportRepository.save(report);
    }

    public void pauseAll(){
        rootThreads.forEach(rootThread -> rootThread.pause());
    }

    public void resumeAll() {
        rootThreads.forEach(rootThread -> rootThread.resumeScan());
	}

    /**
     * @param reportId the reportId to set
     */
    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    /**
     * @param reportRepository the reportRepository to set
     */
    public void setReportRepository(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * @return the reportId
     */
    public int getReportId() {
        return reportId;
    }

    /**
     * @return the reportRepository
     */
    public ReportRepository getReportRepository() {
        return reportRepository;
    }

    public Report getReport() {
        // TODO: check if null
        return reportRepository.findById(reportId).get();
    }

    public void stopScan(){
        rootThreads.forEach(rootThread->rootThread.stop());

    }



}