package rockland.elysiancrest.com.data_service.dto.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HolidayDTO {
    private Long id;
    private Date date;
    private String holidayType;
    private Boolean pickupBlock;
    private Boolean deliveryBlock;
    private String description;
}
