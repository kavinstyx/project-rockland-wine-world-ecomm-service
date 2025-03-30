package rockland.elysiancrest.com.data_service.dto;

import lombok.Data;

@Data
public class EventOrderRequestItem {
    private Long productId;
    private String productName;
    private int quantity;
}
