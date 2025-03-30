package rockland.elysiancrest.com.data_service.entity.order;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "order_item")
@EntityListeners(AuditLogListener.class)
public class OrderItemMaster extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderMaster order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductMaster product;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "sku")
    private String sku;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_url_2")
    private String imageUrl2;

    @Column(name = "image_url_3")
    private String imageUrl3;

    @Column(name = "image_url_4")
    private String imageUrl4;

    @Column(name = "image_url_5")
    private String imageUrl5;

    @Column(name = "unit_price_in_rupee", precision = 10, scale = 2)
    private BigDecimal unitPriceInRupee;

    @Column(name = "unit_price_in_dollar", precision = 10, scale = 2)
    private BigDecimal unitPriceInDollar;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "total_price_in_rupee", precision = 10, scale = 2)
    private BigDecimal totalPriceInRupee;

    @Column(name = "total_price_in_dollar", precision = 10, scale = 2)
    private BigDecimal totalPriceInDollar;

}

