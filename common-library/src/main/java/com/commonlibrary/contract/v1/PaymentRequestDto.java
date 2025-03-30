package com.commonlibrary.contract.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {
    private Long id;
    private BigDecimal amount;
    private String currency;
    private Long orderId;
    private Boolean success;
    private LocalDateTime paymentDate;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String country;
    private String returnUrl;
    private String cancelUrl;
    private String notifyUrl;
    private String items;
    private String cardNumber;

}
