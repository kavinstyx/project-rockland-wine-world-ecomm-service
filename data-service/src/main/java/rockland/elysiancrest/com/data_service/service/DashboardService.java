package rockland.elysiancrest.com.data_service.service;

import rockland.elysiancrest.com.data_service.dto.Response;
import rockland.elysiancrest.com.data_service.dto.dashboard.OrderDataDTO;

import java.time.LocalDateTime;

public interface DashboardService {
    Response<OrderDataDTO> getOrderData(LocalDateTime startDate, LocalDateTime endDate);
}
