package rockland.elysiancrest.com.data_service.entity.cart.gift;

import lombok.Data;

@Data
public class GiftDetailsRequest {

    private String gifterName;
    private String gifteeName;
    private String gifteeContactNumber;
    private String gifterMessage;
    private Boolean isGift;

}
