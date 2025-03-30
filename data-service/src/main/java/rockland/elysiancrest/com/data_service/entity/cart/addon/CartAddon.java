package rockland.elysiancrest.com.data_service.entity.cart.addon;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.entity.cart.CartMaster;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_addon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartAddon extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private CartMaster cart;

    @ManyToOne
    @JoinColumn(name = "addon_id", nullable = false)
    private Addon addon;

    @Column
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

