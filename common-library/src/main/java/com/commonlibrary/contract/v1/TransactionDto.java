package com.commonlibrary.contract.v1;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransactionDto {
    private Long id;
    private Long paymentRequestId;
    private Long orderId;
    private String transactionId;
    private String status;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime transactionDate;
}
