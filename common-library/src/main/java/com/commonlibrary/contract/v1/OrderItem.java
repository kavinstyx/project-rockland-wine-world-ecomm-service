package com.commonlibrary.contract.v1;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItem {

    private Long productId;     // Product ID of the item
    private int quantity;       // Quantity of the product ordered
    private BigDecimal unitPrice; // Price per unit of the product
    private BigDecimal totalPrice; // Total price for this order item
}
