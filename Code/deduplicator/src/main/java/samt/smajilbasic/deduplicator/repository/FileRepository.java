package samt.smajilbasic.deduplicator.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import samt.smajilbasic.deduplicator.entity.File;

/**
 * FileRepository
 */
@Repository
public interface FileRepository extends CrudRepository<File,String>{

    
}