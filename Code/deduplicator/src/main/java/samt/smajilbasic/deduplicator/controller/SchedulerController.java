package samt.smajilbasic.deduplicator.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import samt.smajilbasic.deduplicator.Validator;
import samt.smajilbasic.deduplicator.timer.ScheduleChecker;
import samt.smajilbasic.deduplicator.entity.Scheduler;
import samt.smajilbasic.deduplicator.exception.Response;
import samt.smajilbasic.deduplicator.repository.SchedulerRepository;

/**
 * SchedulerController si occupa di gestire le richieste in entrata che hanno
 * come primo pezzo del percorso "/scheduler". Usa l'annotazione @RestController
 * per indicare a spring che questa classe è un controller e che dovrà essere
 * inizializzata all'avvio dell'applicazione.
 *
 * @author Fadil Smajilbasic
 */
@RestController
@RequestMapping(path = "/scheduler")
public class SchedulerController {

    /**
     * L'attributo schedulerRepository serve al controller per interfacciarsi con la
     * tabella {@link Scheduler} del database. Usa l'annotazione @Autowired per
     * indicare a spring che questo parametro dovrà essere creato come Bean e dovrà
     * essere inizializzato alla creazione della classe.
     */
    @Autowired
    private SchedulerRepository schedulerRepository;

    /**
     * L'attributo context contiene il contesto dell'applicazione. Viene usato per
     * trovare l'utente attualmente collegato.
     */
    @Autowired
    private ApplicationContext context;

    /**
     * Il metodo getAll risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scheduler</b>(localhost:8080/scheduler/).
     *
     * @return tutti i {@link Scheduler} che si trovano nel database.
     */
    @GetMapping()
    public Iterable<Scheduler> getAll() {
        return schedulerRepository.findAll();
    }

    /**
     * Il metodo get risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scheduler/&lt;id&gt;</b>
     * (localhost:8080/scheduler/11).
     *
     * @param id l'id dello {@link Scheduler}.
     * @return l'elemento richiesto della tabella Scheduler, messaggio d'errore
     * altrimenti.
     */
    @GetMapping(value = "/{id}")
    public Object get(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if (intId != null && schedulerRepository.existsById(intId))
            return schedulerRepository.findById(intId).get();
        else
            return new ResponseEntity<Response>(new Response("Invalid scheduler id"), HttpStatus.NOT_FOUND);
    }

    /**
     * Il metodo insert risponde alla richiesta di tipo PUT sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scheduler</b>(localhost:8080/scheduler/). Il
     * metodo inserisce un nuovo {@link Scheduler} nel database Scheduler.
     * <p>
     * I parametri vengono trattati in modo binario: Se bisogna eseguire lo
     * scheduler ogni 15 del mese, come parametro monthly si dovrà passare il numero
     * 32768 (2^15 -&gt; 000000000000000000100000000000000). Se bisogna eseguire lo
     * scheduler ogni 3 giorno della settimana, come parametro weekly bisogna
     * mettere 8 (2^3).
     * <p>
     * I parametri vengono accettati in modo binario per permette di aggiungere più
     * giorni di esecuzione, è fatto in modo per permettere uno sviluppo futuro.
     * <p>
     * Il parametro hour accetta l'ora di esecuzione in minuti partendo da 0 a 1439
     *
     * @param monthly   parametro da passare se lo {@link Scheduler} deve essere
     *                  eseguito mensilmente.
     * @param weekly    parametro da passare se lo {@link Scheduler} deve essere
     *                  eseguito settimanalmente.
     * @param repeated  parametro da passare se lo scheduler dovrà essere ripetuto,
     *                  valori: "true" o "false"
     * @param timeStart la data e ora dalla quale partirà l'esecuzione formato
     *                  timestamp -&gt; Long
     * @return lo scheduler inserito oppure messaggio d'errore
     */
    @PutMapping()
    public Object insert(@RequestParam String monthly, @RequestParam String weekly, @RequestParam String repeated,
                         @RequestParam String timeStart) {

        Integer monthlyInt = Validator.isInt(monthly);
        Integer weeklyInt = Validator.isInt(weekly);
        boolean repeatedBool = (repeated.equals("true"));
        Long date = Validator.isLong(timeStart);
        Scheduler scheduler = new Scheduler();

        if (date != null) {
            scheduler.setRepeated(repeatedBool);
            if (repeatedBool) {
                if (date > System.currentTimeMillis()) {
                    if (monthlyInt != null) {
                        Integer dayMonth = getFirstPosition(monthlyInt, 31);

                        scheduler.setMonthly(dayMonth);

                    } else if (weeklyInt != null) {
                        Integer dayWeek = getFirstPosition(weeklyInt, 7);
                        scheduler.setWeekly(dayWeek);

                    } else {
                        Logger.getGlobal().log(Level.INFO,"Schedule is daily");
                    }
                } else {
                    return new ResponseEntity<Response>(new Response("Schedule hour parameter invalid"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            scheduler.setTimeStart(date);
            schedulerRepository.save(scheduler);
            BeanDefinitionRegistry factory = (BeanDefinitionRegistry) context.getAutowireCapableBeanFactory();
            ((DefaultListableBeanFactory) factory).destroySingleton("scheduleChecker");
            ScheduleChecker checker = (ScheduleChecker) context.getBean("scheduleChecker");
            synchronized (checker) {
                checker.start();
            }
            return schedulerRepository.findById(scheduler.getSchedulerId());
        } else {
            return new ResponseEntity<Response>(new Response("timeStart parameter not set or invalid"),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Il metodo delete risponde alla richiesta di tipo DELETE sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scheduler/stopTimers</b>(localhost:8080/scheduler/stopTimers/).
     * Il metodo ferma tutti i timer della lista timers.
     *
     * @return il messaggio con status OK 200.
     */
    @DeleteMapping("/{id}")
    public Object delete(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if (intId != null) {
            if (schedulerRepository.existsById(intId)) {
                Scheduler scheduler = schedulerRepository.findById(intId).get();
                schedulerRepository.delete(scheduler);
                return scheduler;
            } else {
                return new ResponseEntity<Response>(new Response("Unable to find scheduler with id: " + id),
                    HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<Response>(new Response("Invalid parameter: " + id),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Il metodo getFirstPosition ritorna la posizione del primo bit a 1 in un
     * intero.
     *
     * @param number il numero da scansionare.
     * @param max    la grandezza del numero in bit (1-32).
     * @return la posizione del primo bit a 1 oppure 0 se non ci sono.
     */
    public Integer getFirstPosition(Integer number, int max) {
        List<Integer> positions = this.getPositions(number, max);
        if (positions.size() > 0) {
            return positions.get(0);
        }
        return 0;

    }

    /**
     * Il metodo getPositions ritorna una lista di posizioni dei bit a 1 nel numero
     * passato come parametro.
     *
     * @param number il numero da scansionare
     * @param max    la grandezza del numero in bit (1-32)
     * @return le posizioni di tutti i bit a 1 oppure 0 se non ci sono.
     */
    public List<Integer> getPositions(Integer number, int max) {

        max = max < 4 ? max = 31 : max;

        List<Integer> positions = new ArrayList<>();

        for (int i = max; i >= 0; i--) {
            if ((number & (1 << i)) != 0) {
                positions.add(i);
            }
        }
        return positions;
    }
}
