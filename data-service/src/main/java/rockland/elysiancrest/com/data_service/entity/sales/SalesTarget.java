package rockland.elysiancrest.com.data_service.entity.sales;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Index;
import lombok.Data;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;

@Entity
@Data
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"year", "month"})
    },
    indexes = {
        @Index(name = "idx_year_month", columnList = "year,month")
    }
)
public class SalesTarget extends BaseEntity  {

        @Column(name = "year", nullable = false)
        private Integer year;

        @Column(name = "month", nullable = false)
        private Integer month;

        @Column(name = "total_sale")
        private Double totalSale;

        @Column(name = "delivery_cost") 
        private Double deliveryCost;

        @Column(name = "net_sales")
        private Double netSales;

        @Column(name = "number_of_orders")
        private Integer numberOfOrders;

        @Column(name = "average_order_value")
        private Double averageOrderValue;

        @Column(name = "average_skus_per_order")
        private Double averageSkusPerOrder;
}
