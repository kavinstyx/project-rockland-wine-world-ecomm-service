package rockland.elysiancrest.com.data_service.service.impl;

import com.commonlibrary.contract.v1.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import rockland.elysiancrest.com.data_service.dto.CartAddonDTO;
import rockland.elysiancrest.com.data_service.dto.CartDTO;
import rockland.elysiancrest.com.data_service.dto.CartItemDTO;
import rockland.elysiancrest.com.data_service.dto.DeliveryChargeResponse;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.entity.cart.CartMaster;
import rockland.elysiancrest.com.data_service.entity.cart.CartItem;
import rockland.elysiancrest.com.data_service.entity.cart.addon.Addon;
import rockland.elysiancrest.com.data_service.entity.cart.addon.AddonSelection;
import rockland.elysiancrest.com.data_service.entity.cart.addon.CartAddon;
import rockland.elysiancrest.com.data_service.entity.cart.addon.CartSubmitRequest;
import rockland.elysiancrest.com.data_service.entity.inventory.Inventory;
import rockland.elysiancrest.com.data_service.entity.master_data.CityMaster;
import rockland.elysiancrest.com.data_service.entity.master_data.DeliveryChargesMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderAddon;
import rockland.elysiancrest.com.data_service.entity.order.OrderItemMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderStatus;
import rockland.elysiancrest.com.data_service.entity.payment.PaymentStatus;
import rockland.elysiancrest.com.data_service.exception.ResourceNotFoundException;
import rockland.elysiancrest.com.data_service.repo.CartAddonRepository;
import rockland.elysiancrest.com.data_service.repo.CartItemRepository;
import rockland.elysiancrest.com.data_service.repo.CartRepository;
import rockland.elysiancrest.com.data_service.service.*;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartServiceImpl extends BaseServiceImpl<CartMaster, CartDTO> implements CartService {

    private final ModelMapper modelMapper;
    private final CartRepository cartRepository;
    private final InventoryService inventoryService;
    private final CartAddonRepository cartAddonRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductMasterService productMasterService;
    private final CityMasterService cityMasterService;
    private final DeliveryChargeMasterService deliveryChargeMasterService;
    private final UserService userService;
    private final CartItemService cartItemService;
    private final AddonService addonService;
    private final CartAddonService cartAddonService;

    public CartServiceImpl(JpaRepository<CartMaster, Long> repository, ModelMapper modelMapper,
                           CartRepository cartRepository, InventoryService inventoryService,
                           CartAddonRepository cartAddonRepository, CartItemRepository cartItemRepository,
                           ProductMasterService productMasterService, CityMasterService cityMasterService,
                           DeliveryChargeMasterService deliveryChargeMasterService, UserService userService,
                           CartItemService cartItemService, AddonService addonService, CartAddonService cartAddonService) {
        super(repository);
        this.modelMapper = modelMapper;
        this.cartRepository = cartRepository;
        this.inventoryService = inventoryService;
        this.cartAddonRepository = cartAddonRepository;
        this.cartItemRepository = cartItemRepository;
        this.productMasterService = productMasterService;
        this.cityMasterService = cityMasterService;
        this.deliveryChargeMasterService = deliveryChargeMasterService;
        this.userService = userService;
        this.cartItemService = cartItemService;
        this.addonService = addonService;
        this.cartAddonService = cartAddonService;
    }

    @Override
    public CartDTO convertToDto(CartMaster cartMaster) {
        return modelMapper.map(cartMaster, CartDTO.class);
    }

    @Override
    public CartMaster convertToEntity(CartDTO cartDTO) {
        return modelMapper.map(cartDTO, CartMaster.class);
    }

    @Override
    public Page<CartMaster> findByUserIdAndCartType(Long userId, String cartType, Pageable pageable) {
        return cartRepository.findByUserIdAndCartType(userId, cartType, pageable);
    }

    @Override
    public Optional<CartMaster> findByUserIdAndCartId(Long userId, Long cartId) {
        return cartRepository.findByUserIdAndId(userId, cartId);
    }

    @Override
    public CartMaster updateCartTotal(CartMaster cartMaster) {
        // Calculate the total price in rupees
        BigDecimal totalPriceInRupee = cartMaster.getCartItems().stream()
                .map(CartItem::getTotalPriceInRupee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Add delivery charges to the rupee total if applicable
        if (cartMaster.getDeliveryChargesInRupee() != null) {
            totalPriceInRupee = totalPriceInRupee.add(cartMaster.getDeliveryChargesInRupee());
        }
        cartMaster.setTotalPriceInRupee(totalPriceInRupee);

        // Calculate the total price in dollars
        BigDecimal totalPriceInDollar = cartMaster.getCartItems().stream()
                .map(CartItem::getTotalPriceInDollar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Add delivery charges to the dollar total if applicable (assuming a dollar equivalent is available)
        if (cartMaster.getDeliveryChargesInDollar() != null) {
            totalPriceInDollar = totalPriceInDollar.add(cartMaster.getDeliveryChargesInDollar());
        }
        cartMaster.setTotalPriceInDollar(totalPriceInDollar);

        return save(cartMaster);
    }


    @Override
    public OrderMaster convertCartToOrder(CartMaster cart) {
        OrderMaster order = new OrderMaster();

        // Set basic order details
        order.setUser(cart.getUser());
        order.setSessionId(cart.getSessionId());
        order.setTotalPriceInRupee(cart.getTotalPriceInRupee());
        order.setTotalPriceInDollar(cart.getTotalPriceInDollar());
        order.setCurrency(cart.getCurrency());
        order.setDeliveryAddress(cart.getDeliveryAddress());
        order.setBillingAddress(cart.getBillingAddress());
        order.setPaymentMethod("Pay Here");
        order.setCurrency(cart.getCurrency());
        order.setDeliveryFeeInRupee(cart.getDeliveryChargesInRupee());
        order.setDeliveryFeeInDollar(cart.getDeliveryChargesInDollar());
        order.setCartType(cart.getCartType());
        order.setDeliveryOption(cart.getDeliveryOption());
        order.setEventOrderType("online");

        Optional<User> user = userService.findById(cart.getUser().getId());
        order.setEmail(user.map(User::getEmail).orElse(null));

        //set gift details
        order.setIsGift(cart.getIsGift());
        order.setGifterMessage(cart.getGifterMessage());
        order.setGifteeName(cart.getGifteeName());
        order.setGifterName(cart.getGifterName());
        order.setGifteeContactNumber(String.valueOf(cart.getGifteeContactNumber()));

        if (cart.getUser() != null) {
            order.setCustomerName(cart.getUser().getName());
            order.setContactNumbers(cart.getUser().getContactNumbers());
        }else {
            order.setCustomerName("Guest Customer");
        }

        order.setCityId(cart.getCityMaster().getId());
        order.setCityName(cart.getCityMaster().getCityName());
        order.setOrderDate(ZonedDateTime.now(ZoneId.of("Asia/Colombo")).toLocalDateTime());
        order.setStatus(OrderStatus.OPEN);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setAddonTotalInRupee(cart.getAddonTotalInRupee());
        order.setAddonTotalInDollar(cart.getAddonTotalInDollar());
        order.setOrderNote(cart.getOrderNote());

        // Convert CartItems to OrderItems
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItemMaster orderItem = new OrderItemMaster();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setProductName(cartItem.getProduct().getCorrectName());
            orderItem.setSku(cartItem.getProduct().getSku());
            orderItem.setImageUrl(cartItem.getProduct().getImageUrl());
            orderItem.setImageUrl2(cartItem.getProduct().getImageUrl2());
            orderItem.setImageUrl3(cartItem.getProduct().getImageUrl3());
            orderItem.setImageUrl4(cartItem.getProduct().getImageUrl4());
            orderItem.setImageUrl5(cartItem.getProduct().getImageUrl5());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPriceInRupee(cartItem.getProduct().getRegularPriceInRupee());
            orderItem.setUnitPriceInDollar(cartItem.getProduct().getRegularPriceInDollar());
            orderItem.setTotalPriceInRupee(cartItem.getTotalPriceInRupee());
            orderItem.setTotalPriceInDollar(cartItem.getTotalPriceInDollar());
            orderItem.setOrder(order);

            order.getOrderItems().add(orderItem);
        }

        for (CartAddon cartAddon : cart.getCartAddons()) {
            OrderAddon orderAddon = new OrderAddon();
            orderAddon.setAddon(cartAddon.getAddon());
            orderAddon.setAddonName(cartAddon.getAddon().getName());
            orderAddon.setQuantity(cartAddon.getQuantity());
            orderAddon.setTotalPriceInRupee(cartAddon.getTotalPriceInRupee());
            orderAddon.setTotalPriceInDollar(cartAddon.getTotalPriceInDollar());
            orderAddon.setOrder(order);

            order.getOrderAddons().add(orderAddon);
        }

        return order;
    }


    @Override
    public String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public ResponseEntity<Response<String>> deleteCart(Long userId, String sessionId, String cartType) {

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

        try {
            // Release all inventory freezes associated with this cart's items
            if (cartMaster.getCartItems() != null && !cartMaster.getCartItems().isEmpty()) {
                cartMaster.getCartItems().forEach(cartItem -> {
                    if (cartItem.getProduct() != null) {
//                        inventoryService.releaseInventoryFreeze(cartItem.getProduct().getId(),
//                                cartItem.getQuantity(), cartMaster.getId());
                    }
                });
            }

            // Perform the deletion of the cart (and its items via cascading)
            this.deleteById(cartMaster.getId());

            // Success response message
            String successMessage = "Cart with ID " + cartMaster.getId() + " and all associated items were successfully deleted.";
            return ResponseEntity.ok(Response.<String>builder()
                    .code(HttpStatus.OK.value())
                    .status(Status.SUCCESS)
                    .message(successMessage)
                    .data(successMessage)
                    .build());

        } catch (Exception e) {
            // Handle unexpected errors during the deletion process
            e.printStackTrace();
            String errorMessage = "Error occurred while deleting the cart with ID " + cartMaster.getId() + ": " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<String>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .status(Status.ERROR)
                            .message(errorMessage)
                            .build());
        }
    }

    private CartMaster getCartByUserIdOrSessionIdAndCartType(Long userId, String sessionId, String cartType) {
        CartMaster cartMaster = null;

        System.out.println(userId + cartType + sessionId);
        if (userId != null) {
            cartMaster = getCart(userId, cartType);
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

    @Override
    public int getCartItemCount(Long userId, String cartType) {
        CartMaster cart = getCart(userId, cartType);
        if (cart == null) {
            return 0;
        }
        return cart.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    @Override
    public Optional<CartMaster> findBySessionId(String sessionId) {
        return Optional.ofNullable(cartRepository.findBySessionId(sessionId));
    }

    @Override
    public CartMaster getCart(Long userId, String cartType) {
        Optional<CartMaster> cartOptional = getCartOptional(userId, cartType);
        return cartOptional.orElse(null);
    }


    private Optional<CartMaster> getCartOptional(Long userId, String cartType) {
        //Assuming no carts for sessions
        List<CartMaster> allByUserIdAndCartType = cartRepository.findAllByUserIdAndCartType(userId, cartType);

        if(allByUserIdAndCartType.size()>1){
            CartMaster remove = allByUserIdAndCartType.remove(0);
            allByUserIdAndCartType.forEach(cartMaster -> {
                cartAddonRepository.deleteAll(cartAddonRepository.findAllByCartId(cartMaster.getId()));
                cartItemRepository.deleteAll(cartItemRepository.findAllByCartId(cartMaster.getId()));
                cartRepository.deleteById(cartMaster.getId());
            });

            return Optional.of(remove);
        }

        if(allByUserIdAndCartType.isEmpty()){
            return Optional.of(new CartMaster());
        }

        return Optional.ofNullable(allByUserIdAndCartType.get(0));

    }


    @Override
    public ResponseEntity<Response<CartDTO>> addCartItem(Long userId, String sessionId, String cartType, Long cityId, CartItemDTO cartItemDTO) {

        if (userId != null && userId == -1) {
            userId = null;
        }


        if (sessionId == null && userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<CartDTO>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("Either 'sessionId' or 'userId' must be provided.")
                            .build());
        }

        // Fetch the product and ensure it exists
        Product product = productMasterService.findById(cartItemDTO.getItemId());
        if (product == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<CartDTO>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("Product not found")
                            .build());
//            throw new ResourceNotFoundException("Product not found");
        }

        City cityMaster = cityMasterService.findById(cityId);

        if (Objects.equals(cartType, "normal")) {
            if (cityMaster == null) {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.<CartDTO>builder()
                                .code(HttpStatus.NOT_FOUND.value())
                                .status(Status.ERROR)
                                .message("City not found")
                                .build());
//            throw new ResourceNotFoundException("City not found");
            }


            Plant selectedPlant = cityMaster
                    .getPlants()
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (selectedPlant == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.<CartDTO>builder()
                                .code(HttpStatus.NOT_FOUND.value())
                                .status(Status.ERROR)
                                .message("Store not available in the City you selected!")
                                .build());
            }

            Long plantId = selectedPlant.getId();


            // Fetch the inventory associated with the product
            Optional<Inventory> inventoryOpt = inventoryService.findByProductIdAndPlantMasterId(product.getId(), plantId);
            if (inventoryOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.<CartDTO>builder()
                                .code(HttpStatus.NOT_FOUND.value())
                                .status(Status.ERROR)
                                .message("Inventory not found for the product!.")
                                .build());
            }


            boolean isQuantityAvailable = inventoryService.isAvailableForReservation(product.getId(), cartItemDTO.getQuantity(), plantId);
            if (!isQuantityAvailable) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.<CartDTO>builder()
                                .code(HttpStatus.NOT_FOUND.value())
                                .status(Status.ERROR)
                                .message("Requested quantity is not available in inventory.")
                                .build());
            }

            DeliveryChargesMaster deliveryChargesMaster = deliveryChargeMasterService.findByCityId(cityMaster.getId());
            if (deliveryChargesMaster == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.<CartDTO>builder()
                                .code(HttpStatus.NOT_FOUND.value())
                                .status(Status.ERROR)
                                .message("Delivery charge not found for the product.")
                                .build());
            }

        }

        // Check for existing cart based on userId or sessionId
        CartMaster cartMaster = null;

        if (userId != null) {
            // Look for carts by userId and cartType (for authenticated users)
            List<CartMaster> carts = cartRepository.findAllByUserIdAndCartType(userId, cartType);

            if (carts.size() > 1) {
                // Keep the first cart (or decide based on some criteria)
                CartMaster cartToKeep = carts.get(0);

                // Remove the associations (optional cleanup)
                carts.subList(1, carts.size()).forEach(cart -> {
                    cart.getCartItems().clear();
                    cart.getCartAddons().clear();
                    cartRepository.delete(cart);
                });

                cartMaster = cartToKeep;
            } else if (carts.size() == 1) {
                // If exactly one cart exists, use it
                cartMaster = carts.get(0);
            } else {
                // If no cart exists, handle cart creation here (if applicable)
                cartMaster = null;
            }
        }

        if (cartMaster == null && sessionId != null) {
            // Look for cart by sessionId and cartType (for guest users)
            cartMaster = cartRepository.findBySessionIdAndCartType(sessionId, cartType);
        }

        if (cartMaster == null) {
            // No cart found, create a new one
            cartMaster = new CartMaster();
            if (userId != null) {
                // Set the userId if the user is authenticated
                cartMaster.setUser(userService.findById(userId).orElse(null));
            }

            cartMaster.setSessionId(sessionId);
            cartMaster.setCartType(cartType);
            cartMaster.setDeliveryOption("DELIVERY");
            cartMaster.setIsGift(false);

            cartMaster.setCityMaster(modelMapper.map(cityMaster, CityMaster.class));

            cartMaster = this.save(cartMaster);

        }

        // If the cart was created with a sessionId and userId is not null, update the cart
