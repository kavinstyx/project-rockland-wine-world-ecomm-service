package rockland.elysiancrest.com.data_service.service.impl;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import rockland.elysiancrest.com.data_service.dto.EventOrderRequest;
import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderItemMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderStatus;
import rockland.elysiancrest.com.data_service.repo.OrderRepo;
import rockland.elysiancrest.com.data_service.repo.ProductMasterRepo;
import rockland.elysiancrest.com.data_service.service.EmailService;
import rockland.elysiancrest.com.data_service.service.EventOrderService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventOrderServiceImpl implements EventOrderService {

    private final OrderRepo orderRepo;

    private final ProductMasterRepo productMasterRepo;

    private final EmailService emailService;

    @Value("${wedding.contactus}")
    private String adminWeddingEmail;

    @Value("${corporate.contactus}")
    private String adminCorporateEmail;

    public EventOrderServiceImpl(OrderRepo orderRepo, ProductMasterRepo productMasterRepo, EmailService emailService) {
        this.orderRepo = orderRepo;
        this.productMasterRepo = productMasterRepo;
        this.emailService = emailService;
    }

    @Override
    public Long processAndSaveOrder(EventOrderRequest eventOrderRequest) {
        OrderMaster order = new OrderMaster();
        order.setCustomerName(eventOrderRequest.getName());
        order.setEmail(eventOrderRequest.getEmail());
        order.setContactNumbers(eventOrderRequest.getPhone());
        order.setOrderDate(eventOrderRequest.getDate());
        order.setEventOrderMessage(eventOrderRequest.getMessage());
        order.setEventOrderType(eventOrderRequest.getEventOrderType());
        order.setStatus(OrderStatus.OPEN);

        //map order items from DTO and set them directly into order master
        order.setOrderItems(eventOrderRequest.getItems().stream().map(item -> {
            OrderItemMaster orderItem = new OrderItemMaster();
            ProductMaster product = productMasterRepo.findById(item.getProductId()).orElseThrow(() ->
                    new RuntimeException("Product with ID" + item.getProductId() + " not found"));

            orderItem.setProduct(product);
            orderItem.setProductName(item.getProductName());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setOrder(order);
            return orderItem;

        }).collect(Collectors.toList()));

        //save the order with order items
        orderRepo.save(order);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String formattedDate = order.getOrderDate() != null ? order.getOrderDate().format(formatter) : "Unknown Date";
        //send email to customer
        Context customerContext = new Context();
        customerContext.setVariable("recipientName", order.getCustomerName());
        customerContext.setVariable("phone", order.getContactNumbers());
        customerContext.setVariable("email", order.getEmail());
        customerContext.setVariable("messageContent", order.getEventOrderMessage());
        customerContext.setVariable("eventOrderType", order.getEventOrderType());
        customerContext.setVariable("orderReference", order.getId());
        customerContext.setVariable("orderDate", formattedDate);

        List<Map<String, Object>> orderItems = order.getOrderItems() != null
                ? order.getOrderItems().stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productName", item.getProductName() != null ? item.getProductName() : "Unknown product");
                    map.put("quantity", item.getQuantity() != 0 ? item.getQuantity() : 0);
                    return map;
                }).collect(Collectors.toList())
                : new ArrayList<>();

        customerContext.setVariable("orderItems", orderItems);

        try {
            emailService.eventOrderEmail(
                    eventOrderRequest.getEmail(),
                    "Thank You for Contacting Us With Your Event Order",
                    "customer_event_order.html",
                    customerContext
            );
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send customer contact email", e);
        }

        String adminEmail = order.getEventOrderType().equalsIgnoreCase("weddings") ? adminWeddingEmail :adminCorporateEmail;
        try {
            emailService.eventOrderEmail(
                    adminEmail,
                    "Event Order Inquiry",
                    "admin_event_order_email.html",
                    customerContext
            );
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send customer contact email", e);
        }


        return order.getId(); //return order ID    }
    }
}
