package rockland.elysiancrest.com.data_service.entity.cart.addon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "addon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Addon extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "price_in_rupee", precision = 10, scale = 2)
    private BigDecimal priceInRupee;

    @Column(name = "price_in_dollar", precision = 10, scale = 2)
    private BigDecimal priceInDollar;

    @Column(name = "image_url")
    private String imageUrl;
}

