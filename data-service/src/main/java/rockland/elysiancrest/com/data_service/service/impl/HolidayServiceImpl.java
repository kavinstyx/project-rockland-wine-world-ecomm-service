package rockland.elysiancrest.com.data_service.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.dto.config.HolidayDTO;
import rockland.elysiancrest.com.data_service.dto.config.NextAvailableDaysDTO;
import rockland.elysiancrest.com.data_service.entity.config.Holiday;
import rockland.elysiancrest.com.data_service.repo.HolidayRepository;
import rockland.elysiancrest.com.data_service.service.HolidayService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class HolidayServiceImpl extends CrudServiceImpl<Holiday, Long, HolidayRepository, HolidayDTO> implements HolidayService {

    private final HolidayRepository holidayRepository;

    public HolidayServiceImpl(HolidayRepository repository, ModelMapper modelMapper) {
        super(repository, modelMapper);
        this.holidayRepository = repository;
    }

    public Page<HolidayDTO> searchHolidays(Date startDate, Date endDate, String holidayType, 
            Boolean pickupBlock, Boolean deliveryBlock, int page, int size) {
        
        Specification<Holiday> spec = Specification.where(null);
        
        if (startDate != null && endDate != null) {
            spec = spec.and((root, query, cb) -> 
                cb.between(root.get("date"), startDate, endDate));
        }
        
        if (holidayType != null && !holidayType.isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("holidayType"), holidayType));
        }
        
        if (pickupBlock != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("pickupBlock"), pickupBlock));
        }
        
        if (deliveryBlock != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("deliveryBlock"), deliveryBlock));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        Page<Holiday> holidays = holidayRepository.findAll(spec, pageable);
        return holidays.map(holiday -> modelMapper.map(holiday, HolidayDTO.class));
    }

    public NextAvailableDaysDTO getNextAvailableDays() {
        List<LocalDate> nextPickupAvailableDates = new ArrayList<>();
        List<LocalDate> nextDeliveryAvailableDates = new ArrayList<>();
        List<HolidayDTO> holidaysInRange = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();
        
        while (!hasEnoughDates(nextPickupAvailableDates, nextDeliveryAvailableDates)) {
            processDate(currentDate, nextPickupAvailableDates, nextDeliveryAvailableDates, holidaysInRange);
            currentDate = currentDate.plusDays(1);
        }

        return new NextAvailableDaysDTO(
            nextPickupAvailableDates,
            nextDeliveryAvailableDates,
            holidaysInRange
        );
    }

    private boolean hasEnoughDates(List<LocalDate> pickupDates, List<LocalDate> deliveryDates) {
        return pickupDates.size() >= 7 && deliveryDates.size() >= 7;
    }

    private void processDate(LocalDate currentDate, 
                           List<LocalDate> pickupDates, 
                           List<LocalDate> deliveryDates,
                           List<HolidayDTO> holidaysInRange) {
        Holiday holiday = findHolidayForDate(currentDate);
        
        if (holiday != null) {
            holidaysInRange.add(modelMapper.map(holiday, HolidayDTO.class));
            if (!Boolean.TRUE.equals(holiday.getPickupBlock()) && pickupDates.size() < 7) {
                pickupDates.add(currentDate);
            }
            if (!Boolean.TRUE.equals(holiday.getDeliveryBlock()) && deliveryDates.size() < 7) {
                deliveryDates.add(currentDate);
            }
        } else {
            if (pickupDates.size() < 7) {
                pickupDates.add(currentDate);
            }
            if (deliveryDates.size() < 7) {
                deliveryDates.add(currentDate);
            }
        }
    }

    private Holiday findHolidayForDate(LocalDate date) {
        Date dateToCheck = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return holidayRepository.findAll((root, query, cb) ->
                cb.equal(cb.function("DATE", Date.class, root.get("date")),
                        cb.function("DATE", Date.class, cb.literal(dateToCheck))))
                .stream()
                .findFirst()
                .orElse(null);
    }


    @Override
    public HolidayDTO create(HolidayDTO dto) {
        // Check if holiday already exists for the given date
        Holiday existingHoliday = holidayRepository.findAll((root, query, cb) ->
                cb.equal(cb.function("DATE", Date.class, root.get("date")), 
                        cb.function("DATE", Date.class, cb.literal(dto.getDate())))).stream().findFirst().orElse(null);
                        
        if (existingHoliday != null) {
            throw new IllegalArgumentException("A holiday already exists for the date: " + dto.getDate());
        }

        Holiday holiday = modelMapper.map(dto, Holiday.class);
        return modelMapper.map(holidayRepository.save(holiday), HolidayDTO.class);
    }

    @Override
    public HolidayDTO update(Long id, HolidayDTO holidayDTO) {
        boolean pickupBlocked = Boolean.TRUE.equals(holidayDTO.getPickupBlock());
        boolean deliveryBlocked = Boolean.TRUE.equals(holidayDTO.getDeliveryBlock());
        
        if (!pickupBlocked && !deliveryBlocked) {
            throw new IllegalArgumentException(
                "At least one of pickupBlock or deliveryBlock must be true. " +
                "If you want to remove all blocks, please delete the holiday record instead.");
        }
        return super.update(id, holidayDTO);
    }
}
