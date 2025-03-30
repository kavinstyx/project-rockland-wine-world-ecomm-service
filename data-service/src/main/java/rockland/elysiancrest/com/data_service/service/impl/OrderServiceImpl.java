package rockland.elysiancrest.com.data_service.service.impl;

import com.commonlibrary.contract.v1.City;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.dto.*;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.entity.cart.CartItem;
import rockland.elysiancrest.com.data_service.entity.cart.CartMaster;
import rockland.elysiancrest.com.data_service.entity.cart.addon.AddonSelection;
import rockland.elysiancrest.com.data_service.entity.cart.addon.CartSubmitRequest;
import rockland.elysiancrest.com.data_service.entity.master_data.CityMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderAddon;
import rockland.elysiancrest.com.data_service.entity.order.OrderItemMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;
import rockland.elysiancrest.com.data_service.repo.OrderRepo;
import rockland.elysiancrest.com.data_service.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl extends BaseServiceImpl<OrderMaster, OrderDTO> implements OrderService {

    private final OrderRepo orderRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final CartService cartService;
    private final CityMasterService cityMasterService;
    private final OrderHistoryService orderHistoryService;

    public OrderServiceImpl(OrderRepo orderRepository, ModelMapper modelMapper, UserService userService, CartService cartService, CityMasterService cityMasterService, OrderHistoryService orderHistoryService) {
        super(orderRepository);
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.cartService = cartService;
        this.cityMasterService = cityMasterService;
        this.orderHistoryService = orderHistoryService;
    }

    @Override
    @Transactional
    public Response<CartDTO> convertOrderToCart(Long orderId) {
        // Fetch the order by ID
        Optional<OrderMaster> orderMasterOpt = orderRepository.findById(orderId);
        if (orderMasterOpt.isEmpty()) {
            return Response.<CartDTO>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(Status.ERROR)
                    .message("Order not found!")
                    .build();
        }

        OrderMaster order = orderMasterOpt.get();

        if (order.getCartType() != null && !order.getCartType().equals("normal")) {
            return Response.<CartDTO>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(Status.ERROR)
                    .message("Wedding/Cooperate not supported!")
                    .build();
        }

        // Validate the user
        Optional<User> userOpt = userService.findById(order.getUser().getId());
        if (userOpt.isEmpty()) {
            return Response.<CartDTO>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(Status.ERROR)
                    .message("User not found!")
                    .build();
        }

        User user = userOpt.get();

        // Check for an existing cart of the same type
        CartMaster currentCart = cartService.getCart(user.getId(), order.getCartType());

        // Validate city
        City city = cityMasterService.findById(order.getCityId());
        if (city == null) {
            return Response.<CartDTO>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(Status.ERROR)
                    .message("City not found!")
                    .build();
        }

        int newCartId = 0;
        for (OrderItemMaster orderItem : order.getOrderItems()) {
            CartItemDTO cartItemDTO = new CartItemDTO();
            cartItemDTO.setItemId(orderItem.getProduct().getId());
            cartItemDTO.setQuantity(orderItem.getQuantity());

            // Add the item to the cart
            newCartId = Math.toIntExact(cartService.addCartItem(user.getId(), null, order.getCartType(), city.getId(),
                    cartItemDTO).getBody().getData().getId());
        }

        CartSubmitRequest cartSubmitRequest = new CartSubmitRequest();
        List<AddonSelection> addonSelections = new ArrayList<>();

        for (OrderAddon orderAddon : order.getOrderAddons()) {
            AddonSelection addonSelection = new AddonSelection();
            addonSelection.setAddonId(orderAddon.getAddon().getId());
            addonSelection.setQuantity(orderAddon.getQuantity());
            addonSelections.add(addonSelection);
        }

        cartSubmitRequest.setAddons(addonSelections);
        System.out.println(cartService.submitAddons((long) newCartId, cartSubmitRequest).getBody().getMessage());

        return Response.<CartDTO>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .message("Order successfully converted to cart!")
                .data(modelMapper.map(currentCart, CartDTO.class))
                .build();
    }


    @Override
    public Response<List<OrderDTO>> getOrdersByUserOrSession(Long userId, String sessionId) {
        List<OrderMaster> orders;

        if (userId != null) {
            // Fetch orders for registered user by userId
            orders = orderRepository.findByUserId(userId);
        } else if (sessionId != null) {
            // Fetch orders for guest user by sessionId
            orders = orderRepository.findBySessionId(sessionId);
        } else {
            return Response.<List<OrderDTO>>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .status(Status.ERROR)
                    .message("Either userId or sessionId must be provided")
                    .build();
        }

        if (orders.isEmpty()) {
            return Response.<List<OrderDTO>>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(Status.ERROR)
                    .message("No orders found for the given criteria")
                    .build();
        }


        List<OrderDTO> orderDTOs = orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());


        return Response.<List<OrderDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .data(orderDTOs)
                .message("Orders retrieved successfully")
                .build();
    }

    @Override
    public Response<List<OrderHistoryDTO>> getOrderHistory(Long orderId) {
        if (orderId == null) {
            return Response.<List<OrderHistoryDTO>>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .status(Status.ERROR)
                    .message("Order ID cannot be null")
                    .build();
        }

        List<OrderHistoryDTO> orderHistoryDTOS = orderHistoryService.orderHistoryList(orderId);
        
        if (orderHistoryDTOS.isEmpty()) {
            return Response.<List<OrderHistoryDTO>>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(Status.ERROR)
                    .message("No order history found for the given order ID")
                    .build();
        }

        return Response.<List<OrderHistoryDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(Status.SUCCESS)
                .data(orderHistoryDTOS)
                .message("Order history retrieved successfully")
                .build();
    }

}
