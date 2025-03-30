package rockland.elysiancrest.com.data_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellingPriceMasterDTO {

    private Long id;
    private String sapMaterialCode;
    private String customerGroupType;
    private String unitOfMeasure;
    private BigDecimal priceInclTax;
    private String currency;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
}
