package rockland.elysiancrest.com.data_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "ComplaintLog")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EntityListeners(AuditLogListener.class)
public class ComplaintLog extends BaseEntity {
    @Column(length = 5000)
    private String message;
    private String role;
    private String username;  // Changed from User entity to String
    private String status;  // Added complaint status

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;
} 