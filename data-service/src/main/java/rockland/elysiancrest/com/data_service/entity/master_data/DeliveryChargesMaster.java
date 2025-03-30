package rockland.elysiancrest.com.data_service.entity.master_data;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.listner.AuditLogListener;

import java.math.BigDecimal;

@Entity
@Table(name = "delivery_charges_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditLogListener.class)
public class DeliveryChargesMaster extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "city_id")
    private CityMaster cityMaster;

    private String deliveryType;

    private BigDecimal basePriceInRupee;

    private BigDecimal twoBottlesPriceInRupee;

    private BigDecimal threeBottlesPriceInRupee;

    private BigDecimal fourBottlesPriceInRupee;

    private BigDecimal fiveBottlesPriceInRupee;

    private BigDecimal sixBottlesPriceInRupee;

    private BigDecimal sevenBottlesPriceInRupee;

    private BigDecimal eightBottlesPriceInRupee;

    private BigDecimal basePriceInDollar;

    private BigDecimal twoBottlesPriceInDollar;

    private BigDecimal threeBottlesPriceInDollar;

    private BigDecimal fourBottlesPriceInDollar;

    private BigDecimal fiveBottlesPriceInDollar;

    private BigDecimal sixBottlesPriceInDollar;

    private BigDecimal sevenBottlesPriceInDollar;

    private BigDecimal eightBottlesPriceInDollar;


    private BigDecimal comboPackInRupee;
    private BigDecimal comboPackInDollar;
    

}
