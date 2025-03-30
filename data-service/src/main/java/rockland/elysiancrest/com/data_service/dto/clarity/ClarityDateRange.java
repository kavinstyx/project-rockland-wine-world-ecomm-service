package rockland.elysiancrest.com.data_service.dto.clarity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ClarityDateRange {
    private LocalDate startDate;
    private LocalDate endDate;
}
