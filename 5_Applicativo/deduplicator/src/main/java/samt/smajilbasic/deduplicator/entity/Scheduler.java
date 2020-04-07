package samt.smajilbasic.deduplicator.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * La classe Scheduler descrive evento pianificato per l'esecuzione. Usa
 * l'annotazione @{@link Entity}
 */
@Entity
public class Scheduler {

    /**
     * L'attributo schedulerId contiene l'id del elemento. Utilizza
     * l'annotazione @Id per indicare che è una chiave primaria della tabella.
     * Utilizza l'annotazione @GeneratedValue(strategy= GenerationType.AUTO) per
     * inidcare che questo è un valore generato eincrementato automaticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer schedulerId;

    /**
     * L'attributo monthly descrive quale giorno del mese dovrà essere ripetuta
     * l'azione. È un valore binario: Se bisogna eseguire lo scheduler ogni 15 del
     * mese, come parametro monthly si dovrà passare il numero 32768 (2^15 -&gt;
     * 000000000000000000100000000000000).
     */
    private Integer monthly;
    /**
     * L'attributo monthly descrive quale giorno della settimana dovrà essere
     * ripetuta l'azione. È un valore binario: Se bisogna eseguire lo scheduler ogni
     * 3 giorno della settimana, come parametro weekly bisogna mettere 8 (2^3).
     */
    private Integer weekly;

    /**
     * L'attributo repeated definisce se lo scheduler è da ripetere oppure no
     */
    private Boolean repeated;
    /**
     * L'attriuto timeStart definisce la data e ora d'inizio della esecuzione in
     * formato timestamp.
     */
    private Long timeStart;
    /**
     * L'attributo executionCounter definisce il numero di volte che lo scheduler è
     * stato eseguito.
     */
    private Integer executionCounter = 0;

    /**
     * L'attributo scheduled descrive se lo scheduler è già stato impostato per l'esecuzione
     */
    private boolean scheduled = false;

    /**
     * L'attributo action contiene tutte le azioni che lo scheduler dovrà eseguire
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "scheduler")
    private List<Action> action;

    /**
     * Metodo getter per l'attributo schedulerId.
     *
     * @return l'id dello scheduler.
     */
    public Integer getSchedulerId() {
        return schedulerId;
    }

    /**
     * Il metodo getter per l'attriburo monthly.
     *
     * @return il valore del attributo monthly.
     */
    public Integer getMonthly() {
        return monthly;
    }

    /**
     * Il metodo setter per l'attributo monthly
     *
     * @param monthly il giorno del mese al quale eseguire lo scheduler. Il valore
     *                impostato dovrebbe essere una potenza di 2 [2^(1-31)].
     */
    public void setMonthly(Integer monthly) {
        this.monthly = monthly;
    }

    /**
     * Il metodo getter per l'attributo weekly
     *
     * @return il valore dell'attributo weekly.
     */
    public Integer getWeekly() {
        return weekly;
    }

    /**
     * Il metodo setter per l'attributo weekly.
     *
     * @param weekly il giorno della settimana al quale eseguire lo scheduler. Il
     *               valore impostato dovrebbe essere una potenza di 2 [2^(1-7)].
     */
    public void setWeekly(Integer weekly) {
        this.weekly = weekly;
    }

    /**
     * Il metodo getter per l'attributo repeated.
     *
     * @return true se lo scheduler è impostato per essere eseguito più volte, false
     * altrimenti.
     */
    public boolean isRepeated() {
        return repeated;
    }

    /**
     * Il metodo setter per l'attributo repeated.
     *
     * @param repeated true se si vuole che lo scheduler si ripete, false se
     *                 l'esecuzione deve avvenire solo una volta.
     */
    public void setRepeated(boolean repeated) {
        this.repeated = repeated;
    }

    /**
     * Il metodo getter per l'attributo timeStart.
     *
     * @return data e ora della prima esecuzione in formato timestamp.
     */
    public Long getTimeStart() {
        return timeStart;
    }

    /**
     * Il metodo setter per l'attributi timeStart.
     *
     * @param timeStart la data e ora della prima esecutione in formato timestamp
     */
    public void setTimeStart(Long timeStart) {
        this.timeStart = timeStart;
    }

    /**
     * Il Metodo getter per l'attributo executionCounter.
     *
     * @return il numero di volte che lo scheduler è stato eseguito.
     */
    public Integer getExecutionCounter() {
        return executionCounter;
    }

    /**
     * Metodo chiamato per incrementare il valore dell'attributo executionCounter.
     * Viene chiamato quando un
     * {@link samt.smajilbasic.deduplicator.timer.ScheduleChecker} finisce con
     * l'esecuzione.
     */
    public void executed() {
        executionCounter++;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean value) {
        this.scheduled = value;
    }
}