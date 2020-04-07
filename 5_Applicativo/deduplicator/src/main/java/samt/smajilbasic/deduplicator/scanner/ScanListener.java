package samt.smajilbasic.deduplicator.scanner;

/**
 * L'interfaccia ScanListener definisce un listener che aspetta la fine d'esecuione del {@link ScannerWorker}. 
 */
public interface ScanListener {
    public void scanFinished();
}