package rockland.elysiancrest.com.data_service.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddonDTO {
    private Long id;
    private String name;
    private BigDecimal priceInRupee;
    private BigDecimal priceInDollar;
    private String imageUrl;

}
