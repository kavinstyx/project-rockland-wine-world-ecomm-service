package rockland.elysiancrest.com.data_service.service.impl;


import com.commonlibrary.contract.v1.City;
import com.commonlibrary.contract.v1.Product;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.entity.master_data.CityMaster;
import rockland.elysiancrest.com.data_service.repo.CityMasterRepo;
import rockland.elysiancrest.com.data_service.service.CityMasterService;

@Service
@Transactional
public class CityMasterServiceImpl extends CrudServiceImpl<CityMaster, Long, CityMasterRepo, City> implements CityMasterService {
    public CityMasterServiceImpl(CityMasterRepo repository, ModelMapper modelMapper) {

        super(repository, modelMapper);

    }

    @Override
    public Page<City> findAll(PageRequest pageRequest) {
        Page<CityMaster> entityPage = repository.findAllByDeliveryEnabled(pageRequest,true);

        return entityPage.map(this::convertToDto);
    }

    @Override
    public Page<City> findAllCities(PageRequest pageRequest) {
        return super.findAll(pageRequest);
    }

    @Override
    protected City convertToDto(CityMaster entity){
        City dto = super.convertToDto(entity);

        //set channel id manually
        if(entity.getChannelMaster() != null){
            dto.setChannelId(entity.getChannelMaster().getId());
        }

        return dto;
    }


//    @Override
//    public City convertToDto(CityMaster cityMaster) {
//        return modelMapper.map(cityMaster, City.class);
//    }
//
//    @Override
//    public CityMaster convertToEntity(City dto) {
//        return modelMapper.map(dto, CityMaster.class);
//    }
}
