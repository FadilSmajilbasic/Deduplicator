package samt.smajilbasic.deduplicator.repository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import samt.smajilbasic.deduplicator.entity.GlobalPath;


/**
 * GlobalPathRepository
 */
@Repository
public interface GlobalPathRepository extends CrudRepository<GlobalPath,String> {

    @Query(value="Select * from global_path where ignore_file=true",nativeQuery = true)
    public List<GlobalPath> findIgnored();
}