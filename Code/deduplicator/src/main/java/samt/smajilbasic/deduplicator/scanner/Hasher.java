package samt.smajilbasic.deduplicator.scanner;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.entity.File;
import samt.smajilbasic.deduplicator.repository.FileRepository;

/**
 * Hasher
 */
public class Hasher extends Thread{


    private LinkedList<java.io.File> files;
    ScannerThreadListener stl;
    Report report;

    private FileRepository fileRepository;

    public Hasher(LinkedList<java.io.File> files,Report report,ScannerThreadListener stl,FileRepository fileRepository) {
        this.files = files;
        this.report = report;
        this.stl = stl;
        this.fileRepository = fileRepository;
    }
    public static String getHash(byte[] bytes, String mode) throws NoSuchAlgorithmException {
        String hashtext;

        MessageDigest method = MessageDigest.getInstance(mode);

        byte[] messageDigest = method.digest(bytes);

        BigInteger no = new BigInteger(1, messageDigest);

        hashtext = no.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }

        return hashtext;
    }

    public static String getFileHash(Path file) throws NoSuchAlgorithmException, IOException {
        return Hasher.getHash(Files.readAllBytes(file), "MD5");

    }

    @Override
    public void run() {
        
        while (files.peek() != null) {
            java.io.File file = files.poll();
            Long lastModified = file.lastModified();

            try {
                String hash = Hasher.getFileHash(Paths.get(file.getAbsolutePath()));
                int size = (Files.readAllBytes(Paths.get(file.getAbsolutePath().toString()))).length;
                File record = new File(file.getAbsolutePath(), lastModified, hash, size,report);
                fileRepository.save(record);
                stl.addFilesScanned();

            } catch (NoSuchAlgorithmException nsae) {
                System.err.println("[ERROR] Unable to hash file: " + nsae.getMessage());

            } catch (IOException ioe) {
                System.err.println("[ERROR] Unable to read file: " + ioe.getMessage());
            } catch (NullPointerException npe) {
                System.err.println("[ERROR] Unable to save file: " + npe.getMessage());
            }
        }

        System.out.println("[INFO] Done saving files");
    }
}