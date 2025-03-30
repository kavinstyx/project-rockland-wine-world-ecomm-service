package rockland.elysiancrest.com.data_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.dto.Response;
import rockland.elysiancrest.com.data_service.dto.Status;
import rockland.elysiancrest.com.data_service.entity.AuditLog;
import rockland.elysiancrest.com.data_service.repo.AuditLogRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLog fetchLatestAuditLog(String entityName, Long entityId) {
        List<AuditLog> oldEntities = auditLogRepository.findByEntityNameAndEntityIdOrderByTimestampDesc(entityName, entityId, Limit.of(1));
        return oldEntities.isEmpty() ? null : oldEntities.get(0);
    }

    @Transactional(readOnly = true)
    public Response<Page<AuditLog>> getAuditLogs(LocalDateTime startDate, 
                                                LocalDateTime endDate, 
                                                String entityName, 
                                                Long entityId, 
                                                Long userId,
                                                String operation,
                                                Integer page,
                                                Integer size) {
        
        Specification<AuditLog> spec = Specification.where(null);
        
        if (startDate != null && endDate != null) {
            spec = spec.and((root, query, cb) -> 
                cb.between(root.get("timestamp"), startDate, endDate));
        }
        
        if (entityName != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("entityName"), entityName));
        }
        
        if (entityId != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("entityId"), entityId));
        }
        
        if (userId != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("changedUserId"), userId));
        }

        if (operation != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("operation"), operation));
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<AuditLog> auditLogs = auditLogRepository.findAll(spec, pageable);

        return Response.<Page<AuditLog>>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .data(auditLogs)
                .message("Audit logs retrieved successfully")
                .build();
    }

    @Transactional(readOnly = true)
    public Response<List<String>> getAllEntityNames() {
        List<String> entityNames = auditLogRepository.findDistinctEntityName();
        
        return Response.<List<String>>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .data(entityNames)
                .message("Entity names retrieved successfully")
                .build();
    }

}