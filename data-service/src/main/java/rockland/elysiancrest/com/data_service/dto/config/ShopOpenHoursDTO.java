package rockland.elysiancrest.com.data_service.dto.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopOpenHoursDTO {

    private Long id;
    private Integer dayOfWeek;
    private String dayName;
    private LocalTime openingTime;
    private LocalTime closingTime;
}
