package com.commonlibrary.contract.v1;

public enum OrderStatus {
    NEW,
    OPEN,            // Newly created order
    PROCESSING,     // Order is being processed
    SHIPPED,        // Order has been shipped
    DELIVERED,      // Order has been delivered
    CANCELLED,      // Order was cancelled
    COMPLETED,      // Order is completed
    RETURNED ,        // Order was returned
    DISPUTED,
    PAYMENT_FAILED,  //payment failed
    PAYMENT_PENDING,  //payment was pending
    ONHOLD
}

