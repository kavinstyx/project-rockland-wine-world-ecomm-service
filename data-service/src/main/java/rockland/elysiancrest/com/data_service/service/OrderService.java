package rockland.elysiancrest.com.data_service.service;

import rockland.elysiancrest.com.data_service.dto.CartDTO;
import rockland.elysiancrest.com.data_service.dto.OrderDTO;
import rockland.elysiancrest.com.data_service.dto.OrderHistoryDTO;
import rockland.elysiancrest.com.data_service.dto.Response;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;

import java.util.List;

public interface OrderService extends BaseService<OrderMaster, OrderDTO>{

    Response<CartDTO> convertOrderToCart(Long orderId);

    Response<List<OrderDTO>> getOrdersByUserOrSession(Long userId, String sessionId);

    Response<List<OrderHistoryDTO>> getOrderHistory(Long orderId);
}