//        if (userId != null && cartMaster.getSessionId() != null && cartMaster.getUser() == null) {
//            // Update the cart with the userId
//            cartMaster.setUser(userService.findById(userId).orElse(null));  // Set the userId
//            cartMaster.setSessionId(null);  // Clear sessionId because the user is now logged in
//            cartMaster = cartService.save(cartMaster);   // Save the updated cart with userId
//        }

        // Check if the item already exists in the cart
        CartItem existingCartItem = cartMaster.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (existingCartItem != null) {
            // If the item exists, update the quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemDTO.getQuantity());
            existingCartItem.setTotalPriceInRupee(product.getRegularPriceInRupee().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            existingCartItem.setTotalPriceInDollar(product.getRegularPriceInDollar().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));

            // Save the updated cart item
            cartItemService.save(existingCartItem);
        } else {
            // If the item doesn't exist, create a new CartItem
            CartItem cartItem = new CartItem();
            cartItem.setCart(cartMaster);
            cartItem.setProduct(productMasterService.convertToEntity(product));
            cartItem.setQuantity(cartItemDTO.getQuantity());
            cartItem.setTotalPriceInRupee(product.getRegularPriceInRupee().multiply(BigDecimal.valueOf(cartItemDTO.getQuantity())));
            cartItem.setTotalPriceInDollar(product.getRegularPriceInDollar().multiply(BigDecimal.valueOf(cartItemDTO.getQuantity())));

            // Save the new cart item
            cartItemService.save(cartItem);

            // Add the new item to the cart
            cartMaster.getCartItems().add(cartItem);
        }

        int totalBottles = cartMaster.getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        if (Objects.equals(cartType, "normal")) {
            DeliveryChargeResponse deliveryChargeResponse = deliveryChargeMasterService.calculateDeliveryCharge(totalBottles, cityMaster.getId());
            BigDecimal deliveryChargeInRupee = deliveryChargeResponse.getTotalDeliveryChargeInRupee();
            BigDecimal deliveryChargeInDollar = deliveryChargeResponse.getTotalDeliveryChargeInDollar();


            cartMaster.setDeliveryChargesInRupee(deliveryChargeInRupee);
            cartMaster.setDeliveryChargesInDollar(deliveryChargeInDollar);

        } else if (Objects.equals(cartType, "cooperate")) {
            DeliveryChargeResponse deliveryChargeResponse = deliveryChargeMasterService.calculateDeliveryCharge(totalBottles, cityMaster.getId());
            BigDecimal deliveryChargeInRupee = deliveryChargeResponse.getTotalDeliveryChargeInRupee();
            BigDecimal deliveryChargeInDollar = deliveryChargeResponse.getTotalDeliveryChargeInDollar();

        } else if (Objects.equals(cartType, "wedding")) {

        }
        // Update the cart total after adding the item
        CartMaster updatedCartMaster = this.updateCartTotal(cartMaster);

        // Convert updated cart to DTO for the response
        CartDTO responseDTO = this.convertToDto(updatedCartMaster);

        return ResponseEntity.ok(Response.<CartDTO>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .data(responseDTO)
                .message("Item added to cart successfully.")
                .build());
    }

    @Override
    public ResponseEntity<Response<CartAddonDTO>> submitAddons(Long cartId, CartSubmitRequest cartSubmitRequest) {

        Optional<CartMaster> cart = this.findById(cartId);
        if (cart.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.<CartAddonDTO>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .status(Status.ERROR)
                            .message("Cart not found")
                            .build());
        }

        CartMaster cartMaster = cart.get();

        // If no addons in the request, delete all existing addons in the cart
        if (cartSubmitRequest.getAddons() == null || cartSubmitRequest.getAddons().isEmpty()) {
            if (cartMaster.getCartAddons() != null && !cartMaster.getCartAddons().isEmpty()) {
                cartMaster.getCartAddons().clear(); // Removes addons from the cart
            }

            cartMaster.updateTotalPriceInRupee();
            cartMaster.updateTotalPriceInDollar();

            cartMaster.setAddonsChargesInRupee();
            cartMaster.setAddonsChargesInDollar();

            this.save(cartMaster); // Save the updated cart without addons

            return ResponseEntity.ok(
                    Response.<CartAddonDTO>builder()
                            .code(HttpStatus.OK.value())
                            .status(Status.SUCCESS)
                            .message("All add-ons removed from the cart")
                            .data(modelMapper.map(cartMaster, CartAddonDTO.class))
                            .build());
        }

        cartMaster.removeAllAddons();

        // Process the add-ons in the request
        for (AddonSelection addonSelection : cartSubmitRequest.getAddons()) {
            Optional<Addon> addon = addonService.findById(addonSelection.getAddonId());

            if (addon.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Response.<CartAddonDTO>builder()
                                .code(HttpStatus.NOT_FOUND.value())
                                .status(Status.ERROR)
                                .message("Addon not found")
                                .build());
            }


            CartAddon cartAddon = new CartAddon();
            cartAddon.setAddon(addon.get());
            cartAddon.setQuantity(addonSelection.getQuantity());
            cartAddon.setAddonName(addon.get().getName());
            cartAddon.setTotalPriceInRupee(cartAddon.calculateTotalPriceInRupee());
            cartAddon.setTotalPriceInDollar(cartAddon.calculateTotalPriceInDollar());

            cartAddon.setCart(cartMaster);

            cartAddonService.save(cartAddon);

            cartMaster.addAddon(cartAddon);
            cartMaster.setAddonsChargesInRupee();
            cartMaster.setAddonsChargesInDollar();
        }

        // Save the updated cart with new add-ons
        this.save(cartMaster);

        return ResponseEntity.ok(
                Response.<CartAddonDTO>builder()
                        .code(HttpStatus.OK.value())
                        .status(Status.SUCCESS)
                        .message("Cart submitted successfully with add-ons")
                        .data(modelMapper.map(cartMaster, CartAddonDTO.class))
                        .build());
    }

    @Override
    public ResponseEntity<Response<CartDTO>> updateSpecialInstructions(
            Long cartId,
            String specialInstructions) {

        // Retrieve the cart item based on cartItemId
        CartMaster cartMaster = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        // Update special instructions
        cartMaster.setOrderNote(specialInstructions);
        cartRepository.save(cartMaster);

        // Return updated cart item details
        CartDTO cartDTO = modelMapper.map(cartMaster, CartDTO.class);
        return ResponseEntity.ok(
                Response.<CartDTO>builder()
                        .code(HttpStatus.OK.value())
                        .status(Status.SUCCESS)
                        .message("Cart submitted successfully with add-ons")
                        .data(cartDTO)
                        .build());
    }



}