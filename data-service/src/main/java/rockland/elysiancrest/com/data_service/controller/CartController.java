package rockland.elysiancrest.com.data_service.controller;

import com.commonlibrary.contract.v1.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.CartAddonDTO;
import rockland.elysiancrest.com.data_service.dto.CartDTO;
import rockland.elysiancrest.com.data_service.dto.CartItemDTO;
import rockland.elysiancrest.com.data_service.dto.DeliveryChargeResponse;
import rockland.elysiancrest.com.data_service.entity.cart.CartMaster;
import rockland.elysiancrest.com.data_service.entity.cart.CartItem;
import rockland.elysiancrest.com.data_service.entity.cart.UpdateCartAddressRequest;
import rockland.elysiancrest.com.data_service.entity.cart.addon.CartSubmitRequest;
import rockland.elysiancrest.com.data_service.entity.cart.gift.GiftDetailsRequest;
import rockland.elysiancrest.com.data_service.entity.master_data.DeliveryChargesMaster;
import rockland.elysiancrest.com.data_service.repo.CartRepository;
import rockland.elysiancrest.com.data_service.service.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.logging.Logger;

@Slf4j
@RestController
@RequestMapping("/api/carts")
@CrossOrigin
public class CartController extends AbstractController {

    private final CartService cartService;
    private final CartItemService cartItemService;
    private final InventoryService inventoryService;
    private final UserService userService;
    private final CartRepository cartRepository;
    private final DeliveryChargeMasterService deliveryChargeMasterService;
    private final Logger logger = Logger.getLogger("msg-data-service");
    private final ModelMapper modelMapper;

    public CartController(CartService cartService, CartItemService cartItemService,
                          InventoryService inventoryService,
                          UserService userService, CartRepository cartRepository,
                          DeliveryChargeMasterService deliveryChargeMasterService,
                          ModelMapper modelMapper) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
        this.inventoryService = inventoryService;
        this.userService = userService;
        this.cartRepository = cartRepository;
        this.deliveryChargeMasterService = deliveryChargeMasterService;
        logger.info("CartController initialized");
        this.modelMapper = modelMapper;
    }

    @PostMapping("/item")
    @Transactional
    public ResponseEntity<Response<CartDTO>> addCartItem(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "cartType") String cartType,
            @RequestParam(value = "cityId") Long cityId,
            @RequestBody CartItemDTO cartItemDTO) {

        return cartService.addCartItem(userId, sessionId, cartType, cityId, cartItemDTO);
    }


    @GetMapping("/get")
    public ResponseEntity<Response<CartDTO>> getCartById(
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "cartType", required = false) String cartType) {

        if (userId != null && userId == -1) {
            userId = null;
        }
        if (sessionId == null && userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.<CartDTO>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .status(Status.ERROR)
                            .message("Either 'id', 'sessionId', or 'userId' must be provided.")
                            .build());
        }

        CartMaster cartMaster = getCartByUserIdOrSessionIdAndCartType(userId, sessionId, cartType);
        if (cartMaster == null) {
            // Return empty cart response
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<CartDTO>builder()
                            .code(HttpStatus.OK.value())
                            .status(Status.SUCCESS)
                            .data(new CartDTO())
                            .message("Cart not found.")
                            .build());
        }

        // ISHAN
