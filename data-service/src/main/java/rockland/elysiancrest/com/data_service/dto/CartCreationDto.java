package rockland.elysiancrest.com.data_service.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CartCreationDto {
    private String currency;
    private String localContactNumber;
    private Date deliveryDate;
    private String orderNote;
}
