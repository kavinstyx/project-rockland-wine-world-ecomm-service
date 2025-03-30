package rockland.elysiancrest.com.data_service.service.impl;

import com.commonlibrary.contract.v1.Department;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rockland.elysiancrest.com.data_service.dto.DepartmentMasterDTO;
import rockland.elysiancrest.com.data_service.entity.master_data.DepartmentMaster;
import rockland.elysiancrest.com.data_service.repo.DepartmentMasterRepo;
import rockland.elysiancrest.com.data_service.service.DepartmentMasterService;

@Service
@Transactional
public class DepartmentMasterImpl extends CrudServiceImpl<DepartmentMaster,Long, DepartmentMasterRepo, Department> implements DepartmentMasterService {

    public DepartmentMasterImpl(DepartmentMasterRepo repository, ModelMapper modelMapper) {
        super(repository, modelMapper);

    }
}
