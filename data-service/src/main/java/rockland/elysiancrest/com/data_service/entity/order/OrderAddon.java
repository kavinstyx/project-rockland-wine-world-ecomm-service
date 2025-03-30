package rockland.elysiancrest.com.data_service.entity.order;

import jakarta.persistence.*;
import lombok.Data;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.entity.cart.addon.Addon;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_addon")
@EntityListeners(AuditLogListener.class)
public class OrderAddon extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderMaster order;

    @ManyToOne
    @JoinColumn(name = "addon_id", nullable = false)
    private Addon addon;

    @Column(name = "addon_name", nullable = false)
    private String addonName;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "total_price_in_rupee", precision = 10, scale = 2)
    private BigDecimal totalPriceInRupee;

    @Column(name = "total_price_in_dollar", precision = 10, scale = 2)
    private BigDecimal totalPriceInDollar;

    public BigDecimal calculateTotalPriceInRupee() {
        return addon.getPriceInRupee().multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal calculateTotalPriceInDollar() {
        return addon.getPriceInDollar().multiply(BigDecimal.valueOf(quantity));
    }

}
