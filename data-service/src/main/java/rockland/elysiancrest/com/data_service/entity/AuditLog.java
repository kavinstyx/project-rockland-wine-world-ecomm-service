package rockland.elysiancrest.com.data_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
        indexes = {
                @Index(name = "idx_changed_user_id", columnList = "changedUserId"),
                @Index(name = "idx_timestamp", columnList = "timestamp"),
                @Index(name = "idx_entity_name", columnList = "entityName"),
                @Index(name = "idx_entity_id", columnList = "entityId"),
                @Index(name = "idx_combined", columnList = "changedUserId, timestamp, entityName, entityId")
        }
)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entityName;

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false)
    private String operation; // INSERT, UPDATE, DELETE

    @Column(nullable = false)
    private String changedBy;

    @Column(nullable = false)
    private Long changedUserId;

    @Column(nullable = false)
    private String changedUserRole;


    @Column(columnDefinition = "TEXT")
    private String changes; // Full JSON representation of the entity

    @Column(columnDefinition = "TEXT")
    private String changeSummary; // Summary of changed fields

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
