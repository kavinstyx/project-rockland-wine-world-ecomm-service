package rockland.elysiancrest.com.data_service.repo;


import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.AuditLog;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> , JpaSpecificationExecutor<AuditLog> {
    List<AuditLog> findByEntityNameAndEntityIdOrderByTimestampDesc(String entityName, Long entityId, Limit limit);

    @Query("SELECT DISTINCT a.entityName FROM AuditLog a")
    List<String> findDistinctEntityName();
}
