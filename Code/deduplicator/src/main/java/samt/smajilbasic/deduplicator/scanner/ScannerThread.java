package samt.smajilbasic.deduplicator.scanner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Thread for scanning
 */
public class ScannerThread extends Thread {
    
    private ScannerThreadListener listener;
    private Path rootPath;

    public ScannerThread(Path rootPath,ScannerThreadListener listener){
        this.listener = listener;
        this.rootPath = rootPath;
    }

    @Override
    public void run() {
        File[] list = new File(rootPath.toString()).listFiles();
        for (File file : list) {
            if (file.isFile()) {
                System.out.println("File " + file.getName());
                listener.fileFound(file);
            } else if (file.isDirectory()) {
                System.out.println("Directory " + file.getName());
                ScannerThread thread = new ScannerThread(Paths.get(file.getAbsolutePath()), listener);
                try{
                thread.join();
                }catch(InterruptedException ie){
                    System.out.println("[ERROR] Thread interrupted: " + ie.getStackTrace());
                }
            }
        }
    }

    
}