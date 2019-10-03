package samt.smajilbasic.deduplicator.scanner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Thread for scanning
 */
public class ScannerThread extends Thread {
    
    private ScannerThreadListener stl;
    private Path rootPath;

    public ScannerThread(Path rootPath,ScannerThreadListener stl){
        this.stl = stl;
        this.rootPath = rootPath;
    }

    @Override
    public void run() {
        File[] list = new File(rootPath.toString()).listFiles();
        for (File file : list) {
            if (file.isFile()) {
                System.out.println("File " + file.getName());
                stl.fileFound(file);
            } else if (file.isDirectory()) {
                System.out.println("Directory " + file.getName());
                new ScannerThread(Paths.get(file.getAbsolutePath()), stl);
            }
        }
        stl.work();
    }

    
}