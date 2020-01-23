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
 * La classe hasher si occupa di generare le hash dei file trovati e salvare
 * questi nella tabella File.
 * 
 */
public class Hasher extends Thread {

    /**
     * L'attributo file definisce il file dal quale verrà creato il hash.
     */
    private java.io.File file;

    /**
     * L'attributo stl definisce il listener che verrà avvisto quando questo oggetto
     * finisce la usa esecuzione.
     */
    private ScannerThreadListener stl;
    /**
     * L'attributo report definisce il riferimento al rapporto che verrà impostato
     * al file.
     */
    private Report report;

    /**
     * L'attributo fileRepository definisce il repository usato per
     * l'interfacciamento con la tabella File.
     */
    private FileRepository fileRepository;

    /**
     * L'attributo monitor viene usato per controllare se l'esecuzione dovrà essere
     * messa in pausa oppure ripresa.
     */
    Object monitor;

    /**
     * L'attributo paused definisce se L'esecuzione è in pausa oppure no.
     */
    private boolean paused;

    /**
     * L'attributo BUFFER_SIZE definisce la grandezza del buffer per la lettura dei
     * contenuti di file grandi, per la generazione del hash.
     */
    private final static int BUFFER_SIZE = 32768;

    /**
     * Metodo costruttore per la classe Hasher.
     * 
     * @param file           il file scansionato.
     * @param report         il rapporto nel quale è stato trovato il file.
     * @param stl            il listener che verrà notifiacto quando l'esecuzione
     *                       finisce.
     * @param fileRepository il repository per slavare il file nel databse.
     * @param monitor        l'oggetto che verrà controllato nel caso che
     *                       l'esecuzione viene fermata.
     */
    public Hasher(java.io.File file, Report report, ScannerThreadListener stl, FileRepository fileRepository,
            Object monitor) {
        this.file = file;
        this.report = report;
        this.stl = stl;
        this.fileRepository = fileRepository;
        this.monitor = monitor;
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

    /**
     * Metodo che viene usato per controllare lo stato del monitor per vedere se
     * bisogna fermare l'esecuzione.
     */
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

    /**
     * Il metodo pause imposta l'attributo paused a true.
     */
    public void pause() {
        if (isAlive()) {
            this.paused = true;
            System.out.println("[INFO] Paused hasher ");
        }

    }

    /**
     * Metodo getter per la variabile paused.
     * 
     * @return ture se l'esecuzione è ferma, false altrimenti.
     */
    public boolean isPaused() {
        return paused;
    }
}