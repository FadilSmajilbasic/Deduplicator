package samt.smajilbasic.deduplicator.scanner;

import java.io.File;

interface ScannerThreadListener {

    public void fileFound(File file);
}