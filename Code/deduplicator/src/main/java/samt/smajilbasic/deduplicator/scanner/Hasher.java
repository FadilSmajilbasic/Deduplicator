package samt.smajilbasic.deduplicator.scanner;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Hasher
 */
public class Hasher {

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
}