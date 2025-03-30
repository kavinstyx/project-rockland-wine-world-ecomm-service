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
import rockland.elysiancrest.com.data_service.dto.dashboard.SalesTargetDTO;
import rockland.elysiancrest.com.data_service.entity.config.Holiday;
import rockland.elysiancrest.com.data_service.entity.sales.SalesTarget;
import rockland.elysiancrest.com.data_service.repo.HolidayRepository;
import rockland.elysiancrest.com.data_service.repo.SalesTargetRepo;
import rockland.elysiancrest.com.data_service.service.HolidayService;
import rockland.elysiancrest.com.data_service.service.SalesTargetService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SalesTargetServiceImpl extends CrudServiceImpl<SalesTarget, Long, SalesTargetRepo, SalesTargetDTO> implements SalesTargetService {


    public SalesTargetServiceImpl(SalesTargetRepo repository, ModelMapper modelMapper) {
        super(repository, modelMapper);
    }
}
