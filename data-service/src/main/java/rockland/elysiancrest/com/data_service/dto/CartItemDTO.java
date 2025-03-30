package rockland.elysiancrest.com.data_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long itemId;
    private String productName;
    private int quantity;
    private double priceInRupee;
    private double priceInDollar;
    private ProductMasterDTO product;
}
