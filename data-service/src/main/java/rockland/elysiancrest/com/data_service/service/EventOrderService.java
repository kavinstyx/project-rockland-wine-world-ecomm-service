package rockland.elysiancrest.com.data_service.service;

import rockland.elysiancrest.com.data_service.dto.EventOrderRequest;

public interface EventOrderService {
    Long processAndSaveOrder(EventOrderRequest eventOrderRequest);

}
