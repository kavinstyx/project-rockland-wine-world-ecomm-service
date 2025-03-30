package rockland.elysiancrest.com.data_service.controller;


import com.commonlibrary.contract.v1.City;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.*;
import rockland.elysiancrest.com.data_service.dto.config.NextAvailableDaysDTO;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.entity.cart.CartItem;
import rockland.elysiancrest.com.data_service.entity.cart.CartMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;
import rockland.elysiancrest.com.data_service.service.*;
import rockland.elysiancrest.com.data_service.service.impl.PlantMasterServiceImpl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final CartService cartService;
    private final OrderService orderService;
    private final InventoryService inventoryService;
    private final CityMasterService cityMasterService;
    private final OrderHistoryService orderHistoryService;
    private final HolidayService holidayService;

    // Constructor injection of dependencies
    protected OrderController(EmailService emailService, UserService userService, ModelMapper modelMapper,
                              CartService cartService, OrderService orderService, InventoryService inventoryService,
                              CityMasterService cityMasterService, OrderHistoryService orderHistoryService,
                              HolidayService holidayService) {
//        super(service);
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.cartService = cartService;
        this.orderService = orderService;
        this.inventoryService = inventoryService;
        this.cityMasterService = cityMasterService;
        this.orderHistoryService = orderHistoryService;
        this.holidayService = holidayService;
    }


    @PostMapping("/create/{cartId}")
    @Transactional
    public ResponseEntity<Response<OrderDTO>> createOrderFromCart(@PathVariable("cartId") Long cartId,
                                                                  @RequestParam(value = "userId", required = false) Long userId,
                                                                  @RequestParam(value = "sessionId", required = false) String sessionId,
                                                                  @RequestParam(value = "cityId", required = false) Long cityId,
                                                                  @RequestBody CartCreationDto cartCreationDto) {
        System.out.println("=== Starting createOrderFromCart ===");
        System.out.println("Input parameters - cartId: " + cartId + ", userId: " + userId + ", sessionId: " + sessionId + ", cityId: " + cityId);
        System.out.println("CartCreationDto: " + cartCreationDto);

        // Fetch the cart
        Optional<CartMaster> cartOptional = cartService.findById(cartId);
        if (cartOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<OrderDTO>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("Cart not found")
                            .build());
        }

        CartMaster cart = cartOptional.get();
