package rockland.elysiancrest.com.data_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartAddonDTO {

    private Long id; // From BaseEntity

    private Long cartId; // ID of the associated CartMaster

    private String addonName; // Addon name for better context in the response

    private int quantity;

    private BigDecimal totalPriceInRupee;

    private BigDecimal totalPriceInDollar;

}
