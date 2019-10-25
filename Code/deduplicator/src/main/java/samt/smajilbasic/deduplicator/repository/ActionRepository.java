package samt.smajilbasic.deduplicator.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import samt.smajilbasic.deduplicator.entity.*;


/**
 * ActionRepository
 */
@Repository
public interface ActionRepository extends CrudRepository<Action,Integer> {

     @Query( value = "SELECT * "+
                    "FROM actions  "+
                    "WHERE scheduler=?1",
            nativeQuery = true)
    List<Action> findActionsFromScheduler(Scheduler schedule);
}