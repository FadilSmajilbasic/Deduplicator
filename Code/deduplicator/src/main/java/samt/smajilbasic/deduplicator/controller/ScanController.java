package samt.smajilbasic.deduplicator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import samt.smajilbasic.deduplicator.entity.AuthenticationDetails;
import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.exception.Response;
import samt.smajilbasic.deduplicator.repository.AuthenticationDetailsRepository;
import samt.smajilbasic.deduplicator.repository.FileRepository;
import samt.smajilbasic.deduplicator.repository.GlobalPathRepository;
import samt.smajilbasic.deduplicator.repository.ReportRepository;
import samt.smajilbasic.deduplicator.scanner.ScanListener;
import samt.smajilbasic.deduplicator.scanner.ScanManager;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class ScanController implements ScanListener {

    /**
     * L'attributo reportRepository serve al controller per interfacciarsi con la
     * tabella {@link Report} del database. Usa l'annotazione @Autowired per
     * indicare a spring che questo parametro dovrà essere creato come Bean e dovrà
     * essere inizializzato alla creazione della classe.
     */
    @Autowired
    private ReportRepository reportRepository;

    /**
     * L'attributo fileRepository serve al controller per interfacciarsi con la
     * tabella File del database. Usa l'annotazione @Autowired per indicare a spring
     * che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    private FileRepository fileRepository;

    /**
     * L'attributo adr serve al controller per interfacciarsi con la tabella
     * {@link AuthenticationDetails} del database. Usa l'annotazione @Autowired per
     * indicare a spring che questo parametro dovrà essere creato come Bean e dovrà
     * essere inizializzato alla creazione della classe.
     */
    @Autowired
    private AuthenticationDetailsRepository adr;

    /**
     * L'attributo gpr serve al controller per interfacciarsi con la tabella
     * {@link samt.smajilbasic.deduplicator.entity.GlobalPath} del database. Usa
     * l'annotazione @Autowired per indicare a spring che questo parametro dovrà
     * essere creato come Bean e dovrà essere inizializzato alla creazione della
     * classe.
     */
    @Autowired
    private GlobalPathRepository gpr;

    /**
     * L'attributo currentScan contiene lo stato dell'ultima scansione. Viene
     * inizializzato alla chiamata del metodo start.
     */
    private ScanManager currentScan;

    /**
     * L'attributo context contiene il contesto dell'applicazione. Viene usato per
     * trovare l'utente attualmente collegato.
     */
    @Autowired
    private ApplicationContext context;

    /**
     * L'attributro report contiene il riferimento al {@link Report} dell'ultima
     * scansione inizializzata
     */
    private Report report = null;

    /**
     * Il metodo start risponde alla richiesta di tipo POST sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scan/start</b>(localhost:8080/scan/start). Il
     * metodo avvia una nuova scansione inizializzando l'attributo currentScan e
     * report.
     *
     * @param threadCount il numero di thread da avviare durante la scansione, di
     *                    default 10, parametro opzionale
     * @return il rapporto creato all'avvio della scansione.
     * @see #currentScan
     * @see #report
     */
    @PostMapping("/start")
    public Object start(@RequestParam(required = false) Integer threadCount) {

        currentScan = (ScanManager) context.getBean("scanManager");
        if (gpr.count() > 0) {
            if (!currentScan.isAlive()) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null) {
                    Optional<AuthenticationDetails> tempUser = adr.findById(authentication.getName());
                    report = tempUser.map(Report::new).orElseGet(() -> new Report(adr.findById("scheduler").get()));
                } else {
                    Logger.getGlobal().log(Level.WARNING, "No user set");
//                    return new ResponseEntity<Response>(new Response("No user set"), HttpStatus.INTERNAL_SERVER_ERROR);
                    report = new Report();
                }
                report.setStart(System.currentTimeMillis());
                reportRepository.save(report);
                currentScan.setReportRepository(reportRepository);
                currentScan.setReportId(report.getId());
                currentScan.setThreadCount(threadCount);
                currentScan.setListener(this);
                currentScan.start();
                return report;
            } else {
                Logger.getGlobal().log(Level.WARNING, "Scan is already running");
                return new ResponseEntity<Response>(new Response("Scan is already running"),
                    HttpStatus.ALREADY_REPORTED);
            }
        } else {
            Logger.getGlobal().log(Level.SEVERE, "No path to scan set");
            return new ResponseEntity<Response>(new Response("No path to scan set"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Il metodo stop risponde alla richiesta di tipo POST sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scan/stop</b>(localhost:8080/scan/stop).
     * <p>
     * Il metodo stop interrompe la scansione distrugendo l'attributo currentScan e
     * report.
     *
     * @return il rapporto dell'ultima scansione oppure messaggio d'errore se la
     * scansione non è attiva.
     * @see #currentScan
     * @see #report
     */
    @PostMapping("/stop")
    public Object stop() {
        if (currentScan != null) {
            currentScan.stopScan();

            System.out.println("Waiting finish");

            Report report = currentScan.getReport();
            destroyScanManager();
            return report;
        } else {
            return new ResponseEntity<Response>(new Response("No scan currently running"),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Il metodo pause risponde alla richiesta di tipo POST sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scan/pause</b>(localhost:8080/scan/pause).
     * <p>
     * Il metodo pause mette in pausa la scansione.
     *
     * @return Messaggio che la scansione è in pause oppore messaggio d'errore se la
     * scansione nom è attiva.
     */
    @PostMapping(path = "/pause", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> pause() {
        if (currentScan != null) {
            if (!currentScan.isPaused()) {
                currentScan.pauseScan();
                return new ResponseEntity<Response>(new Response("Scan paused"), HttpStatus.OK);
            } else {
                return new ResponseEntity<Response>(new Response("Scan already paused"), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<Response>(new Response("No scan currently running"),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Il metodo resume risponde alla richiesta di tipo POST sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scan/resume</b>(localhost:8080/scan/resume).
     * <p>
     * Il metodo resume prosegue con l'esecuzione dell'ultima scansione nel caso che
     * sia stata messa in pausa.
     *
     * @return Messaggio che la scansione è in pause oppore messaggio d'errore se la
     * scansione non è attiva.
     * @see Response
     */
    @PostMapping("/resume")
    public ResponseEntity<Response> resume() {
        if (currentScan != null) {
            if (currentScan.isPaused())
                currentScan.resumeScan();
            return new ResponseEntity<Response>(new Response("Scan resumed"), HttpStatus.OK);
        } else {
            return new ResponseEntity<Response>(new Response("No scan currently running"),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Il metodo getStatus risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scan/status</b>(localhost:8080/scan/status).
     * <p>
     * Il metodo getStatus restituisce lo stato della scansione.
     *
     * @return Messaggio che in base al campo status indica che una scansione è in
     * esecuzione o no, grazie al campo message indica quanti file sono
     * stati scansionati e grazie al campo timestamp indica la data
     * d'esecuzione della scansione.
     * @see Response
     */
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        try {
            if (currentScan != null) {
                System.out.println("report id " + currentScan.getReportId());
                Object scanStatus = new Object() {
                    public int fileCount = fileRepository.findByReport(report);
                    public float progress = currentScan.scanProgress;
                    public long timestamp = report.getStart();
                    public int reportId = report.getId();
                };

                Jackson2JsonEncoder encoder = new Jackson2JsonEncoder();
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

                return ResponseEntity.ok().headers(headers).body(encoder.getObjectMapper().writeValueAsString(scanStatus));
            } else {
                Logger.getGlobal().log(Level.INFO, "Scan is not running");
                return new ResponseEntity<Response>(new Response("Scan is not running"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception ex) {
            Logger.getGlobal().log(Level.WARNING, "Unable to retrieve scan status");
            return new ResponseEntity<Response>(new Response("Unable to retrieve scan status"),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Metodo che viene chiamato quando una scansione viene finita.
     */
    @Override
    public void scanFinished() {
        destroyScanManager();
    }

    /**
     * Questo metodo distrugge il parametro currentScan di tipo ScanManager grazie
     * al contesto dell'applicazione. {@link ScanController#context}
     */
    private void destroyScanManager() {
        BeanDefinitionRegistry factory = (BeanDefinitionRegistry) context.getAutowireCapableBeanFactory();
        ((DefaultListableBeanFactory) factory).destroySingleton("scanManager");
    }
}
