package samt.smajilbasic.deduplicator.controller;

import samt.smajilbasic.deduplicator.exception.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import samt.smajilbasic.deduplicator.PathType;
import samt.smajilbasic.deduplicator.Validator;
import samt.smajilbasic.deduplicator.entity.GlobalPath;
import samt.smajilbasic.deduplicator.repository.GlobalPathRepository;

/**
 * PathController si occupa di gestire le richieste in entrata che hanno come
 * primo pezzo del percorso "/path". Usa l'annotazione @RestController per
 * indicare a spring che questa classe è un controller e che dovrà essere
 * inizializzata all'avvio dell'applicazione.
 * 
 * @author Fadil Smajilbasic
 */
@RestController
@RequestMapping(path = "/path")
public class PathController {

    /**
     * L'attributo gpr serve al controller per interfacciarsi con la tabella
     * GlobalPath del database. Usa l'annotazione @Autowired per indicare a spring
     * che questo parametro dovrà essere creato come Bean e dovrà essere
     * inizializzato alla creazione della classe.
     */
    @Autowired
    GlobalPathRepository gpr;

    /**
     * Il metodo getAll risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/path</b>(localhost:8080/path/).
     * 
     * @return tutti {@link GlobalPath} contenuti nella tabella GlobalPath del
     *         database.
     */
    @GetMapping()
    public @ResponseBody Iterable<GlobalPath> getAll() {
        return gpr.findAll();
    }

    /**
     * Il metodo get risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/path/&lt;percorso&gt;</b>
     * (localhost:8080/path/&#47;home&#47;user&#47;Desktop&#47;).
     * 
     * @param path il percorso con il quale verrà cercaro il valore nella tabella
     *             GlobalPath.
     * @return l'elemento richiesto della tabella GlobalPath, messaggio d'errore
     *         altrimenti.
     */
    @GetMapping(value = "/{path}")
    public @ResponseBody Object get(@RequestParam String path) {

        path = path.replaceAll("&#47;", File.separator).trim();
        if (Validator.getPathType(path) != PathType.Invalid) {
            if (gpr.existsById(path))
                return gpr.findById(path).get();
            else {
                return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Path doesn't exist: " + path);
            }
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid path format: " + path);
        }
    }

    /**
     * Il metodo insert risponde alla richiesta di tipo PUT sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/path</b>(localhost:8080/path/). Il metodo
     * inserisce una nuova azione nel database GlobalPath.
     * 
     * @param path       il percorso del oggetto, passato come parametro del body
     *                   della richiesta.
     * @param ignorePath se è da ignorare oppure da scansionare, passato come
     *                   parametro del body della richiesta.
     * @return l'oggetto inserito oppure un messaggio d'errore in base al errore.
     */
    @PutMapping()
    public @ResponseBody Object insert(@RequestParam String path, @RequestParam String ignorePath) {
        path = path.replaceAll("&#47;", File.separator).trim();
        try {
            Path p = Paths.get(path);

            if (!gpr.existsById(p.toAbsolutePath().toString())) {
                if (Validator.getPathType(p.toAbsolutePath().toString()) != PathType.Invalid) {
                    if (Files.isReadable(p)) {
                        gpr.save(new GlobalPath(p.toAbsolutePath().toString(), (ignorePath.equals("true"))));
                        return get(path);
                    } else {
                        return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "path not readable.: " + path);
                    }
                } else {
                    return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid path format: " + path);
                }
            } else {
                return new Message(HttpStatus.BAD_REQUEST, "Path already present in database: " + path);
            }
        } catch (InvalidPathException ipe) {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid path format: " + path);
        }
    }

    /**
     * Il metodo delete risponde alla richiesta di tipo DELETE sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/path</b>(localhost:8080/path/). Il metodo rimuove
     * un oggetto dalla tabella GlobalPath in base al percorso passato come
     * parametro nel body della richiesta.
     * 
     * @param path il precorso dell'oggetto da rimuovere
     * @return l'oggetto eliminato oppure il messaggio d'errore
     */
    @DeleteMapping()
    public @ResponseBody Object delete(@RequestParam String path) {

        PathType type = Validator.getPathType(path);
        path = path.replaceAll("&#47;", File.separator).trim();

        if (gpr.existsById(path) && type != PathType.Invalid) {
            GlobalPath entry = gpr.findById(path).get();
            gpr.delete(entry);
            return entry;
        } else {
            return new Message(HttpStatus.BAD_REQUEST, "Invalid path: " + path);
        }
    }
}