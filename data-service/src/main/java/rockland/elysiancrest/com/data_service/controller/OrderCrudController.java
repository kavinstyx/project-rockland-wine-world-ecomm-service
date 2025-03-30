package rockland.elysiancrest.com.data_service.controller;

import com.lowagie.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.*;
import rockland.elysiancrest.com.data_service.entity.order.OrderAddon;
import rockland.elysiancrest.com.data_service.entity.order.OrderItemMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;
import rockland.elysiancrest.com.data_service.exception.ResourceNotFoundException;
import rockland.elysiancrest.com.data_service.service.OrderCrudService;
import rockland.elysiancrest.com.data_service.service.OrderHistoryService;
import rockland.elysiancrest.com.data_service.service.OrderService;
import rockland.elysiancrest.com.data_service.service.PdfGenerationService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("api/orders")
@CrossOrigin
public class OrderCrudController extends AbstractCrudController<OrderDTO, Long, OrderCrudService> {

    private final OrderCrudService orderCrudService;
    private final OrderService orderService;
    private final PdfGenerationService pdfGenerationService;
    private final OrderHistoryService orderHistoryService;


    protected OrderCrudController(OrderCrudService service, OrderService orderService,
                                  PdfGenerationService pdfGenerationService,
                                  OrderHistoryService orderHistoryService) {
        super(service);
        this.orderCrudService = service;
        this.orderService = orderService;
        this.pdfGenerationService = pdfGenerationService;
        this.orderHistoryService = orderHistoryService;
    }

