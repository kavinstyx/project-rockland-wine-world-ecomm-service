package rockland.elysiancrest.com.data_service.service.impl;


import com.commonlibrary.contract.v1.Brand;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.dto.BrandMasterDTO;
import rockland.elysiancrest.com.data_service.dto.ProductMasterDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.BrandMaster;
import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;
import rockland.elysiancrest.com.data_service.repo.BrandMasterRepo;
import rockland.elysiancrest.com.data_service.repo.ProductMasterRepo;
import rockland.elysiancrest.com.data_service.service.BrandMasterService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class BrandMasterServiceImpl extends CrudServiceImpl<BrandMaster,Long, BrandMasterRepo, Brand> implements BrandMasterService {

    private final ProductMasterRepo productMasterRepo;

    public BrandMasterServiceImpl(BrandMasterRepo repository, ModelMapper modelMapper, ProductMasterRepo productMasterRepo) {
        super(repository, modelMapper);
        this.productMasterRepo = productMasterRepo;

    }

    @Override
    public List<String> getBrandNames(Long departmentId, Long categoryId) {
        return productMasterRepo.findBrandNamesByDepartmentAndCategory(departmentId, categoryId);
    }

    @Override
    public List<String> getAllBrandNames() {
        return productMasterRepo.findAllBrandNames();
    }


}
