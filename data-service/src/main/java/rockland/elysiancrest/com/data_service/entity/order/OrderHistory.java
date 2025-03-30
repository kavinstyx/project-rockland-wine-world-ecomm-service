package rockland.elysiancrest.com.data_service.entity.order;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "order_history", indexes = @Index(name = "idx_order_id", columnList = "orderId"))
public class OrderHistory  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long userId;
    private String username;
    private String userRole;
    @Column(columnDefinition = "TEXT")
    private String action;
    private String status;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String message;
    private LocalDateTime createdOn;
}
