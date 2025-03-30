package rockland.elysiancrest.com.data_service.entity.cart;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductMaster product;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonBackReference  // Back-reference to CartMaster
    private CartMaster cart;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "total_price_in_rupee", precision = 10, scale = 2)
    private BigDecimal totalPriceInRupee;

    @Column(name = "total_price_in_dollar", precision = 10, scale = 2)
    private BigDecimal totalPriceInDollar;

    public BigDecimal getTotalPriceInRupee() {
        return product.getRegularPriceInRupee().multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getTotalPriceInDollar() {
        return product.getRegularPriceInDollar().multiply(BigDecimal.valueOf(quantity));
    }
}