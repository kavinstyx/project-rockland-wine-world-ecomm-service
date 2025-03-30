package rockland.elysiancrest.com.data_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Complaint")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EntityListeners(AuditLogListener.class)
public class Complaint extends BaseEntity{
    private String inquiryType;
    private String subject;
    @Column(length = 5000)
    private String message;
    private String attachmentPath;
    private String temporaryLink;
    private String status;
    @Column(length = 5000)
    private String adminComment;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderMaster order;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL)
    private List<ComplaintLog> complaintLogs;
}
