package rockland.elysiancrest.com.data_service.entity.cart.addon;

import lombok.Data;

import java.util.List;

@Data
public class CartSubmitRequest {
    private List<AddonSelection> addons;
}