    @GetMapping("/order-search")
    @Operation(summary = "Search orders")
    @ApiResponse(responseCode = "200", description = "Orders fetched successfully")
    public ResponseEntity<Page<OrderSummeryDTO>> orderSearch(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "orderStatus", required = false) String orderStatus,
            @RequestParam(name = "searchString", required = false) String searchString,
            @RequestParam(name = "cityId", required = false) Long cityId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(name = "eventOrderType", required = false) String eventOrderType // New parameter
    ) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size);
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            
            if (startDate != null) {
                startDateTime = startDate.atStartOfDay();
            }
            if (endDate != null) {
                endDateTime = endDate.atTime(23, 59, 59);
            }

            Page<OrderSummeryDTO> orders = orderCrudService.searchOrders(
                    orderStatus,
                    searchString,
                    cityId,
                    userId,
                    startDateTime,
                    endDateTime,
                    eventOrderType,
                    pageRequest
            );
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status")
    @ApiResponse(responseCode = "200", description = "Order status updated successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    public ResponseEntity<Response<String>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String newStatus
    ) {
        try {
            log.info("Updating status for order with ID: {} to {}", orderId, newStatus);
            orderCrudService.updateOrderStatus(orderId, newStatus);
            Response<String> response = Response.<String>builder()
                    .code(HttpStatus.OK.value())
                    .status(Status.SUCCESS)
                    .message("Status changed successfully.")
                    .data("Status changed successfully.")
                    .build();
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            log.error("Order not found with ID: {}", orderId, e);

            Response<String> response = Response.<String>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(Status.SUCCESS)
                    .message("Order not found")
                    .data("Order not found")
                    .build();
            return ResponseEntity.status(404).body(response);

        } catch (Exception e) {
            log.error("Error updating order status for order ID: {}", orderId, e);

            Response<String> response = Response.<String>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(Status.SUCCESS)
                    .message("An unexpected error occurred")
                    .data("An unexpected error occurred")
                    .build();
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/{orderId}/note")
    @Operation(summary = "Add note to order")
    @ApiResponse(responseCode = "200", description = "Note added successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    public ResponseEntity<Response<String>> addOrderNote(
            @PathVariable Long orderId,
            @RequestParam String note
    ) {
        try {
            log.info("Updating NOTE for order with ID: {} to {}", orderId, note);
            orderCrudService.updateOrderNote(orderId, note);
            Response<String> response = Response.<String>builder()
                    .code(HttpStatus.OK.value())
                    .status(Status.SUCCESS)
                    .message("Note added successfully.")
                    .data("Note added successfully.")
                    .build();
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            log.error("Order not found with ID: {}", orderId, e);

            Response<String> response = Response.<String>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(Status.SUCCESS)
                    .message("Order not found")
                    .data("Order not found")
                    .build();
            return ResponseEntity.status(404).body(response);

        } catch (Exception e) {
            log.error("Error updating NOTE for order ID: {}", orderId, e);

            Response<String> response = Response.<String>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(Status.SUCCESS)
                    .message("An unexpected error occurred")
                    .data("An unexpected error occurred")
                    .build();
            return ResponseEntity.status(500).body(response);
        }
    }


    @PostMapping("/downloadInvoice/{orderReference}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long orderReference) throws IOException, DocumentException {

        // Fetch the order details from the database based on the orderReference (or ID)
        Optional<OrderMaster> orderOptional = orderService.findById(orderReference);

        if (orderOptional.isEmpty()) {
            // Return an error response as a byte array
            String errorMessage = "Order not found";
            byte[] errorBytes = errorMessage.getBytes(); // Convert error message to byte array
            return new ResponseEntity<>(errorBytes, HttpStatus.NOT_FOUND);
        }

        OrderMaster order = orderOptional.orElseThrow(() -> new IllegalArgumentException("Order not found"));

// Extracting fields safely
        Date deliveryDate = order.getDeliveryDate();
        String deliveryOption = order.getDeliveryOption();
        LocalDateTime orderDate = order.getOrderDate();
        String currency = order.getCurrency();

// Null-safe BigDecimal assignments
        BigDecimal addonSubTotal = (order.getAddonTotalInRupee() != null) ? order.getAddonTotalInRupee() : BigDecimal.ZERO;
        BigDecimal addonSubTotalUSD = (order.getAddonTotalInDollar() != null) ? order.getAddonTotalInDollar() : BigDecimal.ZERO;
        BigDecimal deliveryFee = (order.getDeliveryFeeInRupee() != null) ? order.getDeliveryFeeInRupee() : BigDecimal.ZERO;
        BigDecimal deliveryFeeUSD = (order.getDeliveryFeeInDollar() != null) ? order.getDeliveryFeeInDollar() : BigDecimal.ZERO;
        BigDecimal totalOrderAmount = (order.getTotalPriceInRupee() != null) ? order.getTotalPriceInRupee() : BigDecimal.ZERO;
        BigDecimal totalOrderAmountUSD = (order.getAddonTotalInDollar() != null) ? order.getAddonTotalInDollar() : BigDecimal.ZERO;

        String customerName = order.getCustomerName();
        String billingAddress = order.getBillingAddress();
        String userPhoneNumber = order.getContactNumbers();
        String userEmail = order.getEmail();
        String shippingAddress = order.getDeliveryAddress();

// Null-safe lists
        List<OrderItemMaster> orderItems = (order.getOrderItems() != null) ? order.getOrderItems() : new ArrayList<>();
        List<OrderAddon> orderAddons = (order.getOrderAddons() != null) ? order.getOrderAddons() : new ArrayList<>();

        boolean isGift = (order.getIsGift() != null) ? order.getIsGift() : false; // Assuming it's a Boolean type
        String gifteeName = order.getGifteeName();
        String gifteeContactNumber = order.getGifteeContactNumber();
        String gifterName = order.getGifterName();
        String gifterMessage = order.getGifterMessage();

// Safe calculation of order items subtotal
        BigDecimal orderItemsSubtotal = totalOrderAmount.subtract(addonSubTotal.add(deliveryFee));
        BigDecimal orderItemsSubtotalUSD = totalOrderAmountUSD.subtract(addonSubTotalUSD.add(deliveryFeeUSD));

        String cardNumber = order.getCardNumber();
        String orderNote = order.getOrderNote();

        // Generate PDF using the dynamic data
        byte[] pdfBytes = pdfGenerationService.generatePdf(orderReference, deliveryDate, orderDate,
                currency, addonSubTotal, addonSubTotalUSD,
                deliveryFee, totalOrderAmount, customerName,
                deliveryFeeUSD, totalOrderAmountUSD,
                billingAddress, userPhoneNumber, userEmail, shippingAddress,
                gifteeName, orderItems, orderAddons, isGift, gifteeContactNumber, gifterName, gifterMessage,
                deliveryOption, orderItemsSubtotal, orderItemsSubtotalUSD,cardNumber, orderNote);

        // Return PDF as a downloadable response
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=invoice_" + orderReference + ".pdf");

        this.orderHistoryService.addOrderHistory(order,"Order Invoice Downloaded","Order Invoice Generated and Downloaded");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
