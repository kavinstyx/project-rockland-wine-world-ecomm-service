package rockland.elysiancrest.com.data_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderAddonDTO {

    private String addonName;
    private int quantity;
    private BigDecimal totalPriceInRupee;
    private BigDecimal totalPriceInDollar;
}
