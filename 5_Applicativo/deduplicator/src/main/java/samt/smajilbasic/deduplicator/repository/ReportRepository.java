package samt.smajilbasic.deduplicator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import samt.smajilbasic.deduplicator.entity.Report;

/**
 * ReportRepository
 */
@Repository
public interface ReportRepository extends CrudRepository<Report, Integer> {

    @Query(value = "SELECT * FROM report ORDER BY report_id DESC limit 1;",nativeQuery = true)
    public Report getLastReport();

    @Query(value="SELECT report_id, start FROM report;",nativeQuery = true)
    public List<String> findAllReduced();

}