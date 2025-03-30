package rockland.elysiancrest.com.data_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryFreezeDTO {

    private Long productId;         // The ID of the product being reserved
    private String productName;     // The name of the product being reserved
    private Long userId;            // The ID of the user who made the reservation
    private int quantityReserved;   // The number of items being reserved
    private BigDecimal price;       // The total price of the reserved quantity (optional, depending on use case)
    private LocalDateTime reservedAt;  // The date and time when the reservation was made

}
