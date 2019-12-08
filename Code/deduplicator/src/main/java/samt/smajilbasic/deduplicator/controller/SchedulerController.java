package samt.smajilbasic.deduplicator.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import samt.smajilbasic.deduplicator.Validator;
import samt.smajilbasic.deduplicator.timer.ScheduleChecker;
import samt.smajilbasic.deduplicator.entity.Scheduler;
import samt.smajilbasic.deduplicator.exception.Message;
import samt.smajilbasic.deduplicator.repository.SchedulerRepository;;

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
public class SchedulerController implements TimerListener {

    /**
     * L'attributo schedulerRepository serve al controller per interfacciarsi con la
     * tabella {@link Scheduler} del database. Usa l'annotazione @Autowired per
     * indicare a spring che questo parametro dovrà essere creato come Bean e dovrà
     * essere inizializzato alla creazione della classe.
     */
    @Autowired
    private SchedulerRepository schedulerRepository;

    /**
     * L'attributo checker serve a per eseguire il controllo di azioni o scan da
     * eseguire.
     */
    @Autowired
    private ScheduleChecker checker;

    /**
     * L'attributo timers è una lista di {@link Timer} che contengono operazioni da
     * eseguire in un determinante istante.
     */
    private List<Timer> timers = new ArrayList<>();

    /**
     * Il metodo getPaths risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scheduler</b>(localhost:8080/scheduler/).
     * 
     * @return tutti i {@link Scheduler} che si trovano nel database.
     */
    @GetMapping()
    public @ResponseBody Iterable<Scheduler> getSchedulers() {
        return schedulerRepository.findAll();
    }

    /**
     * Il metodo getScheduler risponde alla richiesta di tipo GET sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scheduler/&lt;id&gt;</b>
     * (localhost:8080/scheduler/11).
     * 
     * @param id l'id dello {@link Scheduler}.
     * @return l'elemento richiesto della tabella Scheduler, messaggio d'errore
     *         altrimenti.
     */
    @GetMapping(value = "/{id}")
    public @ResponseBody Object getScheduler(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if (intId != null && schedulerRepository.existsById(intId))
            return schedulerRepository.findById(intId).get();
        else
            return new Message(HttpStatus.NOT_FOUND, "Invalid scheduler id");
    }

    /**
     * Il metodo insert risponde alla richiesta di tipo PUT sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scheduler</b>(localhost:8080/scheduler/). Il
     * metodo inserisce un nuovo {@link Scheduler} nel database Scheduler.
     * 
     * I parametri vengono trattati in modo binario: Se bisogna eseguire lo
     * scheduler ogni 15 del mese, come parametro monthly si dovrà passare il numero
     * 32768 (2^15 -> 000000000000000000100000000000000).
     * Se bisogna eseguire lo scheduler ogni 3 giorno della settimana, come parametro weekly bisogna
     * mettere 8 (2^3).
     * 
     * I parametri vengono accettati in modo binario per permette di aggiungere più
     * giorni di esecuzione, è fatto in modo per permettere uno sviluppo futuro.
     * 
     * Il parametro hour accetta l'ora di esecuzione in minuti partendo da 0 a 1439
     * 
     * @param monthly   parametro da passare se lo {@link Scheduler} deve essere
     *                  eseguito mensilmente.
     * @param weekly    parametro da passare se lo {@link Scheduler} deve essere
     *                  eseguito settimanalmente.
     * @param hour      parametro da passare per indiacre l'ora di esecuzione.
     * @param repeated  parametro da passare se lo scheduler dovrà essere ripetuto,
     *                  valori: "true" o "false"
     * @param dateStart la data dalla quale partirà l'esecuzione formato timestamp
     *                  -> long
     * @return lo scheduler inserito oppure messaggio d'errore
     */
    @PutMapping()
    public @ResponseBody Object addScheduler(@RequestParam String monthly, @RequestParam String weekly,
            @RequestParam String hour, @RequestParam String repeated, @RequestParam String dateStart) {

        Integer monthlyInt = Validator.isInt(monthly);
        Integer weeklyInt = Validator.isInt(weekly);
        Integer minutes = Validator.isInt(hour);
        boolean repeatedBool = (repeated.equals("true")) ? true : false;
        Date date;
        Scheduler scheduler = new Scheduler();
        try {
            date = new Date(Long.parseLong(dateStart));
        } catch (NumberFormatException e) {
            date = new Date(System.currentTimeMillis());
        }
        if (repeatedBool) {
            scheduler.setRepeated(repeatedBool);
            if (minutes != null && minutes >= 0 && minutes <= 1440) { // 60 min * 24 h = 1440
                if (monthlyInt != null) {
                    Integer dayMonth = getFirstPosition(monthlyInt, 31);

                    scheduler.setMonthly(dayMonth);

                } else if (weeklyInt != null) {
                    Integer dayWeek = getFirstPosition(weeklyInt, 7);
                    scheduler.setWeekly(dayWeek);

                } else {
                    return new Message(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Schedule monthly and weekly parameters invalid");
                }
                scheduler.setMinutes(minutes);
            } else {
                return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Schedule hour parameter invalid");
            }
        } else {

            scheduler.setDateStart(date);
            scheduler.setMinutes(minutes);
            scheduler.setRepeated(repeatedBool);
        }
        schedulerRepository.save(scheduler);
        checker.check();
        return schedulerRepository.findById(scheduler.getSchedulerId());
    }

    /**
     * Il metodo stopTimers risponde alla richiesta di tipo PUT sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scheduler/stopTimers</b>(localhost:8080/scheduler/stopTimers/). 
     * Il metodo ferma tutti i timer della lista timers.
     * 
     * @return il messaggio con status OK 200.
     */
    @PostMapping("/stopTimers")
    public Message stopTimers() {

        timers.forEach(timer -> {
            timer.cancel();
            timers.remove(timer);
        });

        return new Message(HttpStatus.OK, "Timers stopped");
    }

    /**
     * Il metodo deleteScheduler risponde alla richiesta di tipo DELETE sull'indirizzo
     * <b>&lt;indirizzo-server&gt;/scheduler/stopTimers</b>(localhost:8080/scheduler/stopTimers/). 
     * Il metodo ferma tutti i timer della lista timers.
     * 
     * @return il messaggio con status OK 200.
     */
    @DeleteMapping("/{id}")
    public @ResponseBody Object deleteScheduler(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if (intId != null) {
            if (schedulerRepository.existsById(intId)) {
                Scheduler scheduler = schedulerRepository.findById(intId).get();
                schedulerRepository.delete(scheduler);
                return scheduler;
            } else {
                return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to find scheduler with id: " + id);
            }
        } else {
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid parameter: " + id);
        }
    }


    /**
     * Il metodo getFirstPosition ritorna la posizione del primo bit a 1 in un intero.
     * 
     * @param number il numero da scansionare.
     * @param max la grandezza del numero in bit (1-32).
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
     * Il metodo getFirstPosition ritorna una lista di posizioni dei bit a 1 nel numero passato come parametro.
     * 
     * @param number il numero da scansionare
     * @param max la grandezza del numero in bit (1-32)
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

    /**
     * TODO: check usage
     * Metodo che aggiunge un timer alla lista timers.
     */
    @Override
    public void timerAdded(Timer timer) {
        timers.add(timer);
    }
}
