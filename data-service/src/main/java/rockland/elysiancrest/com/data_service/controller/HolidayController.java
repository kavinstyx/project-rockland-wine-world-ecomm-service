package rockland.elysiancrest.com.data_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.config.HolidayDTO;
import rockland.elysiancrest.com.data_service.dto.config.NextAvailableDaysDTO;
import rockland.elysiancrest.com.data_service.service.HolidayService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("api/holiday")
@CrossOrigin
public class HolidayController extends AbstractCrudController<HolidayDTO, Long, HolidayService>{
    protected HolidayController(HolidayService service) {
        super(service);
    }

    @GetMapping("/search")
    @Operation(summary = "Search holidays")
    @ApiResponse(responseCode = "200", description = "Holidays fetched successfully")
    public ResponseEntity<Page<HolidayDTO>> searchHolidays(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(name = "holidayType", required = false) String holidayType,
            @RequestParam(name = "pickupBlock", required = false) Boolean pickupBlock,
            @RequestParam(name = "deliveryBlock", required = false) Boolean deliveryBlock
    ) {
        try {
            Page<HolidayDTO> holidays = service.searchHolidays(startDate, endDate, holidayType, pickupBlock, deliveryBlock, page, size);
            return ResponseEntity.ok(holidays);
        } catch (Exception e) {
            log.error("Error fetching holidays", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/types")
    @Operation(summary = "Get holiday types")
    @ApiResponse(responseCode = "200", description = "Holiday types fetched successfully")
    public ResponseEntity<List<String>> getHolidayTypes() {
        try {
            List<String> holidayTypes = Arrays.asList("Public Holiday", "Bank Holiday", "Religious Holiday", "National Holiday");
            return ResponseEntity.ok(holidayTypes);
        } catch (Exception e) {
            log.error("Error fetching holiday types", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/next-available-days")
    @Operation(summary = "Get next available days")
    @ApiResponse(responseCode = "200", description = "Available types fetched successfully")
    public ResponseEntity<NextAvailableDaysDTO> getNextAvailableDays() {
        NextAvailableDaysDTO nextAvailableDays = service.getNextAvailableDays();

        return ResponseEntity.ok(nextAvailableDays);

    }
}
