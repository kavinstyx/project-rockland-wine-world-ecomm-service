package rockland.elysiancrest.com.data_service.service;

import com.commonlibrary.contract.v1.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import rockland.elysiancrest.com.data_service.entity.master_data.CityMaster;

public interface CityMasterService extends CrudService<City, Long> {
//    City convertToDto(CityMaster cityMaster);
//    CityMaster convertToEntity(City dto);

    Page<City> findAllCities(PageRequest pageRequest);

}
