package rockland.elysiancrest.com.data_service.service;

import rockland.elysiancrest.com.data_service.dto.OrderHistoryDTO;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;

import java.util.List;

public interface OrderHistoryService {

     List<OrderHistoryDTO> orderHistoryList(Long orderId);
     void addOrderHistory(Long orderId, Long userId, String username,String userRole, String action, String orderStatus, String message);
     void addOrderHistory(OrderMaster order, String action, String message);
}
