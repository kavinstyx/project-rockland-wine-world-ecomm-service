package rockland.elysiancrest.com.data_service.listner;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import rockland.elysiancrest.com.data_service.config.SpringContext;
import rockland.elysiancrest.com.data_service.entity.AuditLog;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.service.AuditLogService;
import rockland.elysiancrest.com.data_service.util.AuthenticatedUserUtil;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuditLogListener {

    private final ObjectMapper objectMapper;
    private final AuthenticatedUserUtil authenticatedUserUtil;

    @PostPersist
    public void onPostPersist(Object entity) {
        saveAuditLog(entity, "INSERT", null);
    }

    @PostUpdate
    public void onPostUpdate(Object entity) {
        Object oldEntity = getOriginalEntity(entity);
        saveAuditLog(entity, "UPDATE", oldEntity);
    }

    @PostRemove
    public void onPostRemove(Object entity) {
        saveAuditLog(entity, "DELETE", null);
    }

    private void saveAuditLog(Object entity, String operation, Object oldEntity) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setEntityName(entity.getClass().getSimpleName());
            auditLog.setOperation(operation);
            auditLog.setTimestamp(LocalDateTime.now());

            // Get entity ID
            Field idField = ReflectionUtils.findField(entity.getClass(), "id");
            if (idField != null) {
                ReflectionUtils.makeAccessible(idField);
                auditLog.setEntityId((Long) idField.get(entity));
            }

            // Serialize changes
            String changes = objectMapper.writeValueAsString(entity);
            auditLog.setChanges(changes);

            // Calculate and serialize field differences for updates
            if ("UPDATE".equals(operation) && oldEntity != null) {
                Map<String, Object> fieldChanges = calculateFieldChanges(oldEntity, entity);
                String changeSummary = objectMapper.writeValueAsString(fieldChanges);
                auditLog.setChangeSummary(changeSummary);
            }

            User currentUser = authenticatedUserUtil.getCurrentUser();
            if (currentUser != null) {
                auditLog.setChangedBy(currentUser.getUsername());
                auditLog.setChangedUserId(currentUser.getId());
                auditLog.setChangedUserRole(currentUser.getUserRole());
            }else {
                auditLog.setChangedBy("Unknown");
                auditLog.setChangedUserId(-1L);
                auditLog.setChangedUserRole("Unknown");
            }

            // Save to repository
            saveAuditLog(auditLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveAuditLog(AuditLog auditLog) {
        AuditLogRepositoryHolder.getRepository().save(auditLog);
    }

    private Map<String, Object> calculateFieldChanges(Object oldEntityJson, Object newEntity) throws IllegalAccessException {
        Map<String, Object> changes = new HashMap<>();
        try {
            // Deserialize oldEntityJson into an object of the same type as newEntity
            Object oldEntity = objectMapper.readValue((String) oldEntityJson, newEntity.getClass());

            for (Field field : oldEntity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object oldValue = field.get(oldEntity);
                Object newValue = field.get(newEntity);

                if ((oldValue != null && !oldValue.equals(newValue)) || (oldValue == null && newValue != null)) {
                    changes.put(field.getName(), Map.of("old", oldValue, "new", newValue));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return changes;
    }

    public Object getOriginalEntity(Object entity) {
        try {
            // Find the field annotated with @Id
            Field idField = ReflectionUtils.findField(entity.getClass(), "id");
            String entityName = entity.getClass().getSimpleName();
            if (idField != null) {
                ReflectionUtils.makeAccessible(idField);
                Object id = idField.get(entity);

                if (id != null) {
                    AuditLogService auditLogService = SpringContext.getBean(AuditLogService.class); // Get the service bean
                    AuditLog latestLog = auditLogService.fetchLatestAuditLog(entityName, (Long) id);
                    if (latestLog != null) {
                        return latestLog.getChanges();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null if the entity couldn't be fetched
    }
}
