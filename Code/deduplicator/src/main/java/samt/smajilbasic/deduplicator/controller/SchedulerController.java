package samt.smajilbasic.deduplicator.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;

/**
 * ScanController
 */

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

@RestController
@RequestMapping(path = "/scheduler")
public class SchedulerController {

    @Autowired
    SchedulerRepository schedulerRepository;

    @Autowired
    ScheduleChecker checker;

    @Autowired
    ApplicationContext context;

    @GetMapping()
    public @ResponseBody Iterable<Scheduler> getSchedulers() {
        return schedulerRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public @ResponseBody Object getSchduler(@PathVariable String id) {
        Integer intId = Validator.isInt(id);
        if (intId != null && schedulerRepository.existsById(intId))
            return schedulerRepository.findById(intId).get();
        else
            return new Message(HttpStatus.NOT_FOUND, "Invalid scheduler id");
    }

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

    public Integer getFirstPosition(Integer number, int max) {
        List<Integer> positions = this.getPositions(number, max);
        if(positions.size() > 0){
            return positions.get(0);
        }
        return 0;
        
    }

    /**
     * For future implementations (multiple days)
     * 
     * @param number
     * @param max
     * @return
     */
    public List<Integer> getPositions(Integer number, int max) {

        max = max < 4 ? max = 31 : max;

        List<Integer> positions = new ArrayList<>();

        for (int i = max; i >= 0; i--) {
            if ((number & (1 << i)) != 0) {
                positions.add(i);
            }
            ;
        }
        return positions;
    }


    @DeleteMapping("/{id}")
    public @ResponseBody Object deleteScheduler(@PathVariable String id){
        Integer intId = Validator.isInt(id);
        if(intId != null){
            if(schedulerRepository.existsById(intId)){
                Scheduler scheduler = schedulerRepository.findById(intId).get();
                schedulerRepository.delete(scheduler);
                return scheduler;
            }else{
                return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to find scheduler with id: " + id);
            }
        }else{
            return new Message(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid parameter: " + id );
        }
        

    }
}
