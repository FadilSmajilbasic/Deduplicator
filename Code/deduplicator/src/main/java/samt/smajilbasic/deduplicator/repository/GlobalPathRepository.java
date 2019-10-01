package samt.smajilbasic.deduplicator.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import samt.smajilbasic.deduplicator.entities.GlobalPath;


/**
 * GlobalPathRepository
 */
@Repository
public interface GlobalPathRepository extends CrudRepository<GlobalPath,String> {

    
}