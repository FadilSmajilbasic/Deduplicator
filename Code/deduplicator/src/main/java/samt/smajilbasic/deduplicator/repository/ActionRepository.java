package samt.smajilbasic.deduplicator.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import samt.smajilbasic.deduplicator.entity.*;


/**
 * ActionRepository
 */
@Repository
public interface ActionRepository extends CrudRepository<Action,Integer> {

    
}