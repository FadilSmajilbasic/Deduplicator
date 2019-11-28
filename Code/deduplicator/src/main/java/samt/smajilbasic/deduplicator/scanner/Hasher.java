package samt.smajilbasic.deduplicator.scanner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Scanner;

import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.entity.File;
import samt.smajilbasic.deduplicator.repository.FileRepository;

/**
 * Hasher
 */
public class Hasher extends Thread {

    private LinkedList<java.io.File> files;
    ScannerThreadListener stl;
    Report report;

    private FileRepository fileRepository;

    public Hasher(LinkedList<java.io.File> files, Report report, ScannerThreadListener stl,
            FileRepository fileRepository) {
        this.files = files;
        this.report = report;
        this.stl = stl;
        this.fileRepository = fileRepository;
    }

    public static String getHash(RandomAccessFile file, String mode) throws NoSuchAlgorithmException {

        MessageDigest messageDigest = MessageDigest.getInstance(mode);

        int buff = 16384;
        try {

            byte[] buffer = new byte[buff];

            long read = 0;

            long end = file.length();
            int unitsize;
            long start = System.currentTimeMillis();
            while (read < end) {
                unitsize = (int) (((end - read) >= buff) ? buff : (end - read));
                file.read(buffer, 0, unitsize);
                messageDigest.update(buffer, 0, unitsize);
                read += unitsize;
            }
            System.out.println("Hash duration: "+(System.currentTimeMillis()-start) + "ms");

        } catch (FileNotFoundException fnfE) {
            System.out.println("[ERROR] Hasher: Item not found");
        
        } catch (IOException ioE) {
            System.out.println("[ERROR] Hasher: IO Exception");
        }

        byte[] digest = messageDigest.digest();

        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < digest.length; i++) {
            hexString.append(Integer.toHexString(0xFF & digest[i]));
        }

        return hexString.toString();
    }

    @Override
    public void run() {

        while (files.peek() != null) {
            java.io.File file = files.poll();
            Long lastModified = file.lastModified();

            try {
                RandomAccessFile fileRAF = new RandomAccessFile(file.getAbsolutePath(), "r");
                String hash = Hasher.getHash(fileRAF, "MD5");
                long size = fileRAF.length();

                fileRAF.close();

                File record = new File(file.getAbsolutePath(), lastModified, hash, size, report);
                fileRepository.save(record);
                stl.addFilesScanned();

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
    }
}