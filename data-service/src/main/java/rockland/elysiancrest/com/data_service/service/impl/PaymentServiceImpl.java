package rockland.elysiancrest.com.data_service.service.impl;

import com.commonlibrary.contract.v1.*;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.context.Context;
import rockland.elysiancrest.com.data_service.dto.DeliveryChargeResponse;
import rockland.elysiancrest.com.data_service.dto.OrderDTO;
import rockland.elysiancrest.com.data_service.dto.PaymentData;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.entity.cart.CartItem;
import rockland.elysiancrest.com.data_service.entity.cart.CartMaster;
import rockland.elysiancrest.com.data_service.entity.master_data.CityMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderItemMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderStatus;
import rockland.elysiancrest.com.data_service.entity.payment.PaymentRequest;
import rockland.elysiancrest.com.data_service.entity.payment.PaymentStatus;
import rockland.elysiancrest.com.data_service.entity.payment.Transaction;
import rockland.elysiancrest.com.data_service.repo.OrderRepo;
import rockland.elysiancrest.com.data_service.repo.PaymentRepo;
import rockland.elysiancrest.com.data_service.repo.TransactionRepo;
import rockland.elysiancrest.com.data_service.service.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final EmailService emailService;
    @Value("${payhere.merchant_id}")
    private String merchantId;

    @Value("${payhere.merchant_secret}")
    private String merchantSecret;

    @Value("${payhere.api.baseurl}")
    private String payHereBaseUrl;

    @Value("${payhere.return_url}")
    private String returnUrl;

    @Value("${payhere.cancel_url}")
    private String cancelUrl;

    @Value("${app.domain}")
    private String appDomain;

    private final OrderRepo orderRepo;
    private final PaymentRepo paymentRepo;
    private final TransactionRepo transactionRepo;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final CityMasterService cityMasterService;
    private final PlantMasterService plantMasterService;
    private final DeliveryChargeMasterService deliveryChargeMasterService;
    private final InventoryService inventoryService;
    private final OrderHistoryService orderHistoryService;

    public PaymentServiceImpl(OrderRepo orderRepo, PaymentRepo paymentRepo, TransactionRepo transactionRepo, ModelMapper modelMapper, EmailService emailService, UserService userService, CityMasterService cityMasterService, PlantMasterService plantMasterService, DeliveryChargeMasterService deliveryChargeMasterService, InventoryService inventoryService, OrderHistoryService orderHistoryService) {
        this.orderRepo = orderRepo;
        this.paymentRepo = paymentRepo;
        this.transactionRepo = transactionRepo;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
        this.userService = userService;
        this.cityMasterService = cityMasterService;
        this.plantMasterService = plantMasterService;
        this.deliveryChargeMasterService = deliveryChargeMasterService;
        this.inventoryService = inventoryService;
        this.orderHistoryService = orderHistoryService;
    }


    @Override
    public PaymentResult initiatePayment(PaymentRequestDto paymentRequestDto) {
        OrderMaster order = orderRepo.findById(paymentRequestDto.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + paymentRequestDto.getOrderId()));
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrder(order);
        paymentRequest.setAmount(paymentRequestDto.getAmount());
        paymentRequest.setCurrency(paymentRequestDto.getCurrency());
        paymentRequest.setPaymentDate(LocalDateTime.now());
        paymentRequest = paymentRepo.save(paymentRequest);

        Transaction transaction = new Transaction();
        transaction.setPaymentRequest(paymentRequest);
        transaction.setOrder(order);
        transaction.setTransactionId("TRANS-" + System.currentTimeMillis());
        transaction.setStatus("INITIATED");
        transaction.setAmount(paymentRequestDto.getAmount());
        transaction.setCurrency(paymentRequestDto.getCurrency());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction = transactionRepo.save(transaction);

        TransactionDto transactionDto = mapToDto(transaction);
        String paymentUrl = generatePaymentUrl(paymentRequestDto);

        String orderHistoryMessage = "Payment Link "+paymentUrl +" \n"+transactionDto.toString();
        this.orderHistoryService.addOrderHistory(order,"Order Payment Initiate",orderHistoryMessage);

        return new PaymentResult(transactionDto, paymentUrl);
    }

    private TransactionDto mapToDto(Transaction transaction) {
        return modelMapper.map(transaction, TransactionDto.class);
    }

    @Override
    public void processReturn(Long paymentId, String payerId, String status) {
        Transaction transaction = transactionRepo.findById(paymentId).orElseThrow();
        transaction.setStatus(status);
        transactionRepo.save(transaction);

        OrderMaster order = orderRepo.findById(transaction.getOrder().getId()).orElseThrow();
        String historyMessage = "Status Updated "+order.getStatus()+" -> "+OrderStatus.RETURNED+" \n Reason: Payment Returned "+transaction.toString();
        order.setStatus(OrderStatus.RETURNED);
        orderRepo.save(order);


        this.orderHistoryService.addOrderHistory(order,"Status Updated", historyMessage);


        PaymentRequest paymentRequest = paymentRepo.findById(paymentId).orElseThrow();
        paymentRequest.setStatus(status);
        paymentRepo.save(paymentRequest);

    }

    @Override
    public void processCancel(Long paymentId) {
        Transaction transaction = transactionRepo.findById(paymentId).orElseThrow();
        transaction.setStatus("CANCELLED");
        transactionRepo.save(transaction);

        OrderMaster order = orderRepo.findById(transaction.getOrder().getId()).orElseThrow();
        String historyMessage = "Status Updated "+order.getStatus()+" -> "+OrderStatus.PAYMENT_PENDING+" \n Reason: Payment Cancelled "+transaction.toString();

        order.setStatus(OrderStatus.PAYMENT_PENDING);
        orderRepo.save(order);

        this.orderHistoryService.addOrderHistory(order,"Status Updated", historyMessage);

        PaymentRequest paymentRequest = paymentRepo.findById(paymentId).orElseThrow();
        paymentRequest.setStatus("CANCELLED");
        paymentRepo.save(paymentRequest);

    }

    @Override
    public void processNotification(PaymentData paymentData) {

        Long paymentId = Long.valueOf(paymentData.getPayment_id());
        Long orderId = Long.valueOf(paymentData.getOrder_id());
        int statusId = Integer.parseInt(paymentData.getStatus_code());
        PaymentStatus paymentStatus = PaymentStatus.fromCode(statusId);

        // Fetch the Transaction and update its status
        Transaction transaction = transactionRepo.findByOrderId(orderId).orElseThrow();
        transaction.setStatus(paymentStatus.getDescription());
        transaction.setTransactionId(paymentData.getPayment_id());
        transaction.setMerchantId(paymentData.getMerchant_id());
        transaction.setPayhereAmount(new BigDecimal(paymentData.getPayhere_amount()));
        transaction.setPayhereCurrency(paymentData.getPayhere_currency());
        transaction.setMethod(paymentData.getMethod());
        transaction.setCardHolderName(paymentData.getCard_holder_name());
        transaction.setCardNo(paymentData.getCard_no());
        transaction.setCardExpiry(paymentData.getCard_expiry());
        transaction.setCustomerEmail(paymentData.getCustomer_email());
        transaction.setCustomerPhone(paymentData.getCustomer_phone());
        transaction.setCustomerAddress(paymentData.getCustomer_address());
        transaction.setCustomerCity(paymentData.getCustomer_city());
        transaction.setCustomerCountry(paymentData.getCustomer_country());
        transaction.setPaymentHash(paymentData.getPayment_hash());
        transaction.setGatewayVersion(paymentData.getGateway_version());
        transaction.setHashVersion(paymentData.getHash_version());
        transaction.setStatusMessage(paymentData.getStatus_message());

        transactionRepo.save(transaction);

        // Fetch the associated OrderMaster and PaymentRequest
        OrderMaster order = orderRepo.findById(orderId).orElseThrow();
        order.setCardNumber(paymentData.getCard_no());
        order.setMethod(paymentData.getMethod());
        order.setStatusMessage(paymentData.getStatus_message());
        PaymentRequest paymentRequest = paymentRepo.findByOrderId(orderId).orElseThrow();
        paymentRequest.setCardNumber(paymentData.getCard_no());
        City city = cityMasterService.findById(order.getCityId());
        Optional<User> user = userService.findById(order.getUser().getId());

        // Switch case to handle business logic based on PaymentStatus
        switch (paymentStatus) {
            case SUCCESS:
                // Payment successful
                String paymentSuccessHistoryMessage = "Status Updated "+order.getStatus()+" -> "+OrderStatus.PROCESSING+" \nReason: Payment Success. \nPayment Data: "+ paymentData.toString()+" \nTransaction Data: "+transaction.toString();

                order.setStatus(OrderStatus.PROCESSING);
                order.setPaymentStatus(PaymentStatus.SUCCESS);
                orderRepo.save(order);

                this.orderHistoryService.addOrderHistory(order,"Status Updated", paymentSuccessHistoryMessage);

                paymentRequest.setStatus(paymentStatus.getDescription());
                paymentRepo.save(paymentRequest);

                for (OrderItemMaster orderItem : order.getOrderItems()) {
                    inventoryService.createInventoryFreeze(orderItem.getProduct().getId(), orderItem.getQuantity(),
                            user.get().getId(), order.getId(), city.getPlants().get(0).getId());

                }

                // Additional actions (e.g., send confirmation email)
                sendOrderConfirmationEmail(order);
                sendOrderEmailToTheStore(order);
                break;

            case PENDING:
                // Payment is pending
                String paymentPendingHistoryMessage = "Status Updated "+order.getStatus()+" -> "+OrderStatus.OPEN+" \n Reason: Payment Pending. \nPayment Data: "+ paymentData.toString()+" \nTransaction Data: "+transaction.toString();

                order.setStatus(OrderStatus.OPEN);
                order.setPaymentStatus(PaymentStatus.PENDING);
                orderRepo.save(order);

                this.orderHistoryService.addOrderHistory(order,"Status Updated", paymentPendingHistoryMessage);

                paymentRequest.setStatus(paymentStatus.getDescription());
                paymentRepo.save(paymentRequest);

                //send on hold email
                orderOnHold(order);

                // Additional actions (e.g., notify customer to complete payment)
                // notificationService.sendPendingPaymentAlert(order);
                break;

            case CANCELED:
                // Payment canceled
                String paymentCancelledHistoryMessage = "Status Updated "+order.getStatus()+" -> "+OrderStatus.PAYMENT_PENDING+" \n Reason: Payment Cancelled. \nPayment Data: "+ paymentData.toString()+" \nTransaction Data: "+transaction.toString();

                order.setStatus(OrderStatus.PAYMENT_PENDING);
                order.setPaymentStatus(PaymentStatus.CANCELED);
                orderRepo.save(order);

                this.orderHistoryService.addOrderHistory(order,"Status Updated", paymentCancelledHistoryMessage);

                paymentRequest.setStatus(paymentStatus.getDescription());
                paymentRepo.save(paymentRequest);

                // Additional actions (e.g., log cancellation reason)
                // log.info("Payment canceled for Order ID: " + order.getId());
                break;

            case FAILED:
                // Payment failed
                String paymentFailedHistoryMessage = "Status Updated "+order.getStatus()+" -> "+OrderStatus.DECLINED+" \n Reason: Payment Failed. \nPayment Data: "+ paymentData.toString()+" \nTransaction Data: "+transaction.toString();

                order.setStatus(OrderStatus.DECLINED);
                order.setPaymentStatus(PaymentStatus.FAILED);
                orderRepo.save(order);

                this.orderHistoryService.addOrderHistory(order,"Status Updated", paymentFailedHistoryMessage);

                paymentRequest.setStatus(paymentStatus.getDescription());
                paymentRepo.save(paymentRequest);

                //send faliure email
                orderFailed(order);

                // Additional actions (e.g., retry mechanism)
                // paymentRetryService.scheduleRetry(paymentRequest);
                break;

            case CHARGEDBACK:
                // Payment chargeback
                String paymentChargedbackHistoryMessage = "Status Updated "+order.getStatus()+" -> "+OrderStatus.DISPUTED+" \n Reason: Payment Failed. \nPayment Data: "+ paymentData.toString()+" \nTransaction Data: "+transaction.toString();

                order.setStatus(OrderStatus.DISPUTED);
                order.setPaymentStatus(PaymentStatus.CHARGEDBACK);
                orderRepo.save(order);
                this.orderHistoryService.addOrderHistory(order,"Status Updated", paymentChargedbackHistoryMessage);


                paymentRequest.setStatus(paymentStatus.getDescription());
                paymentRepo.save(paymentRequest);

                // Additional actions (e.g., open dispute case)
                // disputeService.openDisputeCase(order);
                break;

            default:
                throw new IllegalStateException("Unexpected PaymentStatus: " + paymentStatus);
        }

        System.out.println("Processed payment notification for status: " + paymentStatus.getDescription());
    }

    @Override
    public String generatePaymentUrl(PaymentRequestDto paymentRequest) {
        return UriComponentsBuilder.fromUriString(payHereBaseUrl)
                .queryParam("merchant_id", merchantId)
                .queryParam("return_url", appDomain)
                .queryParam("cancel_url", appDomain + "/payment/cancel")
                .queryParam("notify_url", appDomain + "/api/payment/notify")
                .queryParam("first_name", paymentRequest.getFirstName())
                .queryParam("last_name", paymentRequest.getLastName())
                .queryParam("email", paymentRequest.getEmail())
                .queryParam("phone", paymentRequest.getPhone())
                .queryParam("address", paymentRequest.getAddress())
                .queryParam("city", paymentRequest.getCity())
                .queryParam("country", paymentRequest.getCountry())
                .queryParam("order_id", paymentRequest.getOrderId())
                .queryParam("items", paymentRequest.getItems())
                .queryParam("currency", paymentRequest.getCurrency())
                .queryParam("amount", paymentRequest.getAmount())
                .queryParam("hash", calc(String.valueOf(paymentRequest.getOrderId()), paymentRequest.getAmount().doubleValue(), paymentRequest.getCurrency()))
                .toUriString();

    }

