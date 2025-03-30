package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.master_data.BrandMaster;

import java.util.List;

@Repository
public interface BrandMasterRepo extends JpaRepository<BrandMaster, Long> {
    List<BrandMaster> findByBrandNameIn(List<String> brandName);
}
