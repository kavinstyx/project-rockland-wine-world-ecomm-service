package rockland.elysiancrest.com.data_service.service.impl;

import com.commonlibrary.contract.v1.Plant;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.dto.PlantMasterDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.PlantMaster;
import rockland.elysiancrest.com.data_service.repo.PlantMasterRepo;
import rockland.elysiancrest.com.data_service.service.PlantMasterService;

@Service
@Transactional
public class PlantMasterServiceImpl extends CrudServiceImpl<PlantMaster, Long, PlantMasterRepo, Plant> implements PlantMasterService {


    public PlantMasterServiceImpl(PlantMasterRepo repository, ModelMapper modelMapper) {
        super(repository, modelMapper);

    }

    @Override
    protected Plant convertToDto(PlantMaster entity){
        Plant dto = super.convertToDto(entity);

        //set city id manually
        if (entity.getCity() != null){
            dto.setCityId(entity.getCity().getId());
        }

        return dto;
    }


}
