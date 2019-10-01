package samt.smajilbasic.deduplicator.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import samt.smajilbasic.deduplicator.entities.Scheduler;


/**
 * SchedulerRepository
 */
@Repository
public interface SchedulerRepository extends CrudRepository<Scheduler,Integer> {

    
}