package samt.smajilbasic.deduplicator.scanner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

/**
 * Logger
 */
public class FileLogger implements ScannerThreadListener {

    private Statement mysqlStatement;
    private LinkedList<File> files = new LinkedList<File>();

    public FileLogger(Statement mysqlStatement) {
        this.mysqlStatement = mysqlStatement;
    }

    @Override
    public void fileFound(File file) {
        files.add(file);
    }

    public void work() {
        while (files.peek() != null) {
            File file = files.poll();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                String hash = Hasher.getFileHash(Paths.get(file.getAbsolutePath()));
                Long lastModified = file.lastModified();
                int size = (Files.readAllBytes(Paths.get(file.getAbsolutePath().toString()))).length;
                String query = "INSERT into file(" + file.getAbsolutePath() + "," + lastModified + ");";

                mysqlStatement.executeQuery(query);

            } catch (NoSuchAlgorithmException nsae) {
                System.out.println("Bruh u don't have md5: " + nsae.getMessage());
            } catch (IOException ioe) {
                System.out.println("Bruh u got an io exception: " + ioe.getMessage());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}