//        System.out.println("Retrieved cart: " + cart);
        System.out.println("Cart type: " + cart.getCartType());

        // Validate the ownership of the cart
        if (userId != null && !userId.equals(cart.getUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Response.<OrderDTO>builder()
                            .code(HttpStatus.FORBIDDEN.value())
                            .status(Status.ERROR)
                            .message("The cart does not belong to the provided user.")
                            .build());
        }

        String cartType = cartOptional.get().getCartType();

        cart.setCurrency(cartCreationDto.getCurrency());
        cart.setOrderNote(cartCreationDto.getOrderNote());

        if (cart.getDeliveryAddress() == null || cart.getDeliveryAddress().isEmpty()
                || cart.getBillingAddress() == null || cart.getBillingAddress().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<OrderDTO>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("Delivery or Billing address not provided!")
                            .build());
        }

        // Ensure either userId or sessionId is provided
        if (userId == null && sessionId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.<OrderDTO>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .status(Status.ERROR)
                            .message("User ID or Session ID must be provided")
                            .build());
        }

        Optional<User> userOptional = Optional.empty();
        User user = null;
        if (userId != null) {
            userOptional = userService.findById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.<OrderDTO>builder()
                                .code(HttpStatus.NOT_FOUND.value())
                                .status(Status.ERROR)
                                .message("User not found")
                                .build());
            }
            user = userOptional.get();
        }

        City cityMaster = cityMasterService.findById(cityId);
        if (cityMaster == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<OrderDTO>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("City not found.")
                            .build());
        }

        boolean inventoryAvailability = true;
        for (CartItem cartItem : cart.getCartItems()) {
            System.out.println("Checking inventory for product: " + cartItem.getProduct().getId() + ", quantity: " + cartItem.getQuantity());
            boolean isQuantityAvailable = inventoryService.isAvailableForReservation(cartItem.getProduct().getId(), cartItem.getQuantity(), cityMaster.getPlants().get(0).getId());
            System.out.println("Inventory available: " + isQuantityAvailable);
            if (!isQuantityAvailable) {
                System.out.println("Quantity not available for product. Returning error response.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.<OrderDTO>builder()
                                .code(HttpStatus.NOT_FOUND.value())
                                .status(Status.ERROR)
                                .message("Requested quantity is not available in inventory.")
                                .build());
            }
        }

        if (!inventoryAvailability) {
            System.out.println("Not enough inventory available for all items. Returning error response.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.<OrderDTO>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .status(Status.ERROR)
                            .message("Not enough inventory available to freeze for all items")
                            .build());
        }

        System.out.println("Converting cart to order...");
        OrderMaster order = cartService.convertCartToOrder(cart);
        String localContactNumber = cartCreationDto.getLocalContactNumber();
        Date deliveryDate = cartCreationDto.getDeliveryDate();

        // Set local contact number if provided
        if (localContactNumber != null && !localContactNumber.isEmpty()) {
            System.out.println("Setting local contact number: " + localContactNumber);
            order.setLocalContactNumber(localContactNumber);
        }

        // Set delivery date if provided
        if (deliveryDate != null) {
            System.out.println("Setting provided delivery date: " + deliveryDate);
            order.setDeliveryDate(deliveryDate);
        } else {
            System.out.println("No delivery date provided, calculating next available date...");
            // Handle case where delivery date is not provided
            try {
                // Fetch next available dates
                System.out.println("Fetching next available days from holiday service...");
                NextAvailableDaysDTO nextAvailableDaysDTO = holidayService.getNextAvailableDays();

                if ("DELIVERY".equalsIgnoreCase(cart.getDeliveryOption())) {
                    System.out.println("Calculating next delivery date...");
                    deliveryDate = getNextAvailableDate(nextAvailableDaysDTO.getNextDeliveryAvailableDates());
                } else if ("STORE_PICKUP".equalsIgnoreCase(cart.getDeliveryOption())) {
                    System.out.println("Calculating next pickup date...");
                    deliveryDate = getNextAvailableDate(nextAvailableDaysDTO.getNextPickupAvailableDates());
                }
            } catch (Exception e) {
                log.error("Error while fetching next available dates", e);
                System.out.println("Error occurred while fetching next available dates: " + e.getMessage());
            }

            // Set the calculated delivery date
            System.out.println("Setting calculated delivery date: " + deliveryDate);
            order.setDeliveryDate(deliveryDate);
        }

        OrderMaster savedOrder = orderService.save(order);
        System.out.println("Order saved successfully with ID: " + savedOrder.getId());

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        System.out.println("Order mapped to DTO: " + orderDTO);

        ResponseEntity<com.commonlibrary.contract.v1.Response<String>> resulttt = cartService.deleteCart(userId, sessionId, cart.getCartType());

        try {
            this.orderHistoryService.addOrderHistory(savedOrder, "Order Created", "New Order Created");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("=== Completed createOrderFromCart ===");
        return ResponseEntity.ok(
                Response.<OrderDTO>builder()
                        .code(HttpStatus.OK.value())
                        .status(Status.SUCCESS)
                        .data(orderDTO)
                        .message("Order successfully created")
                        .build()
        );
    }

    @GetMapping("/user")
    public ResponseEntity<Response<List<OrderDTO>>> getOrdersByUser(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "sessionId", required = false) String sessionId) {

        // Check if both userId and sessionId are null
        if (userId == null && sessionId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Response.<List<OrderDTO>>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .status(Status.ERROR)
                            .message("Either userId or sessionId must be provided")
                            .build()
            );
        }

        // Retrieve orders based on userId or sessionId
        Response<List<OrderDTO>> response = orderService.getOrdersByUserOrSession(userId, sessionId);

        if (response.getCode() == HttpStatus.OK.value()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(response.getCode()).body(response);
        }
    }

    @GetMapping("/{orderId}/history")
    public ResponseEntity<Response<List<OrderHistoryDTO>>> getOrderHistory(
            @PathVariable("orderId") Long orderId) {

        // Check if both userId and sessionId are null
        if (orderId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Response.<List<OrderHistoryDTO>>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .status(Status.ERROR)
                            .message("Order Id must be provided")
                            .build()
            );
        }

        // Retrieve orders based on userId or sessionId
        Response<List<OrderHistoryDTO>> response = orderService.getOrderHistory(orderId);

        if (response.getCode() == HttpStatus.OK.value()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(response.getCode()).body(response);
        }
    }

    @PostMapping("/convert-order")
    public ResponseEntity<Response<CartDTO>> convertOrderToCart(@RequestParam Long orderId) {
        Response<CartDTO> response = orderService.convertOrderToCart(orderId);

        // Return the appropriate HTTP response based on the response status
        HttpStatus status = response.getStatus() == Status.SUCCESS ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }

    public Date getNextAvailableDate(List<LocalDate> availableDates) {
        LocalDate dayAfterTomorrow = LocalDate.now().plusDays(2); // Calculate the day after tomorrow

        if (availableDates != null && !availableDates.isEmpty()) {
            return availableDates.stream()
                    .filter(date -> date.isAfter(dayAfterTomorrow)) // Ensure the date is in the future
                    .findFirst()
                    .map(localDate -> Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .orElse(null);

        }

        return null;
    }


}