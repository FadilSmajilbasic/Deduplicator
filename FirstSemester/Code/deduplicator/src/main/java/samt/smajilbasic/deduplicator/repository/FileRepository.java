package samt.smajilbasic.deduplicator.repository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import samt.smajilbasic.deduplicator.entity.Report;
import samt.smajilbasic.deduplicator.entity.File;

/**
 * FileRepository
 */
@Repository
public interface FileRepository extends CrudRepository<File,String>{

    @Query( value =  "SELECT * FROM file f where f.report=?1 and f.hash = ?2",nativeQuery = true)
    List<File> findFilesFromHashAndReport(Report report,String hash);



    @Query(value = "SELECT if(EXISTS(SELECT * from file WHERE hash=?1)>0,'true','false')",nativeQuery = true)
    boolean existsByHash(String hash);
}