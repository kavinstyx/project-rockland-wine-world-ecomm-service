package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rockland.elysiancrest.com.data_service.entity.master_data.ProductMaster;
//import rockland.elysiancrest.com.data_service.service.ProductFilterService;

public interface ProductFilterRepo extends JpaRepository<ProductMaster, Long>, JpaSpecificationExecutor<ProductMaster> {
}
