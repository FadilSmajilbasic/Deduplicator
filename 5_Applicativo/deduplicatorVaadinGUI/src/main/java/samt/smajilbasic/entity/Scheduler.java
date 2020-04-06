package samt.smajilbasic.entity;


import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * La classe Scheduler descrive evento pianificato per l'esecuzione.
 */
public class Scheduler {

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


    private Boolean repeated;

    private Long timeStart;

    private Integer executionCounter = 0;

    private boolean scheduled;


    public Integer getSchedulerId() {
        return schedulerId;
    }


    public Integer getMonthly() {
        return monthly;
    }

    public String getMonthlyFormatted() {
        if(getMonthly() != null)
            return "Repeated on the " + getPositions(getMonthly(), 31).get(0);
        else
            return "None";
    }

    public void setMonthly(Integer monthly) {
        this.monthly = monthly;
    }


    public Integer getWeekly() {
        return weekly;
    }

    public String getWeeklyFormatted() {
        String[] weekdayNames = DateFormatSymbols.getInstance().getWeekdays();
        weekdayNames = Arrays.copyOfRange(weekdayNames, 1, weekdayNames.length);
        if(getWeekly() != null) {
            return "Repeated on the " + weekdayNames[getPositions(getWeekly(), 7).get(0)];
        }else{
            return "None";
        }
    }


    public void setWeekly(Integer weekly) {
        this.weekly = weekly;
    }


    public boolean isRepeated() {
        return repeated;
    }


    public void setRepeated(boolean repeated) {
        this.repeated = repeated;
    }

    public Long getTimeStart() {
        return timeStart;
    }

    public String getTimeStartFormatted() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTimeStart());
        return dateFormat.format(cal.getTime());
    }

    public void setTimeStart(Long timeStart) {
        this.timeStart = timeStart;
    }

    public Integer getExecutionCounter() {
        return executionCounter;
    }

    public void executed() {
        executionCounter++;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean value) {
        this.scheduled = value;
    }

    private List<Integer> getPositions(Integer number, int max) {

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