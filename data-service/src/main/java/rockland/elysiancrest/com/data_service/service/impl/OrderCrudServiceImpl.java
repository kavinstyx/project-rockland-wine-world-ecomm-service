package rockland.elysiancrest.com.data_service.service.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import rockland.elysiancrest.com.data_service.dto.OrderAddonDTO;
import rockland.elysiancrest.com.data_service.dto.OrderDTO;
import rockland.elysiancrest.com.data_service.dto.OrderSummeryDTO;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderStatus;
import rockland.elysiancrest.com.data_service.exception.ResourceNotFoundException;
import rockland.elysiancrest.com.data_service.repo.OrderRepo;
import rockland.elysiancrest.com.data_service.service.EmailService;
import rockland.elysiancrest.com.data_service.service.OrderCrudService;
import rockland.elysiancrest.com.data_service.service.OrderHistoryService;
import rockland.elysiancrest.com.data_service.service.UserService;
import rockland.elysiancrest.com.data_service.util.AuthenticatedUserUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class OrderCrudServiceImpl  extends CrudServiceImpl<OrderMaster, Long, OrderRepo, OrderDTO> implements OrderCrudService {
    private final OrderRepo orderRepo;
    private final EmailService emailService;
    private final UserService userService;
    private final OrderHistoryService orderHistoryService;


    public OrderCrudServiceImpl(OrderRepo repository, ModelMapper modelMapper, OrderRepo orderRepo, EmailService emailService, UserService userService, OrderHistoryService orderHistoryService) {
        super(repository, modelMapper);
        this.orderRepo = orderRepo;
        this.emailService = emailService;
        this.userService = userService;
        this.orderHistoryService = orderHistoryService;
    }


    @Override
    public Page<OrderSummeryDTO> searchOrders(String orderStatus, String searchString, Long cityId, Long userId,
                                              LocalDateTime startDate, LocalDateTime endDate, String eventOrderType,
                                              PageRequest pageRequest) {
        log.info("searchOrders() method called with filters");

        try {
            // Step 1: Create specifications based on provided filters
            Specification<OrderMaster> spec = Specification.where(null);

            // Filter by order status
            if (orderStatus != null && !orderStatus.isEmpty()) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), OrderStatus.valueOf(orderStatus.toUpperCase())));
            }

            // Filter by search string in delivery address, session ID, customer name, or user contact numbers
            if (searchString != null && !searchString.isEmpty()) {
                spec = spec.and((root, query, cb) -> {
                    Join<Object, Object> userJoin = root.join("user", JoinType.LEFT);
                    return cb.or(
                            cb.like(root.get("deliveryAddress"), "%" + searchString + "%"),
                            cb.like(root.get("customerName"), "%" + searchString + "%"),
                            cb.like(userJoin.get("contactNumbers"), "%" + searchString + "%"),
                            cb.like(userJoin.get("sku"), "%" + searchString + "%")
                    );
                });
            }

            // Filter by city ID
            if (cityId != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("cityId"), cityId));
            }

            // Filter by user ID
            if (userId != null) {
                spec = spec.and((root, query, cb) -> {
                    Join<OrderMaster, User> userJoin = root.join("user", JoinType.LEFT);
                    return cb.equal(userJoin.get("id"), userId); // Ensure `id` matches the user's ID field
                });
            }

            // Filter by order date range
            if (startDate != null && endDate != null) {
                spec = spec.and((root, query, cb) -> cb.between(root.get("orderDate"), startDate, endDate));
            }

            // Filter by event order type
            if (eventOrderType != null && !eventOrderType.isEmpty()) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("eventOrderType"), eventOrderType));
            }

            // Add sorting to PageRequest
            PageRequest sortedPageRequest = PageRequest.of(
                    pageRequest.getPageNumber(),
                    pageRequest.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "id") // Replace "orderDate" with your desired field
            );

            // Step 2: Fetch filtered and paginated results
            Page<OrderMaster> orderPage = orderRepo.findAll(spec, sortedPageRequest);

            // Step 3: Map entities to DTOs
            return orderPage.map(this::convertToDTO);
        } catch (Exception e) {
            log.error("Error occurred while searching orders: ", e);
            throw new ResourceNotFoundException("Failed to search orders");
        }
    }


    @Override
    public void updateOrderStatus(Long orderId, String newStatus) {
        // Validate the new status
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + newStatus);
        }

        // Find the order by ID
        OrderMaster order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        String historyMessage = "Status Updated "+order.getStatus()+" -> "+status;

        // Update the status
        order.setStatus(status);
        OrderMaster saveOrder =  orderRepo.save(order);
        this.orderHistoryService.addOrderHistory(order,"Status Updated", historyMessage);

        // Check if the user is null before proceeding
        if (order.getEventOrderType() == null && order.getUser() == null) {
            log.warn("User is null for order ID: {}", orderId);
            return; // Do not proceed with email if user is null
        }


        // Send an email if the status is DELIVERED or CANCELLED
        if (status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED || status == OrderStatus.ONHOLD){
            sendStatusChangeEmail(saveOrder);
        }
    }

    @Override
    public void updateOrderNote(Long orderId, String note) {
        // Find the order by ID
        OrderMaster order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        // Update the note
        order.setAdminNote(note);
        orderRepo.save(order);

        this.orderHistoryService.addOrderHistory(order,"Order Note Added",note);

    }


    private OrderSummeryDTO convertToDTO(OrderMaster orderMaster) {
        if (orderMaster == null) {
            return null;
        }

        OrderSummeryDTO dto = new OrderSummeryDTO();
        dto.setId(orderMaster.getId());
        dto.setStatus(orderMaster.getStatus() != null ? orderMaster.getStatus().name() : null);
        dto.setTotalPrice("USD".equalsIgnoreCase(orderMaster.getCurrency())
                ? orderMaster.getTotalPriceInDollar()
                : orderMaster.getTotalPriceInRupee());
        dto.setCurrency(orderMaster.getCurrency());
        dto.setDeliveryAddress(orderMaster.getDeliveryAddress());
        dto.setPaymentMethod(orderMaster.getPaymentMethod());
        dto.setCustomerName(orderMaster.getCustomerName());
        dto.setCity(orderMaster.getCityName());
        dto.setCityId(orderMaster.getCityId());
        dto.setContactNumber(orderMaster.getUser() != null ? orderMaster.getUser().getContactNumbers() : null);
        dto.setOrderDate(orderMaster.getOrderDate());
        dto.setGifteeName(orderMaster.getGifteeName());
        dto.setGifterName(orderMaster.getGifterName());
        dto.setGifterMessage(orderMaster.getGifterMessage());
        dto.setGifteeContactNumber(orderMaster.getGifteeContactNumber());
        dto.setBillingAddress(orderMaster.getBillingAddress());
        dto.setDeliveryDate(orderMaster.getDeliveryDate());
        dto.setEventOrderType(orderMaster.getEventOrderType());
        dto.setAddonTotalInDollar(orderMaster.getAddonTotalInDollar());
        dto.setAddonTotalInRupee(orderMaster.getAddonTotalInRupee());
        dto.setContactNumber(orderMaster.getContactNumbers());

        dto.setOrderAddons(orderMaster.getOrderAddons().stream()
                .map(addon -> modelMapper.map(addon, OrderAddonDTO.class))
                .collect(Collectors.toList()));

        dto.setEmail(orderMaster.getEmail());
        dto.setDeliveryFeeInRupee(orderMaster.getDeliveryFeeInRupee());
        dto.setDeliveryFeeInDollar(orderMaster.getDeliveryFeeInDollar());
        dto.setDeliveryOption(orderMaster.getDeliveryOption());

        // Map order items
        List<OrderSummeryDTO.OrderItemDTO> orderItemDTOs = orderMaster.getOrderItems().stream()
                .map(item -> {
                    OrderSummeryDTO.OrderItemDTO itemDTO = new OrderSummeryDTO.OrderItemDTO();
                    itemDTO.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
                    itemDTO.setProductName(item.getProductName());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setTotalPrice("USD".equalsIgnoreCase(orderMaster.getCurrency())
                            ? item.getTotalPriceInDollar()
                            : item.getTotalPriceInRupee());
                    return itemDTO;
                })
                .toList();

        dto.setOrderItems(orderItemDTOs);

        return dto;
    }

    private void sendStatusChangeEmail(OrderMaster savedOrder) {

        if (savedOrder == null) {
            log.warn("Saved order is null, cannot send email.");
            return;
        }

        User user = savedOrder.getUser();

        if (savedOrder.getEventOrderType() == null  && user == null) {
            log.warn("User is null for order ID: {}, cannot send email.", savedOrder.getId());
            return;
        }

        Optional<User> userOptional = userService.findById(user.getId());

        if (userOptional.isEmpty()) {
            System.out.println("User not found for sending E-mail");
        }

        if (userOptional.isPresent()) {
            String userEmail = userOptional.get().getEmail();

            if (userEmail != null && !userEmail.isEmpty()) {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
                String formattedDeliveryDate = savedOrder.getDeliveryDate() != null
                        ? savedOrder.getDeliveryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter)
                        : null;

                String formattedOrderDate = savedOrder.getOrderDate() != null
                        ? savedOrder.getOrderDate().format(formatter)
                        : "Unknown date";
                Context context = new Context();
                context.setVariable("orderReference", savedOrder.getId() != null ? savedOrder.getId().toString() : "Unknown ID");
                context.setVariable("deliveryDate", formattedDeliveryDate);
                context.setVariable("orderDate", formattedOrderDate);
                context.setVariable("deliveryOption", savedOrder.getDeliveryOption() != null ? savedOrder.getDeliveryOption() : "");

                // Setting the delivery fee based on currency
                String deliveryFee = "LKR".equals(savedOrder.getCurrency().toString())
                        && savedOrder.getDeliveryFeeInRupee() != null ?
                        savedOrder.getDeliveryFeeInRupee().toString()
                        : (savedOrder.getDeliveryFeeInDollar() != null
                        ? savedOrder.getDeliveryFeeInDollar().toString()
                        : "0");
                context.setVariable("deliveryFee", deliveryFee);

                // Setting the total order amount based on currency
                String totalOrderAmount = "LKR".equals(savedOrder.getCurrency().toString())
                        && savedOrder.getTotalPriceInRupee() != null ?
                        savedOrder.getTotalPriceInRupee().toString()
                        : (savedOrder.getTotalPriceInDollar() != null
                        ? savedOrder.getTotalPriceInDollar().toString()
                        : "0");
                context.setVariable("totalOrderamount", totalOrderAmount);
                context.setVariable("orderNote", savedOrder.getOrderNote() != null ? savedOrder.getOrderNote() : "");


                context.setVariable("paymentMethod", savedOrder.getPaymentMethod() != null ? savedOrder.getPaymentMethod() : "Unknown");

                context.setVariable("currency", savedOrder.getCurrency() != null ? savedOrder.getCurrency() : "Unknown");
                context.setVariable("shippingAddress", savedOrder.getDeliveryAddress() != null ? savedOrder.getDeliveryAddress() : "Unknown address");
                context.setVariable("billingAddress", savedOrder.getBillingAddress() != null ? savedOrder.getBillingAddress() : "Unknown address");
                context.setVariable("userEmail", userEmail);
                context.setVariable("userPhoneNumber", savedOrder.getUser() != null && savedOrder.getUser().getContactNumbers() != null
                        ? savedOrder.getUser().getContactNumbers().toString() : "Unknown phone number");
                context.setVariable("method", savedOrder.getMethod() != null ? savedOrder.getMethod() : "PayHere");

                List<Map<String, Object>> orderItems = savedOrder.getOrderItems() != null
                        ? savedOrder.getOrderItems().stream()
                        .map(item -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("productName", item.getProductName() != null ? item.getProductName() : "Unknown product");
                            map.put("quantity", item.getQuantity() != 0 ? item.getQuantity() : 0);

                            //check th currency
                            BigDecimal totalPrice = "LKR".equals(savedOrder.getCurrency()) && item.getTotalPriceInRupee() != null
                                    ? item.getTotalPriceInRupee()
                                    : (item.getTotalPriceInDollar() != null ? item.getTotalPriceInDollar() : BigDecimal.ZERO);
                            map.put("totalPrice", totalPrice);

                            return map;
                        }).collect(Collectors.toList())
                        : new ArrayList<>();

                // collect addons
                List<Map<String, Object>> orderAddons = savedOrder.getOrderAddons() != null
                        ? savedOrder.getOrderAddons().stream()
                        .map(addon -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("addonName", addon.getAddonName() != null ? addon.getAddonName() : "Unknown addon");
                            map.put("quantity", addon.getQuantity() != 0 ? addon.getQuantity() : 0);

                            //check th currency
                            BigDecimal totalPrice = "LKR".equals(savedOrder.getCurrency()) && addon.getTotalPriceInRupee()!= null
                                    ? addon.getTotalPriceInRupee()
                                    : (addon.getTotalPriceInDollar()!= null ? addon.getTotalPriceInDollar() : BigDecimal.ZERO);
                            map.put("totalPrice", totalPrice);

                            return map;
                        }).collect(Collectors.toList())
                        : new ArrayList<>();

                //calculate subtotal of all products
                BigDecimal subTotal = orderItems.stream()
                        .map(item -> (BigDecimal) item.get("totalPrice"))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                //calculate addon subtotal
                BigDecimal addonSubTotal = orderAddons.stream()
                        .map(addon -> (BigDecimal) addon.get("totalPrice"))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                context.setVariable("subTotal", subTotal);
                context.setVariable("orderItems", orderItems);
                context.setVariable("orderAddons", orderAddons);
                //add adon subtotal to context
                context.setVariable("addonSubTotal", addonSubTotal);


                //assume order is a gift
                if (savedOrder.getIsGift()) {
                    context.setVariable("customerName", savedOrder.getCustomerName() != null ? savedOrder.getCustomerName() : "No Customer");
                    context.setVariable("gifterName", savedOrder.getGifterName() != null ? savedOrder.getGifterName() : "No gifter name");
                    context.setVariable("gifteeName", savedOrder.getGifteeName() != null ? savedOrder.getGifteeName() : "No giftee name");
                    context.setVariable("gifteePhoneNumber", savedOrder.getGifteeContactNumber() != null ? savedOrder.getGifteeContactNumber().toString() : "Unknown phone");
                    context.setVariable("gifteeMessage", savedOrder.getGifterMessage().toString());
                } else {
                    context.setVariable("customerName", savedOrder.getCustomerName() != null ? savedOrder.getCustomerName() : "No customer name ");

                }

                // Determine the email template based on the order status
                String emailTemplate;
                String emailSubject;

                if (savedOrder.getStatus() == OrderStatus.DELIVERED) {
                    emailTemplate = "order_delivery.html";
                    emailSubject = "Wine world order delivery";
                } else if (savedOrder.getStatus() == OrderStatus.CANCELLED) {
                    emailTemplate = "order_cancelled.html";
                    emailSubject = "Your Order Has Been Cancelled";
                }  else if (savedOrder.getStatus() == OrderStatus.ONHOLD) {
                    emailTemplate = "order_on_hold.html";
                    emailSubject = "Your Order Is On Hold";
                }  else {
                    return;
                }

                try {
                    emailService.orderConfirmation(
                            userEmail,
                            emailSubject,
                            emailTemplate,
                            context);

                    log.info("Email sent to {} for order status {}", userEmail, savedOrder.getStatus());



                } catch (Exception e) {
                    System.out.println("Failed to send email: " + e.getMessage());
                }

            }
        }
    }
}
