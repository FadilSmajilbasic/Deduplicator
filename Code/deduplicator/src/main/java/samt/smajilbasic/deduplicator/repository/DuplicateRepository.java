package samt.smajilbasic.deduplicator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.entity.Duplicate;

/**
 * DuplicateRepository
 */
@Repository
public interface DuplicateRepository extends CrudRepository<Duplicate, String> {
    @Query("SELECT " +
           "    new samt.smajilbasic.deduplicator.entity.Duplicate(f.path, f.lastModified,f.size,f.hash) " +
           "FROM " +
           "    File f " +
           " WHERE report = ?1 " + 
           "GROUP BY " +
           "f.hash, f.size " + 
           "order by path")
    List<Duplicate> findDuplicates(Report report);
}