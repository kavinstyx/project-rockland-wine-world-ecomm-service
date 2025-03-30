package rockland.elysiancrest.com.data_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderHistoryDTO {
    private Long id;
    private Long orderId;
    private Long userId;
    private String username;
    private String userRole;
    private String action;
    private String status;
    private String message;
    private LocalDateTime createdOn;
}
