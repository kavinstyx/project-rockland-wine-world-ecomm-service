package rockland.elysiancrest.com.data_service.service;

import com.commonlibrary.contract.v1.Brand;
import rockland.elysiancrest.com.data_service.dto.BrandMasterDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.BrandMaster;

import java.util.List;
import java.util.Map;

public interface BrandMasterService extends CrudService<Brand, Long>{
    List<String> getBrandNames(Long departmentId, Long categoryId);
    List<String> getAllBrandNames();
//    BrandMasterDTO convertToDto(BrandMaster brandMaster);
//    BrandMaster convertToEntity(BrandMasterDTO dto);
}
