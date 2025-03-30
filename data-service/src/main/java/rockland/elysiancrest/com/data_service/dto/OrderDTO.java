package rockland.elysiancrest.com.data_service.dto;

import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String status;
    private String paymentStatus;
    private String currency;
    private String deliveryAddress;
    private String paymentMethod;
    private String contactNumbers;
    private LocalDateTime orderDate;
    private List<OrderItemDTO> orderItems;
    private List<OrderAddonDTO> orderAddons;

    private BigDecimal totalPriceInRupee;
    private BigDecimal totalPriceInDollar;
    private BigDecimal deliveryFeeInRupee;
    private BigDecimal deliveryFeeInDollar;
    private String billingAddress;
    private String customerName;
    private String sessionId;
    private String cartType;
    private Long cityId;
    private String cityName;
    private String gifterName;
    private String gifteeName;
    private String gifteeContactNumber;
    private String gifterMessage;
    private String email;
    private String eventOrderMessage;
    private String eventOrderType;
    private String localContactNumber;

    private Date deliveryDate;
    private BigDecimal addonTotalInRupee;
    private BigDecimal addonTotalInDollar;
    private String deliveryOption;
    private String adminNote;
    private String cardNumber;
    private String orderNote;
    private String statusMessage;



    @Data
    public static class OrderItemDTO {
        private Long id;
        private int quantity;
        private String productName;
        private BigDecimal totalPriceInRupee;
        private BigDecimal totalPriceInDollar;
        private BigDecimal unitPriceInRupee;
        private BigDecimal unitPriceInDollar;
        private String imageUrl;
        private Long productId;
        private Long orderId;
    }
}

