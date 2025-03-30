package com.commonlibrary.contract.v1;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id; // ID from BaseEntity
    private OrderStatus status;
    private Long userId; // Referencing user by ID
    private List<OrderItem> orderItems; // List of order items
    private BigDecimal totalPrice;
    private String currency;
    private String deliveryAddress;
    private String paymentMethod;
    private LocalDateTime orderDate;
    private Long paymentId;
    private List<TransactionDto> transactions;
    private List<Long> complaintIds;
    private String email;
    private String eventOrderMessage;
    private String eventOrderType;
    private String orderNote;

}

