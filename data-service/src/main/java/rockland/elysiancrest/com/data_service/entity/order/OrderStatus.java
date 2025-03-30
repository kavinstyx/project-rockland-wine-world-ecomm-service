package rockland.elysiancrest.com.data_service.entity.order;

public enum OrderStatus {
    NEW,
    OPEN,            // Newly created order
    PROCESSING,     // Order is being processed
    SHIPPED,        // Order has been shipped
    DELIVERED,      // Order has been delivered
    CANCELLED,      // Order was cancelled
    COMPLETED,      // Order is completed
    DISPUTED,
    RETURNED ,       // Order was returned
    PAYMENT_PENDING,
    DECLINED,
    ONHOLD
}

