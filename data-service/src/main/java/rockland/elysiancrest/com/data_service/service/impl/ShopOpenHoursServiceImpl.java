package rockland.elysiancrest.com.data_service.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.dto.config.ShopOpenHoursDTO;
import rockland.elysiancrest.com.data_service.entity.config.ShopOpenHours;
import rockland.elysiancrest.com.data_service.repo.ShopOpenHoursRepository;
import rockland.elysiancrest.com.data_service.service.ShopOpenHoursService;


@Service
@Transactional

public class ShopOpenHoursServiceImpl extends CrudServiceImpl<ShopOpenHours,Long, ShopOpenHoursRepository, ShopOpenHoursDTO> implements ShopOpenHoursService {

    public ShopOpenHoursServiceImpl(ShopOpenHoursRepository repository, ModelMapper modelMapper) {
        super(repository, modelMapper);
    }
}