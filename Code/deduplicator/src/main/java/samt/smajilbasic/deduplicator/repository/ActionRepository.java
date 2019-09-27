package samt.smajilbasic.deduplicator.repository;

import org.springframework.data.repository.CrudRepository;

import samt.smajilbasic.deduplicator.entities.Action;


/**
 * ActionRepository
 */
public interface ActionRepository extends CrudRepository<Action,Integer> {

    
}