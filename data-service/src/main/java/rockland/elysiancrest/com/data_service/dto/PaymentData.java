package rockland.elysiancrest.com.data_service.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PaymentData {
    private String merchant_id;
    private String order_id;
    private String payment_id;
    private String payhere_amount;
    private String payhere_currency;
    private String status_code;
    private String md5sig;
    private String custom_1;
    private String custom_2;
    private String method;
    private String card_holder_name;
    private String card_no;
    private String card_expiry;
    private String customer_email;
    private String customer_phone;
    private String customer_address;
    private String customer_city;
    private String customer_country;
    private String payment_hash;
    private String gateway_version;
    private String hash_version;
    private String status_message;
}