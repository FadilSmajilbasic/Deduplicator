package samt.smajilbasic.deduplicator.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import samt.smajilbasic.deduplicator.entity.AuthenticationDetails;


/**
 * AuthenticationDetailsRepository
 */
@Repository
public interface AuthenticationDetailsRepository extends CrudRepository<AuthenticationDetails,String> {

    
}