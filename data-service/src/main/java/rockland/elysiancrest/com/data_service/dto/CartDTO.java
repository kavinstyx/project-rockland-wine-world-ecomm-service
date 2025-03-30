package rockland.elysiancrest.com.data_service.dto;

import com.commonlibrary.contract.v1.City;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long id;
    private String cartType;
    private List<CartItemDTO> cartItems;
    private Long userId;
    private double totalPriceInRupee;
    private double totalPriceInDollar;
    private String currencyCode;
    private BigDecimal deliveryChargesInRupee;
    private BigDecimal deliveryChargesInDollar;
    private String sessionId;
    private City city;
    private String deliveryAddress;
    private String billingAddress;
    private String deliveryOption;
    private List<CartAddonDTO> cartAddons;
    private String gifterName;
    private String gifteeName;
    private String gifteeContactNumber;
    private String gifterMessage;
    private Boolean isGift;
    private BigDecimal addonTotalInRupee;
    private BigDecimal addonTotalInDollar;
    private BigDecimal bottleTotalInDollar;
    private BigDecimal bottleTotalInRupee;
    private String orderNote;

}
