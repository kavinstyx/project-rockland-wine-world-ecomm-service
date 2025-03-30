package rockland.elysiancrest.com.data_service.service.impl;

import com.commonlibrary.contract.v1.Category;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.dto.CategoryMasterDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.CategoryMaster;
import rockland.elysiancrest.com.data_service.repo.CategoryMasterRepo;
import rockland.elysiancrest.com.data_service.service.CategoryMasterService;

@Service
@Transactional
public class CategoryMasterServiceImpl extends CrudServiceImpl<CategoryMaster,Long, CategoryMasterRepo, Category> implements CategoryMasterService {


    public CategoryMasterServiceImpl(CategoryMasterRepo repository, ModelMapper modelMapper) {
        super(repository,modelMapper);

    }

}
