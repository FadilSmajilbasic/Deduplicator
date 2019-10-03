package samt.smajilbasic.deduplicator.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import samt.smajilbasic.deduplicator.entities.ActionType;;


/**
 * ActionTypeRepository
 */
@Repository
public interface ActionTypeRepository extends CrudRepository<ActionType,String> {

    
}