//        // Ensure the cart belongs to the provided user or session
//        if (userId != null && !userId.equals(cartMaster.getUser().getId())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Response.<CartDTO>builder()
//                            .code(HttpStatus.FORBIDDEN.value())
//                            .status(Status.ERROR)
//                            .message("The cart does not belong to the provided user.")
//                            .build());
//        }

        // Convert CartMaster to CartDTO
        CartDTO cartDTO = cartService.convertToDto(cartMaster);

        // MANUALLY UPDATING CAUSE MAPPING DOESNT WORK
        cartDTO.setDeliveryAddress(cartMaster.getDeliveryAddress());
        cartDTO.setBillingAddress(cartMaster.getBillingAddress());
        cartDTO.setDeliveryOption(cartMaster.getDeliveryOption());

        // Update bottle totals in rupee and dollar
        BigDecimal bottleTotalInRupee = cartMaster.getCartItems().stream()
                .map(item -> item.getTotalPriceInRupee().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal bottleTotalInDollar = cartMaster.getCartItems().stream()
                .map(item -> item.getTotalPriceInDollar().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cartDTO.setBottleTotalInRupee(bottleTotalInRupee);
        cartDTO.setBottleTotalInDollar(bottleTotalInDollar);

        return ResponseEntity.ok(
                Response.<CartDTO>builder()
                        .code(HttpStatus.OK.value())
                        .status(Status.SUCCESS)
                        .data(cartDTO)
                        .message("Cart retrieved successfully.")
                        .build()
        );
    }


    // CART ITEM QUANTITY CHANGE
    @PutMapping("/item/{itemId}")
    @Transactional
    public ResponseEntity<Response<CartItemDTO>> updateCartItem(
            @PathVariable("itemId") Long itemId,
            @RequestBody CartItemDTO cartItemDTO,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam("cartType") String cartType,
            @RequestParam("cityId") Long cityId) {

        logger.info("Updating cart item. Item ID: " + itemId + ", Cart Type: " + cartType);
        System.out.println("Deleting cart item. Item ID: " + itemId + ", Cart Type: " + cartType + ", userId: " + userId);

        // Fetch the cart by userId or sessionId and cartType
        CartMaster cartMaster = getCartByUserIdOrSessionIdAndCartType(userId, sessionId, cartType);
        if (cartMaster == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<CartItemDTO>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("Cart not found.")
                            .build());
        }

        // Ensure the cart belongs to the provided user or session
        if (userId != null && !userId.equals(cartMaster.getUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Response.<CartItemDTO>builder()
                            .code(HttpStatus.FORBIDDEN.value())
                            .status(Status.ERROR)
                            .message("The cart does not belong to the provided user.")
                            .build());
        }

        // Check if the cart item exists
        Optional<CartItem> optionalCartItem = cartItemService.findById(itemId);
        if (optionalCartItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<CartItemDTO>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("Cart item not found.")
                            .build());
        }

        DeliveryChargesMaster deliveryChargesMaster = deliveryChargeMasterService.findByCityId(cityId);
        if (deliveryChargesMaster == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<CartItemDTO>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("Delivery charge not found for the product.")
                            .build());
        }

        // Update the cart's userId if necessary
        if (userId != null && cartMaster.getSessionId() != null && cartMaster.getUser() == null) {
            cartMaster.setUser(userService.findById(userId).orElse(null)); // Set the user
            cartMaster.setSessionId(null); // Clear sessionId for logged-in user
            cartMaster = cartService.save(cartMaster); // Save updated cart
        }

        CartItem cartItem = optionalCartItem.get();

        int newQuantity = cartItemDTO.getQuantity();

        // Check inventory availability
        boolean isQuantityAvailable = inventoryService.isAvailableForReservation(cartItem.getProduct().getId(),
                newQuantity, cartMaster.getCityMaster().getPlants().get(0).getId());
        if (!isQuantityAvailable) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Response.<CartItemDTO>builder()
                            .code(HttpStatus.CONFLICT.value())
                            .status(Status.ERROR)
                            .message("Requested quantity is not available in inventory.")
                            .build());
        }

        // Update cart item with new quantity
        cartItem.setQuantity(newQuantity);
        cartItem.setTotalPriceInRupee(cartItem.getProduct().getRegularPriceInRupee().multiply(BigDecimal.valueOf(newQuantity)));
        cartItem.setTotalPriceInDollar(cartItem.getProduct().getRegularPriceInDollar().multiply(BigDecimal.valueOf(newQuantity)));

        // Save the updated cart item
        CartItem updatedCartItem = cartItemService.update(cartItem.getId(), cartItem);

        // Update the total price of the cart
        cartMaster.updateTotalPriceInRupee(); // Assuming CartMaster has updateTotalPriceInRupee()
        cartMaster.updateTotalPriceInDollar(); // Assuming CartMaster has updateTotalPriceInDollar()

        cartMaster.setAddonsChargesInRupee();
        cartMaster.setAddonsChargesInDollar();

        // Calculate the total bottles in the cart and set the delivery charge
        int totalBottles = cartMaster.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        DeliveryChargeResponse deliveryChargeResponse = deliveryChargeMasterService.calculateDeliveryCharge(totalBottles, cityId);
        BigDecimal deliveryChargeInRupee = deliveryChargeResponse.getTotalDeliveryChargeInRupee();
        BigDecimal deliveryChargeInDollar = deliveryChargeResponse.getTotalDeliveryChargeInDollar();

        cartMaster.setDeliveryChargesInRupee(deliveryChargeInRupee);
        cartMaster.setDeliveryChargesInDollar(deliveryChargeInDollar);

        // Save the updated cart with the new total price and delivery charge
        cartService.save(cartMaster);

        // Return the updated cart item as response
        return ResponseEntity.ok(Response.<CartItemDTO>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .data(cartItemService.convertToDto(updatedCartItem))
                .message("Cart item updated successfully")
                .build());
    }


    private CartMaster getCartByUserIdOrSessionIdAndCartType(Long userId, String sessionId, String cartType) {
        CartMaster cartMaster = null;

        System.out.println(userId + cartType + sessionId);
        if (userId != null) {
            cartMaster = cartService.getCart(userId, cartType);
            if (cartMaster == null) {
                System.out.println("1 NULL" + userId);

            }
        }
        if (cartMaster == null & sessionId != null) {
            cartMaster = cartRepository.findBySessionIdAndCartType(sessionId, cartType);
            if (cartMaster == null) {
                System.out.println("2 NULL" + userId);

            }
        }

        return cartMaster;
    }


    //DELETE CART
    @DeleteMapping("/item/{itemId}")
    @Transactional
    public ResponseEntity<Response<String>> deleteCartItem(
            @PathVariable("itemId") Long itemId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam("cartType") String cartType,
            @RequestParam("cityId") Long cityId) {

        logger.info("Deleting cart item. Item ID: " + itemId + ", Cart Type: " + cartType);
        System.out.println("Deleting cart item. Item ID: " + itemId + ", Cart Type: " + cartType + ", userId: " + userId);

        // Retrieve the active cart based on userId or sessionId and cartType
        CartMaster cartMaster = getCartByUserIdOrSessionIdAndCartType(userId, sessionId, cartType);
        if (cartMaster == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<String>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("Cart not found.")
                            .build());
        }

        // Ensure the cart belongs to the provided user or session
        if (userId != null && !userId.equals(cartMaster.getUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Response.<String>builder()
                            .code(HttpStatus.FORBIDDEN.value())
                            .status(Status.ERROR)
                            .message("The cart does not belong to the provided user.")
                            .build());
        }

        // Verify that the cart item exists and belongs to the specified cart
        CartItem cartItem = cartItemService.findById(itemId).orElse(null);
        if (cartItem == null || !cartItem.getCart().getId().equals(cartMaster.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<String>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("Cart item not found or does not belong to the specified cart.")
                            .build());
        }

        // Release the inventory freeze associated with this cart item
