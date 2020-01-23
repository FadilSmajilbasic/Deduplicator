package samt.smajilbasic.deduplicator.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import samt.smajilbasic.deduplicator.entity.AuthenticationDetails;
import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.exception.Message;
import samt.smajilbasic.deduplicator.repository.AuthenticationDetailsRepository;
import samt.smajilbasic.deduplicator.repository.GlobalPathRepository;
import samt.smajilbasic.deduplicator.repository.ReportRepository;
import samt.smajilbasic.deduplicator.scanner.ScanListener;
import samt.smajilbasic.deduplicator.scanner.ScanManager;

/**
 * ScanController si occupa di gestire le richieste in entrata che hanno come
 * primo pezzo del percorso "/scan". Usa l'annotazione @RestController per
 * indicare a spring che questa classe è un controller e che dovrà essere
 * inizializzata all'avvio dell'applicazione.
 * 
 * @author Fadil Smajilbasic
 */
@RestController
@RequestMapping(path = "/scan")
public class ScanController implements ScanListener{

    /**
     * L'attributo reportRepository serve al controller per interfacciarsi con la
     * tabella {@link Report} del database. Usa l'annotazione @Autowired per indicare a
     * spring che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    private ReportRepository reportRepository;

    /**
     * L'attributo adr serve al controller per interfacciarsi con la tabella
     * {@link AuthenticationDetails} del database. Usa l'annotazione @Autowired per indicare
     * a spring che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    private AuthenticationDetailsRepository adr;

    /**
     * L'attributo gpr serve al controller per interfacciarsi con la tabella
     * {@link samt.smajilbasic.deduplicator.entity.GlobalPath} del database. Usa l'annotazione @Autowired per indicare a spring
     * che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    private GlobalPathRepository gpr;


    /**
     * L'attributo currentScan contiene lo stato dell'ultima scansione.
     * Viene inizializzato alla chiamata del metodo start.
     */
    private ScanManager currentScan;

     /**
     * L'attributo context contiene il contesto dell'applicazione. Viene usato per
     * trovare l'utente attualmente collegato.
     * 
     */
    @Autowired
    private ApplicationContext context;

    /**
     * L'attributro report contiene il riferimento al {@link Report} dell'ultima scansione inizializzata
     */
    private Report report = null; 

    /**
     * Il metodo start risponde alla richiesta di tipo POST sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scan/start</b>(localhost:8080/scan/start).
     * Il metodo avvia una nuova scansione inizializzando l'attributo currentScan e report.
     * @see #currentScan
     * @see #report
     * @param threadCount il numero di thread da avviare durante la scansione, di default 10, parametro opzionale
     * @return il rapporto creato all'avvio della scansione.
     */
    @PostMapping("/start")
    public @ResponseBody Object start(@RequestParam(required = false) Integer threadCount) {
        
        if (gpr.count() > 0) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUser = authentication.getName();
            AuthenticationDetails internalUser = adr.findById(authenticatedUser).get();

            currentScan = (ScanManager) context.getBean("scanManager");
            report = new Report(internalUser);
            report.setStart(System.currentTimeMillis());
            reportRepository.save(report);

            currentScan.setReportRepository(reportRepository);
            currentScan.setReportId(report.getId());
            currentScan.setThreadCount(threadCount);
            currentScan.setListener(this);
            currentScan.start();
            return report;
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "No path to scan set");
        }
    }

    /**
     * Il metodo stop risponde alla richiesta di tipo POST sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scan/stop</b>(localhost:8080/scan/stop).
     * 
     * Il metodo stop interrompe la scansione distrugendo l'attributo currentScan e report.
     * @see #currentScan
     * @see #report
     * @return il rapporto dell'ultima scansione oppure messaggio d'errore se la scansione non è attiva. 
     */
    @PostMapping("/stop")
    public @ResponseBody Object stop() {
        if (currentScan != null) {
            currentScan.stopScan();

            System.out.println("Waiting finish");
            while (currentScan.isAlive()) {
                long time = System.currentTimeMillis();
                if (System.currentTimeMillis() - time > 100) {
                    System.out.print(".");
                }
            }
            Report report = currentScan.getReport();
            destroyScanManager();
            return report;
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "No scan currently running");
        }
    }

    /**
     * Il metodo pause risponde alla richiesta di tipo POST sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scan/pause</b>(localhost:8080/scan/pause).
     * 
     * Il metodo pause mette in pausa la scansione.
     * @return Messaggio che la scansione è in pause oppore messaggio d'errore se la scansione nom è attiva.
     */
    @PostMapping("/pause")
    public @ResponseBody Message pause() {
        if (currentScan != null) {
            if (!currentScan.isPaused()) {
                currentScan.pauseAll();
                return new Message(HttpStatus.OK, "Scan paused");
            } else {
                return new Message(HttpStatus.OK, "Scan already paused");
            }
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "No scan currently runnin");
        }
    }

    /**
     * Il metodo resume risponde alla richiesta di tipo POST sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scan/resume</b>(localhost:8080/scan/resume).
     * 
     * Il metodo resume prosegue con l'esecuzione dell'ultima scansione nel caso che sia stata messa in pausa.
     * @return Messaggio che la scansione è in pause oppore messaggio d'errore se la scansione non è attiva.
     * @see Message
     */
    @PostMapping("/resume")
    public @ResponseBody Message resume() {
        if (currentScan != null) {
            if (currentScan.isPaused())
                currentScan.resumeAll();
            return new Message(HttpStatus.OK, "Scan resumed");
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "No scan currently running");
        }
    }

    /**
     * Il metodo getStatus risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scan/status</b>(localhost:8080/scan/status).
     * 
     * Il metodo getStatus restituisce lo stato della scansione.
     * @return Messaggio che in base al campo status indica che una scansione è in esecuzione o no, grazie al campo message indica quanti file sono stati scansionati e grazie al campo timestamp indica la data d'esecuzione della scansione.
     * @see Message
     */
    @GetMapping("/status")
    public @ResponseBody Object getStatus() {
        int count = report.getFilesScanned() == null ? 0 : report.getFilesScanned();
        Message response;
        if(report != null){
            response = new Message(HttpStatus.OK, String.valueOf(count));
        }else{
            response = new Message(HttpStatus.NOT_FOUND, String.valueOf(count));
        }
        LocalDateTime time = Instant.ofEpochMilli(report.getStart() == null ? 0 : report.getStart())
                                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
        response.setTimestamp(time);
        return response;
    }

    /**
     * Metodo che viene chiamato quando una scansione viene finita.
     */
    @Override
    public void scanFinished() {
        destroyScanManager();
    }

    /**
     * Questo metodo distrugge il parametro currentScan di tipo ScanManager grazie al contesto dell'applicazione.
     * {@link ScanController#context}
     */
    private void destroyScanManager(){
        BeanDefinitionRegistry factory = (BeanDefinitionRegistry) context.getAutowireCapableBeanFactory();
        ((DefaultListableBeanFactory) factory).destroySingleton("scanManager");

    }
}