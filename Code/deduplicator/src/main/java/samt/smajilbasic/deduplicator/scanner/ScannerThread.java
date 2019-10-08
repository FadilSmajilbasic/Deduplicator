package samt.smajilbasic.deduplicator.scanner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Thread for scanning
 */
public class ScannerThread extends Thread {

    private ScannerThreadListener listener;
    private Path rootPath;

    private List<ScannerThread> children = new ArrayList<ScannerThread>();

    private boolean paused = false;

    public ScannerThread(Path rootPath, ScannerThreadListener listener) {
        this.listener = listener;
        this.rootPath = rootPath;
    }

    @Override
    public void run() {

        File[] list = new File(rootPath.toString()).listFiles();
        //TODO: check if root path has been scanned

        for (File file : list) {

            if(isPaused()){

                children.forEach(child -> {
                    child.pause();
                });

                try {
                    this.wait();
                    System.out.println("[INFO] Paused thread with root path: " + rootPath);
                } catch (InterruptedException e) {
        
                }
                
            }

            if (file.isFile()) {
                listener.fileFound(file);
            } else if (file.isDirectory()) {
                System.out.println("Directory " + file.getAbsolutePath());
                ScannerThread thread = new ScannerThread(Paths.get(file.getAbsolutePath()), listener);
                children.add(thread);
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException ie) {
                    System.err.println("[ERROR] Thread interrupted: " + ie.getStackTrace());
                }

            }
        }
        System.out.println("[INFO] Done scanning ThreadId: " + this.getId());
    }


    public void pause(){
        this.paused = true;
    }

    /**
     * @return the paused
     */
    public boolean isPaused() {
        return paused;
    }

    public void resumeScan() {
        this.paused = false;
        this.notifyAll();
        children.forEach(child -> {
            child.resumeScan();
        });
    }

    public void stopScan(){
        this.interrupt();
        children.forEach(child -> child.stopScan());
    }
    

}