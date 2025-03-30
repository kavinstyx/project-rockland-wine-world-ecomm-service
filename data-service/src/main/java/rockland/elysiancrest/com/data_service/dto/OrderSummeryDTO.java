package rockland.elysiancrest.com.data_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Data
public class OrderSummeryDTO {
    private Long id;
    private String status;
    private String paymentStatus;
    private BigDecimal totalPrice;
    private String currency;
    private String deliveryAddress;
    private String paymentMethod;
    private String customerName;
    private String city;
    private Long cityId;
    private String contactNumber;
    private LocalDateTime orderDate;
    private List<OrderItemDTO> orderItems;
    private String cityName;
    private String gifterName;
    private String gifteeName;
    private String gifteeContactNumber;
    private String gifterMessage;
    private Boolean isGift;


    private List<OrderAddonDTO> orderAddons;
    private BigDecimal totalPriceInRupee;
    private BigDecimal totalPriceInDollar;
    private BigDecimal deliveryFeeInRupee;
    private BigDecimal deliveryFeeInDollar;
    private String billingAddress;
    private String sessionId;
    private String cartType;
    private String email;
    private String eventOrderMessage;
    private String eventOrderType;
    private String localContactNumber;
    private Date deliveryDate;
    private BigDecimal addonTotalInRupee;
    private BigDecimal addonTotalInDollar;
    private String deliveryOption;


    @Data
    public static class OrderItemDTO {
        private Long productId;
        private String productName;
        private int quantity;
        private BigDecimal totalPrice;
        private String imageUrl;

        private BigDecimal totalPriceInRupee;
        private BigDecimal totalPriceInDollar;
        private BigDecimal unitPriceInRupee;
        private BigDecimal unitPriceInDollar;
        private Long orderId;
    }


}
