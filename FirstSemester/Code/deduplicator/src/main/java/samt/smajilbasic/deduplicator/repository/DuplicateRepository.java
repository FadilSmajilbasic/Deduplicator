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
    @Query( value = "SELECT f.size,f.hash,count(*) as count "+
                    "FROM file f "+
                    "WHERE report=?1 "+
                    "GROUP BY f.hash,f.size "+
                    "HAVING count > 1 "+
                    "ORDER BY hash,size DESC",
            nativeQuery = true)
    List<Duplicate> findDuplicatesFromReport(Report report);
}