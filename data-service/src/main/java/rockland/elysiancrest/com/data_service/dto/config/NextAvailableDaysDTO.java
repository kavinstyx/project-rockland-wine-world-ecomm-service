package rockland.elysiancrest.com.data_service.dto.config;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class NextAvailableDaysDTO {
    private List<LocalDate> nextPickupAvailableDates;
    private List<LocalDate> nextDeliveryAvailableDates;
    private List<HolidayDTO> holidaysInRange;

    public NextAvailableDaysDTO(List<LocalDate> nextPickupAvailableDates, List<LocalDate> nextDeliveryAvailableDates, List<HolidayDTO> holidaysInRange) {
        this.nextPickupAvailableDates = nextPickupAvailableDates;
        this.nextDeliveryAvailableDates = nextDeliveryAvailableDates;
        this.holidaysInRange = holidaysInRange;
    }
}
