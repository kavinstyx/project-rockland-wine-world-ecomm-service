package rockland.elysiancrest.com.data_service.entity.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartAddressRequest {
    private String billingAddress;
    private String deliveryAddress;
}

