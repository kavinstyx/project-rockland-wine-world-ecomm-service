package rockland.elysiancrest.com.data_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryChargeResponse {
    private BigDecimal totalDeliveryChargeInRupee;
    private BigDecimal totalDeliveryChargeInDollar;

//    private BigDecimal totalComboPackDeliveryChargeInRupee;
//    private BigDecimal totalComboPackDeliveryChargeInDollar;
}
