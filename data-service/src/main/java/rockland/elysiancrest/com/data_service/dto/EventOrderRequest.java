package rockland.elysiancrest.com.data_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventOrderRequest {
    private String name;
    private String email;
    private String phone;
    private LocalDateTime date;
    private String message;
    private String eventOrderType;
    private List<EventOrderRequestItem> items;



}
