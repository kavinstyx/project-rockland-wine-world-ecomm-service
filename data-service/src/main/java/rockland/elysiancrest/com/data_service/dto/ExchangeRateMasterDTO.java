package rockland.elysiancrest.com.data_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateMasterDTO {

    private Long id;
    private Double exRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String currency;
}