//    merchant_id - PayHere Merchant ID
//    return_url - URL to redirect users when payment is approved
//    cancel_url - URL to redirect users when user cancel the payment
//    notify_url - URL to callback the status of the payment (Needs to be a URL accessible on a public IP/domain)
//    first_name - Customer’s First Name
//    last_name - Customer’s Last Name
//    email - Customer’s Email
//    phone - Customer’s Phone No
//    address - Customer’s Address Line1 + Line2
//    city - Customer’s City
//    country - Customer’s Country
//    order_id - Order ID generated by the merchant
//    items - Item title or Order/Invoice number
//    currency - Currency Code (LKR/USD)
//    amount - Total Payment Amount
//    hash - Generated hash value as mentioned below (*Required from 2023-01-16)

    public String calc(String orderID, double amount, String currencyCode) {
        String merahantID = merchantId;
        String secret = this.merchantSecret;
        String currency = currencyCode;
        DecimalFormat df = new DecimalFormat("0.00");
        String amountFormatted = df.format(amount);
        String hash = getMd5(merahantID + orderID + amountFormatted + currency + getMd5(secret));
        System.out.println("Generated Hash: " + hash);
        return hash;
    }

    public static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext.toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendOrderConfirmationEmail(OrderMaster savedOrder) {

        User user = savedOrder.getUser();
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
                            BigDecimal totalPrice = "LKR".equals(savedOrder.getCurrency()) && addon.getTotalPriceInRupee() != null
                                    ? addon.getTotalPriceInRupee()
                                    : (addon.getTotalPriceInDollar() != null ? addon.getTotalPriceInDollar() : BigDecimal.ZERO);
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

                    try {
                        emailService.orderConfirmation(
                                userEmail,
                                "Gift Order Confirmation",
                                "gift_email.html",
                                context);

                    } catch (Exception e) {
                        System.out.println("Failed to send gift order confirmation email: " + e.getMessage());
                    }
                } else {
                    context.setVariable("customerName", savedOrder.getCustomerName() != null ? savedOrder.getCustomerName() : "No customer name ");


                    try {
                        emailService.orderConfirmation(
                                userEmail,
                                "Order Confirmation",
                                "order_confirm_email.html",
                                context);


                    } catch (Exception e) {
                        System.out.println("Failed to send order confirmation email: " + e.getMessage());
                    }
                }

            }
        }
    }


    private void sendOrderEmailToTheStore(OrderMaster savedOrder) {
        City city = cityMasterService.findById(savedOrder.getCityId());
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);

        Plant plant = plantMasterService.findById(city.getPlants().get(0).getId());
        if (plant != null && plant.getEmail() != null) {
            // Build the message body with item details
            StringBuilder emailMessage = new StringBuilder("Order Summary:\n\n");
            emailMessage.append("Order has been created. Order ID: ").append(orderDTO.getId()).append("\n\n");
            emailMessage.append("Order Details:\n");

            BigDecimal totalOrderPrice = BigDecimal.ZERO;

            for (OrderItemMaster orderItem : savedOrder.getOrderItems()) {
                String productName = orderItem.getProduct().getCorrectName(); // Assuming Product has getCorrectName()
                BigDecimal bottlePrice = orderItem.getProduct().getRegularPriceInRupee(); // Assuming Product has getRegularPriceInRupee()
                int quantity = orderItem.getQuantity();
                BigDecimal itemTotalPrice = bottlePrice.multiply(BigDecimal.valueOf(quantity));
                totalOrderPrice = totalOrderPrice.add(itemTotalPrice);

                // Append item details to the email message
                emailMessage.append("Product: ").append(productName)
                        .append("\nQuantity: ").append(quantity)
                        .append("\nPrice per Bottle: ").append(bottlePrice)
                        .append("\nItem Total Price: ").append(itemTotalPrice)
                        .append("\n\n");
            }

            // Calculate delivery charges based on bottle quantity and city
            int totalBottleQuantity = savedOrder.getOrderItems().stream()
                    .mapToInt(OrderItemMaster::getQuantity)
                    .sum();

            DeliveryChargeResponse deliveryChargeResponse = deliveryChargeMasterService.calculateDeliveryCharge(totalBottleQuantity, city.getId());

            BigDecimal deliveryChargeInRupee = deliveryChargeResponse.getTotalDeliveryChargeInRupee();
            BigDecimal deliveryChargeInDollar = deliveryChargeResponse.getTotalDeliveryChargeInDollar();

            BigDecimal totalOrderPriceIncludingDeliveryInRupee = totalOrderPrice.add(deliveryChargeInRupee);
            BigDecimal totalOrderPriceIncludingDeliveryInDollar = totalOrderPrice.add(deliveryChargeInDollar);


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
            context.setVariable("userEmail", savedOrder.getUser().getEmail() != null ? savedOrder.getUser().getEmail() : "No user email");
            context.setVariable("userPhoneNumber", savedOrder.getUser() != null && savedOrder.getUser().getContactNumbers() != null
                    ? savedOrder.getUser().getContactNumbers().toString() : "Unknown phone number");
            context.setVariable("cardNumber", savedOrder.getCardNumber() != null ? savedOrder.getCardNumber() : "Card Details Not Found");
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
                        BigDecimal totalPrice = "LKR".equals(savedOrder.getCurrency()) && addon.getTotalPriceInRupee() != null
                                ? addon.getTotalPriceInRupee()
                                : (addon.getTotalPriceInDollar() != null ? addon.getTotalPriceInDollar() : BigDecimal.ZERO);
                        map.put("totalPrice", totalPrice);

                        return map;
                    }).collect(Collectors.toList())
                    : new ArrayList<>();

            //addon sub total
            //calculate addon subtotal
            BigDecimal addonSubTotal = orderAddons.stream()
                    .map(addon -> (BigDecimal) addon.get("totalPrice"))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);


            // collect product discounts
            //calculate subtotal of all products
            BigDecimal subTotal = orderItems.stream()
                    .map(item -> (BigDecimal) item.get("totalPrice"))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            context.setVariable("subTotal", subTotal);
            context.setVariable("orderItems", orderItems);
            context.setVariable("orderAddons", orderAddons);
            context.setVariable("addonSubTotal", addonSubTotal);


            //assume order is a gift
            if (savedOrder.getIsGift()) {
                context.setVariable("customerName", savedOrder.getCustomerName() != null ? savedOrder.getCustomerName() : "No Customer");
                context.setVariable("gifterName", savedOrder.getGifterName() != null ? savedOrder.getGifterName() : "No gifter name");
                context.setVariable("gifteeName", savedOrder.getGifteeName() != null ? savedOrder.getGifteeName() : "No giftee name");
                context.setVariable("gifteePhoneNumber", savedOrder.getGifteeContactNumber() != null ? savedOrder.getGifteeContactNumber().toString() : "Unknown phone");
                context.setVariable("gifteeMessage", savedOrder.getGifterMessage().toString());

                try {
                    emailService.orderConfirmation(
                            plant.getEmail(),
                            "Gift Order Alert",
                            "gift_email.html",
                            context);

                } catch (Exception e) {
                    System.out.println("Failed to send gift order confirmation email: " + e.getMessage());
                }
            } else {
                context.setVariable("customerName", savedOrder.getCustomerName() != null ? savedOrder.getCustomerName() : "No customer name ");


                // Send the email
                try {
                    // Send the email
                    emailService.orderConfirmation(
                            plant.getEmail(),
                            "Order Alert",
                            "store_confirm_email.html",
                            context
                    );
                } catch (Exception e) {
                    System.out.println("Failed to send order confirmation email: " + e.getMessage());
                }
            }
        }
    }

    private void orderFailed(OrderMaster savedOrder) {

        User user = savedOrder.getUser();
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
                context.setVariable("statusMessage", savedOrder.getStatusMessage() != null ? savedOrder.getStatusMessage() : "Unknown");

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
                            BigDecimal totalPrice = "LKR".equals(savedOrder.getCurrency()) && addon.getTotalPriceInRupee() != null
                                    ? addon.getTotalPriceInRupee()
                                    : (addon.getTotalPriceInDollar() != null ? addon.getTotalPriceInDollar() : BigDecimal.ZERO);
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

                try {
                    emailService.orderConfirmation(
                            userEmail,
                            "Order Failed",
                            "order_failed.html",
                            context);


                } catch (Exception e) {
                    System.out.println("Failed to send order failed email: " + e.getMessage());
                }

            }
        }
    }

    private void orderOnHold(OrderMaster savedOrder) {

        User user = savedOrder.getUser();
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
                            BigDecimal totalPrice = "LKR".equals(savedOrder.getCurrency()) && addon.getTotalPriceInRupee() != null
                                    ? addon.getTotalPriceInRupee()
                                    : (addon.getTotalPriceInDollar() != null ? addon.getTotalPriceInDollar() : BigDecimal.ZERO);
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

                try {
                    emailService.orderConfirmation(
                            userEmail,
                            "Order On-hold",
                            "order_on_hold.html",
                            context);


                } catch (Exception e) {
                    System.out.println("Failed to send order on hold email: " + e.getMessage());
                }

            }
        }
    }

}