//        inventoryService.releaseInventoryFreeze(cartItem.getProduct().getId(), cartItem.getQuantity(), cartMaster.getId());

        // Delete the cart item and update the cart's total price
        cartItemService.deleteById(itemId);
        cartMaster.getCartItems().removeIf(cartItem1 -> cartItem1.getId().equals(itemId));
        cartMaster.updateTotalPriceInRupee();
        cartMaster.updateTotalPriceInDollar();

        cartMaster.setAddonsChargesInRupee();
        cartMaster.setAddonsChargesInDollar();

        // Calculate the total bottles in the cart after deletion and set the delivery charge
        int totalBottles = cartMaster.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        DeliveryChargesMaster deliveryChargesMaster = deliveryChargeMasterService.findByCityId(cityId);
        if (deliveryChargesMaster == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<String>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("Delivery charge not found for the product.")
                            .build());
        }

        DeliveryChargeResponse deliveryChargeResponse = deliveryChargeMasterService.calculateDeliveryCharge(totalBottles, cityId);
        BigDecimal deliveryChargeInRupee = deliveryChargeResponse.getTotalDeliveryChargeInRupee();
        BigDecimal deliveryChargeInDollar = deliveryChargeResponse.getTotalDeliveryChargeInDollar();

        if (!cartMaster.getCartItems().isEmpty()) {
            cartMaster.setDeliveryChargesInRupee(deliveryChargeInRupee);
            cartMaster.setDeliveryChargesInDollar(deliveryChargeInDollar);

        } else {
            cartMaster.setDeliveryChargesInRupee(BigDecimal.valueOf(0));
            cartMaster.setDeliveryChargesInDollar(BigDecimal.valueOf(0));
        }

        // Save the updated cart with new total price and delivery charge
        cartService.save(cartMaster);

        String successMessage = "Cart item with ID " + itemId + " successfully deleted from cart.";
        return ResponseEntity.ok(
                Response.<String>builder()
                        .code(HttpStatus.OK.value())
                        .status(Status.SUCCESS)
                        .message(successMessage)
                        .data(successMessage)
                        .build());
    }


    @DeleteMapping
    public ResponseEntity<Response<String>> deleteCart(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam("cartType") String cartType) {

        Response<String> response = cartService.deleteCart(userId, sessionId, cartType).getBody();

        if (response.getCode() == HttpStatus.OK.value()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(response.getCode()).body(response);
        }
    }

    @GetMapping("/item-count")
    public ResponseEntity<Response<Integer>> getCartItemCount(@RequestParam("userId") Long userId, String cartType) {
        int itemCount = cartService.getCartItemCount(userId, cartType);
        return ResponseEntity.ok(
                Response.<Integer>builder()
                        .code(HttpStatus.OK.value())
                        .status(Status.SUCCESS)
                        .data(itemCount)
                        .message("Cart item count retrieved successfully.")
                        .build()
        );
    }

    @PutMapping("/{cartId}/update-addresses")
    public ResponseEntity<Response<CartDTO>> updateCartAddresses(
            @PathVariable Long cartId,
            @RequestBody UpdateCartAddressRequest request) {

        // Fetch the cart by ID
        Optional<CartMaster> cartOptional = cartService.findById(cartId);
        if (cartOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<CartDTO>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("Cart not found for ID: " + cartId)
                            .build());
        }

        CartMaster cart = cartOptional.get();

        // Update billing and delivery addresses
        if (request.getBillingAddress() != null) {
            cart.setBillingAddress(request.getBillingAddress());
        }
        if (request.getDeliveryAddress() != null) {
            cart.setDeliveryAddress(request.getDeliveryAddress());
        }

        // Save the updated cart
        cartService.save(cart);

        // Convert cart to DTO for response
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        return ResponseEntity.ok(
                Response.<CartDTO>builder()
                        .code(HttpStatus.OK.value())
                        .status(Status.SUCCESS)
                        .data(cartDTO)
                        .message("Addresses updated successfully")
                        .build());
    }

    @PutMapping("/{cartId}/update-delivery-option")
    public ResponseEntity<Response<CartDTO>> updateDeliveryOption(
            @PathVariable Long cartId,
            @RequestParam("deliveryOption") String deliveryOption,
            @RequestParam("cityId") Long cityId) {

        // Fetch the cart by ID
        Optional<CartMaster> cartOptional = cartService.findById(cartId);
        if (cartOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<CartDTO>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("Cart not found for ID: " + cartId)
                            .build());
        }

        CartMaster cart = cartOptional.get();

        if ("STORE_PICKUP".equalsIgnoreCase(deliveryOption)) {
            // Set delivery charge to 0 for store pickup
            cart.setDeliveryChargesInDollar(BigDecimal.ZERO);
            cart.setDeliveryChargesInRupee(BigDecimal.ZERO);
            cart.setDeliveryAddress(null);  // Store pickup doesn't require a delivery address
            cart.setDeliveryOption("STORE_PICKUP");
        } else if ("DELIVERY".equalsIgnoreCase(deliveryOption)) {
            // Calculate the bottle quantity from the cart items
            int bottleQuantity = cart.getCartItems().stream()
                    .mapToInt(CartItem::getQuantity)  // Assuming CartItem has a 'quantity' field
                    .sum();

            // Calculate delivery charge based on cityId and bottle quantity
            DeliveryChargeResponse deliveryChargeResponse = deliveryChargeMasterService.calculateDeliveryCharge(bottleQuantity, cityId);
            BigDecimal deliveryChargeInRupee = deliveryChargeResponse.getTotalDeliveryChargeInRupee();
            BigDecimal deliveryChargeInDollar = deliveryChargeResponse.getTotalDeliveryChargeInDollar();

//            if (deliveryCharge == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(Response.<CartDTO>builder()
//                                .code(HttpStatus.NOT_FOUND.value())
//                                .status(Status.ERROR)
//                                .message("Delivery charges not found for city ID: " + cityId)
//                                .build());
//            }

            cart.setDeliveryChargesInRupee(deliveryChargeInRupee);
            cart.setDeliveryChargesInDollar(deliveryChargeInDollar);

            cart.setDeliveryOption("DELIVERY");
//            cart.setCityId(cityId);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Response.<CartDTO>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .status(Status.ERROR)
                            .message("Invalid delivery option. Allowed values: STORE_PICKUP, DELIVERY")
                            .build());
        }

        // Save the updated cart
        cartService.save(cart);

        // Convert cart to DTO for response
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        return ResponseEntity.ok(
                Response.<CartDTO>builder()
                        .code(HttpStatus.OK.value())
                        .status(Status.SUCCESS)
                        .data(cartDTO)
                        .message("Delivery option updated successfully")
                        .build());
    }


    @PostMapping("/{cartId}/submitAddons")
    @Transactional
    public ResponseEntity<Response<CartAddonDTO>> submitAddons(
            @PathVariable Long cartId,
            @RequestBody CartSubmitRequest cartSubmitRequest) {

        return cartService.submitAddons(cartId, cartSubmitRequest);
    }


    @PostMapping("/{cartId}/updateGiftDetails")
    @Transactional
    public ResponseEntity<Response<CartDTO>> updateGiftDetails(
            @PathVariable Long cartId,
            @RequestBody GiftDetailsRequest giftDetailsRequest) {

        Optional<CartMaster> cartOptional = cartService.findById(cartId);

        // Check if the cart exists
        if (cartOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<CartDTO>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("Cart not found")
                            .build());
        }

        CartMaster cartMaster = cartOptional.get();

        // Update the gift details
        cartMaster.setGifterName(giftDetailsRequest.getGifterName());
        cartMaster.setGifteeName(giftDetailsRequest.getGifteeName());
        cartMaster.setGifteeContactNumber(giftDetailsRequest.getGifteeContactNumber());
        cartMaster.setGifterMessage(giftDetailsRequest.getGifterMessage());
        cartMaster.setIsGift(giftDetailsRequest.getIsGift());

        // Save the updated cart
        cartService.save(cartMaster);

        return ResponseEntity.ok(
                Response.<CartDTO>builder()
                        .code(HttpStatus.OK.value())
                        .status(Status.SUCCESS)
                        .message("Gift details updated successfully")
                        .data(modelMapper.map(cartMaster, CartDTO.class))
                        .build());
    }

    @PostMapping("/item/special-instructions")
    @Transactional
    public ResponseEntity<Response<CartDTO>> updateSpecialInstructions(
            @RequestParam(value = "cartId", required = false) Long cartId,
            @RequestParam(value = "specialInstructions", required = false) String specialInstructions) {

        return cartService.updateSpecialInstructions(cartId, specialInstructions);
    }
}