package com.commonlibrary.contract.v1;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentNotification {
    private String status;
    private String orderId;
    private double amount;
}
