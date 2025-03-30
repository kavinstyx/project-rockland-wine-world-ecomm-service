package rockland.elysiancrest.com.data_service.entity.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.entity.Complaint;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.entity.payment.PaymentRequest;
import rockland.elysiancrest.com.data_service.entity.payment.PaymentStatus;
import rockland.elysiancrest.com.data_service.entity.payment.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "order_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
//@EntityListeners(AuditLogListener.class)
public class OrderMaster extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = true)
    @JsonBackReference // Prevents cyclic reference serialization for User
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItemMaster> orderItems = new ArrayList<>();

    @Column(name = "total_price_in_rupee", precision = 10, scale = 2)
    private BigDecimal totalPriceInRupee;

    @Column(name = "total_price_in_dollar", precision = 10, scale = 2)
    private BigDecimal totalPriceInDollar;

    @Column(name = "delivery_fee_in_rupee", precision = 10, scale = 2)
    private BigDecimal deliveryFeeInRupee;

    @Column(name = "delivery_fee_in_dollar", precision = 10, scale = 2)
    private BigDecimal deliveryFeeInDollar;

    @Column(name = "currency")
    private String currency;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "billing_address")
    private String billingAddress;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "contact_numbers")
    private String contactNumbers;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PaymentRequest payment;

    private LocalDateTime orderDate;
    private String sessionId;
    private String cartType;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Complaint> complaints;

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "city_name")
    private String cityName;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderAddon> orderAddons = new ArrayList<>();

    @Column(name = "gifter_name")
    private String gifterName;

    @Column(name = "giftee_name")
    private String gifteeName;

    @Column(name = "giftee_contact_number")
    private String gifteeContactNumber;

    @Column(name = "gifter_message")
    private String gifterMessage;

    @Column(name = "is_gift")
    private Boolean isGift = false;

    @Column(name = "email")
    private String email;

    @Column(name = "event_order_message" , length = 5000)
    private String eventOrderMessage;

    @Column(name = "event_order_type")
    private String eventOrderType;

    @Column(name = "local_contact_number")
    private String localContactNumber;

    @Column(name = "delivery_date")
    private Date deliveryDate;

    @Column(name = "addon_total_in_rupee")
    private BigDecimal addonTotalInRupee;

    @Column(name = "addon_total_in_dollar")
    private BigDecimal addonTotalInDollar;

    @Column(name = "delivery_option")
    private String deliveryOption;

    @Column(name = "admin_note")
    private String adminNote;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "method")
    private String method;

    @Column(name = "order_note")
    private String orderNote;

    @Column(name = "status_message")
    private String statusMessage;

}

