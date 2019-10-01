package samt.smajilbasic.deduplicator.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import samt.smajilbasic.deduplicator.entities.Report;;


/**
 * ReportRepository
 */
@Repository
public interface ReportRepository extends CrudRepository<Report,String> {

    
}