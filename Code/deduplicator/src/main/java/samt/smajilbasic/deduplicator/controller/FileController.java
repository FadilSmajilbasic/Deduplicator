package samt.smajilbasic.deduplicator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import samt.smajilbasic.deduplicator.Validator;
import samt.smajilbasic.deduplicator.entity.File;
import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.exception.Response;
import samt.smajilbasic.deduplicator.repository.FileRepository;
import samt.smajilbasic.deduplicator.repository.ReportRepository;

/**
 * FileController si occupa di gestire le richieste in entrata che hanno come
 * primo pezzo del percorso "/file". Usa l'annotazione @RestController per
 * indicare a spring che questa classe è un controller e che dovrà essere
 * inizializzata all'avvio dell'applicazione.
 * 
 * @author Fadil Smajilbasic
 */
@RestController
@RequestMapping(path = "/file")
public class FileController {

    /**
     * L'attributo fileRepository serve al controller per interfacciarsi con la
     * tabella File del database. Usa l'annotazione @Autowired per indicare a spring
     * che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    FileRepository fileRepository;

    /**
     * L'attributo reportRepository serve al controller per interfacciarsi con la
     * tabella Report del database. Usa l'annotazione @Autowired per indicare a
     * spring che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    ReportRepository reportRepository;

    /**
     * Il metodo getAll risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/file</b>(localhost:8080/file/).
     * 
     * @return tutte i file contenuti nella tabella File del database.
     */
    @GetMapping()
    public Iterable<File> getAll() {
        return fileRepository.findAll();
    }

    /**
     * Il metodo get risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/file/&lt;id&gt;</b> (localhost:8080/file/45).
     * 
     * @return ritorna il file trovato nella tabella File in base al id passato come
     *         parametro, se non esiste ritorna un messaggio d'errore.
     */
    @GetMapping(value = "/{id}")
    public Object get(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if (intId != null && reportRepository.existsById(intId))
            return ((Report) reportRepository.findById(intId).get()).getFile();
        else
            return new ResponseEntity<Response>(new Response("Invalid report id"), HttpStatus.NOT_FOUND);

    }

}
