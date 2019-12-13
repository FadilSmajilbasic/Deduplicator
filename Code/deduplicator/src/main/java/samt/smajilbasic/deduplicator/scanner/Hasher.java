package samt.smajilbasic.deduplicator.scanner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.entity.File;
import samt.smajilbasic.deduplicator.repository.FileRepository;

/**
 * Hasher
 */
public class Hasher extends Thread {

    private java.io.File file;
    ScannerThreadListener stl;
    Report report;

    private FileRepository fileRepository;

    Object monitor;

    boolean paused;

    private final static int BUFFER_SIZE = 32768;

    public Hasher(java.io.File file, Report report, ScannerThreadListener stl, FileRepository fileRepository,
            Object monitor) {
        this.file = file;
        this.report = report;
        this.stl = stl;
        this.fileRepository = fileRepository;
        this.monitor = monitor;
    }

    public String getHash(RandomAccessFile file, String mode) throws NoSuchAlgorithmException {

        MessageDigest messageDigest = MessageDigest.getInstance(mode);

        
        try {
            
            byte[] buffer = new byte[BUFFER_SIZE];
            long read = 0;

            long end = file.length();
            int unitsize;
            while (read < end) {
                checkPaused();
                unitsize = (int) (((end - read) >= BUFFER_SIZE) ? BUFFER_SIZE : (end - read));
                file.read(buffer, 0, unitsize);
                messageDigest.update(buffer, 0, unitsize);
                read += unitsize;
            }

        } catch (FileNotFoundException fnfE) {
            System.out.println("[ERROR] Hasher: Item not found");

        } catch (IOException ioE) {
            System.out.println("[ERROR] Hasher: IO Exception");
        }

        byte[] digest = messageDigest.digest();

        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < digest.length; i++) {
            hexString.append(String.format("%02x",digest[i]));
        }

        return hexString.toString();
    }

    @Override
    public void run() {

        Long lastModified = file.lastModified();

        try {
            RandomAccessFile fileRAF = new RandomAccessFile(file.getAbsolutePath(), "r");
            String hash = getHash(fileRAF, "MD5");
            long size = fileRAF.length();

            fileRAF.close();

            File record = new File(file.getAbsolutePath(), lastModified, hash, size, report);
            fileRepository.save(record);
            stl.addFilesScanned(0);

        } catch (NoSuchAlgorithmException nsae) {
            System.err.println("[ERROR] Unable to hash file: " + nsae.getMessage());

        } catch (IOException ioe) {
            System.err.println("[ERROR] Unable to read file: " + ioe.getMessage());
        } catch (NullPointerException npe) {
            System.err.println("[ERROR] Unable to save file: " + npe.getMessage());
        } catch (OutOfMemoryError ex) {
            System.out.println("[ERROR] File too big to calculate hash: " + file.getAbsolutePath());
            System.out.println(ex.getMessage());
        }
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

    public void pause() {
        if (isAlive()) {
            this.paused = true;
            System.out.println("[INFO] Paused hasher ");
        }

    }

    /**
     * @return the paused
     */
    public boolean isPaused() {
        return paused;
    }
}