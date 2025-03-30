package rockland.elysiancrest.com.data_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.Response;
import rockland.elysiancrest.com.data_service.dto.dashboard.OrderDataDTO;
import rockland.elysiancrest.com.data_service.service.DashboardService;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("api/dashboard")
@CrossOrigin
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/orders")
    public Response<OrderDataDTO> getOrderData(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        return dashboardService.getOrderData(startDate, endDate);
    }
    
}
