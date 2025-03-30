package rockland.elysiancrest.com.data_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationItemDTO {

    private Long id; // ID of the quotation item (inherited from BaseEntity)

    private Long productId; // Product ID

    private String productName; // Name of the product

    private Integer quantity; // Quantity of the product

    private Double pricePerUnit; // Price per unit of the product

    private Double discountedPrice;

    private Double totalPriceAfterDiscount; // Calculated total price after discount

    private BigDecimal volume;
}

