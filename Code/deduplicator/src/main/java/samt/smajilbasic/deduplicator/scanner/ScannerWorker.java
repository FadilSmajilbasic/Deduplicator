package samt.smajilbasic.deduplicator.scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.repository.FileRepository;

/**
 * Thread for scanning
 */
public class ScannerWorker extends Thread {

    private Path rootPath;
    private Report report;
    private FileRepository fileRepository;


    private Boolean paused = false;
    Object monitor;
    private FilesScanner scanner;
    private final static int BUFFER_SIZE = 32768;
    /**
     * Default timeout for the scanning thread pool given in seconds
     */

    public ScannerWorker(FilesScanner scanner, FileRepository fileRepository, Object monitor, Report report) {
        this.report = report;
        this.fileRepository = fileRepository;
        this.monitor = monitor;
        this.scanner = scanner;
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

    @Override
    public void run() {
        synchronized (scanner) {
             while(scanner.hasNext() && !scanner.isAlive()){
            
                File file = scanner.getNextFile();
                if (file.isFile()) {

                    Long lastModified = file.lastModified();
                    try {
                        RandomAccessFile fileRAF = new RandomAccessFile(file.getAbsolutePath(), "r");
                        String hash = getHash(fileRAF, "MD5");
                        long size = fileRAF.length();

                        fileRAF.close();

                        samt.smajilbasic.deduplicator.entity.File record = new samt.smajilbasic.deduplicator.entity.File(
                                file.getAbsolutePath(), lastModified, hash, size, report);
                        fileRepository.save(record);

                    } catch (NoSuchAlgorithmException nsae) {
                        System.err.println("[ERROR] Unable to hash file: " + nsae.getMessage());
                    } catch (IOException ioe) {
                        System.err.println("[ERROR] Unable to read file: " + ioe.getMessage());
                    } catch (NullPointerException npe) {
                        System.err.println("[ERROR] Unable to save file: " + npe.getMessage());
                    }
                }
                System.out.println("Scanning finished: " + file.getAbsolutePath());
            }
            
        }
        System.out.println("Thread finished");
    }

    /**
     * Metodo che restituisce il hash del file che gli viene passato come parametro.
     * 
     * @param file il file dal quale verrà generato il hash.
     * @param mode il tipo di hash da generare.
     * @return il hash del cententuto del file in formato stringa.
     * @throws NoSuchAlgorithmException Eccezzione tirata in caso che il tipo di
     *                                  hash non è disponibile.
     */
    public String getHash(RandomAccessFile file, String mode) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(mode);

        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            long read = 0;

            long end = file.length();
            int unitsize;

            while (read < end) {
                checkPaused();
                unitsize = (int) (((end - read) >= BUFFER_SIZE) ? BUFFER_SIZE : (end - read)); // controllo se sono
                                                                                               // arrivato in fondo al
                                                                                               // file.
                file.read(buffer, 0, unitsize); // leggo un chunk del file definito dall'attributo BUFFER_SIZE
                messageDigest.update(buffer, 0, unitsize); // aggiorno il hash con i nuovi dati letti.
                read += unitsize; // sposto il buffer al prossimo chunk di dati.
            }

        } catch (FileNotFoundException fnfE) {
            System.out.println("[ERROR] Hasher: Item not found");

        } catch (IOException ioE) {
            System.out.println("[ERROR] Hasher: IO Exception");
        }

        byte[] digest = messageDigest.digest();

        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < digest.length; i++) {
            hexString.append(String.format("%02x", digest[i]));
        }

        return hexString.toString();
    }

    public synchronized void pause() {
        if (isAlive()) {
            this.paused = true;
            System.out.println("[INFO] Paused thread with root path: " + rootPath);
        }
    }

    /**
     * @return the paused
     */
    public synchronized boolean isPaused() {
        return this.paused;
    }

    public void resumeScan() {
        this.paused = false;
    }

    public synchronized void stopScan() {
        System.out.println("[INFO] Stopped thread" + getId());
        if (!Thread.interrupted())
            interrupt();
    }

}