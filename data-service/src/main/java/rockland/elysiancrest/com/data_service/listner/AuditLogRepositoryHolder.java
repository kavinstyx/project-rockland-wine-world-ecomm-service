package rockland.elysiancrest.com.data_service.listner;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rockland.elysiancrest.com.data_service.repo.AuditLogRepository;

@Component
public class AuditLogRepositoryHolder {

    private static AuditLogRepository repository;

    @Autowired
    public AuditLogRepositoryHolder(AuditLogRepository auditLogRepository) {
        repository = auditLogRepository;
    }

    public static AuditLogRepository getRepository() {
        return repository;
    }
}
