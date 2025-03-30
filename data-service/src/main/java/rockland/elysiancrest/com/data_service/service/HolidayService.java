package rockland.elysiancrest.com.data_service.service;

import org.springframework.data.domain.Page;
import rockland.elysiancrest.com.data_service.dto.config.HolidayDTO;
import rockland.elysiancrest.com.data_service.dto.config.NextAvailableDaysDTO;

import java.util.Date;

public interface HolidayService extends CrudService<HolidayDTO, Long> {
    Page<HolidayDTO> searchHolidays(Date startDate, Date endDate, String holidayType,
                                    Boolean pickupBlock, Boolean deliveryBlock, int page, int size) ;

    NextAvailableDaysDTO getNextAvailableDays();
}
