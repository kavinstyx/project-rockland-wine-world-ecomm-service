package rockland.elysiancrest.com.data_service.entity.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import rockland.elysiancrest.com.data_service.entity.BaseEntity;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_request_id")
    private PaymentRequest paymentRequest;

    @Column(name = "transaction_id", unique = false)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderMaster order;

    @Column(name = "status")
    private String status;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    // Payment-specific fields
    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "payhere_amount", precision = 10, scale = 2)
    private BigDecimal payhereAmount;

    @Column(name = "payhere_currency")
    private String payhereCurrency;

    @Column(name = "method")
    private String method;

    @Column(name = "card_holder_name")
    private String cardHolderName;

    @Column(name = "card_no")
    private String cardNo;

    @Column(name = "card_expiry")
    private String cardExpiry;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "customer_address")
    private String customerAddress;

    @Column(name = "customer_city")
    private String customerCity;

    @Column(name = "customer_country")
    private String customerCountry;

    @Column(name = "payment_hash")
    private String paymentHash;

    @Column(name = "gateway_version")
    private String gatewayVersion;

    @Column(name = "hash_version")
    private String hashVersion;

    @Column(name = "status_message")
    private String statusMessage;

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", transactionDate=" + transactionDate +
                ", merchantId='" + merchantId + '\'' +
                ", payhereAmount=" + payhereAmount +
                ", payhereCurrency='" + payhereCurrency + '\'' +
                ", method='" + method + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", cardExpiry='" + cardExpiry + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", customerAddress='" + customerAddress + '\'' +
                ", customerCity='" + customerCity + '\'' +
                ", customerCountry='" + customerCountry + '\'' +
                ", paymentHash='" + paymentHash + '\'' +
                ", gatewayVersion='" + gatewayVersion + '\'' +
                ", hashVersion='" + hashVersion + '\'' +
                ", statusMessage='" + statusMessage + '\'' +
                '}';
    }
}
