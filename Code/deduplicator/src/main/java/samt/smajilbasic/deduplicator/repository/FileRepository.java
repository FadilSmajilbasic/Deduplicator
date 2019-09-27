package samt.smajilbasic.deduplicator.repository;

import org.springframework.data.repository.CrudRepository;

import samt.smajilbasic.deduplicator.entities.File;

/**
 * FileRepository
 */
public interface FileRepository extends CrudRepository<File,String>{

    
}