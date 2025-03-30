package rockland.elysiancrest.com.data_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import rockland.elysiancrest.com.data_service.dto.OrderDTO;
import rockland.elysiancrest.com.data_service.dto.OrderSummeryDTO;

import java.time.LocalDateTime;

public interface OrderCrudService extends CrudService<OrderDTO, Long>{
    Page<OrderSummeryDTO> searchOrders(String orderStatus, String searchString, Long cityId, Long userId,
                                       LocalDateTime startDate, LocalDateTime endDate, String eventOrderType,
                                       PageRequest pageRequest);

    void updateOrderStatus(Long orderId, String newStatus);

    void updateOrderNote(Long orderId, String note);
}
