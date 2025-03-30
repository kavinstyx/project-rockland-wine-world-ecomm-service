package rockland.elysiancrest.com.data_service.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rockland.elysiancrest.com.data_service.dto.OrderHistoryDTO;
import rockland.elysiancrest.com.data_service.entity.User;
import rockland.elysiancrest.com.data_service.entity.order.OrderHistory;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;
import rockland.elysiancrest.com.data_service.repo.OrderHistoryRepo;
import rockland.elysiancrest.com.data_service.service.OrderHistoryService;
import rockland.elysiancrest.com.data_service.util.AuthenticatedUserUtil;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OrderHistoryServiceImpl implements OrderHistoryService {
    private final OrderHistoryRepo orderHistoryRepo;
    private final AuthenticatedUserUtil authenticatedUserUtil;

    public OrderHistoryServiceImpl(OrderHistoryRepo orderHistoryRepo, AuthenticatedUserUtil authenticatedUserUtil) {
        this.orderHistoryRepo = orderHistoryRepo;
        this.authenticatedUserUtil = authenticatedUserUtil;
    }

    @Override
    public List<OrderHistoryDTO> orderHistoryList(Long orderId) {
        List<OrderHistory> histories = orderHistoryRepo.findAllByOrderId(orderId);
        return histories.stream()
                .map(history -> {
                    OrderHistoryDTO dto = new OrderHistoryDTO();
                    dto.setId(history.getId());
                    dto.setOrderId(history.getOrderId());
                    dto.setUserId(history.getUserId());
                    dto.setUsername(history.getUsername());
                    dto.setUserRole(history.getUserRole());
                    dto.setAction(history.getAction());
                    dto.setStatus(history.getStatus());
                    dto.setMessage(history.getMessage());
                    dto.setCreatedOn(history.getCreatedOn());
                    return dto;
                })
                .toList();
    }

    @Override
    public void addOrderHistory(Long orderId, Long userId, String username,String userRole, String action, String orderStatus, String message) {

        OrderHistory history = new OrderHistory();
        history.setOrderId(orderId);
        history.setUserId(userId);
        history.setUsername(username);
        history.setUserRole(userRole);
        history.setAction(action);
        history.setStatus(orderStatus);
        history.setMessage(message);
        history.setCreatedOn(LocalDateTime.now());
        orderHistoryRepo.save(history);
    }

    @Override
    public void addOrderHistory(OrderMaster order, String action, String message) {
        try {
            User currentUser = authenticatedUserUtil.getCurrentUser();
            if (currentUser == null) {
                this.addOrderHistory(order.getId(), -1L, "Unknown", "Unknown", action, String.valueOf(order.getStatus()), message);
            } else {
                this.addOrderHistory(order.getId(), currentUser.getId(), currentUser.getUsername(),currentUser.getUserRole(), action, String.valueOf(order.getStatus()), message);
            }
        }catch (Exception e){
            log.error("Error in creating booking history",e);
        }

    }
}
