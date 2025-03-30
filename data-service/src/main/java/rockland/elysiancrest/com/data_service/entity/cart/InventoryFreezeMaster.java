package rockland.elysiancrest.com.data_service.entity.cart;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_freeze")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryFreezeMaster extends BaseEntity {

    private Long productId;

    private Long userId;  // Track which user reserved the item
    private int quantity;
    private LocalDateTime reservedAt;  // Timestamp of reservation

    @Column(name = "released", nullable = false)
    private boolean released;

    private Long orderId;
    private String sessionId;
}
