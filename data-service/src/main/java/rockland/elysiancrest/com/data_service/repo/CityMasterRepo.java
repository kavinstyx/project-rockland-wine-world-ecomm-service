package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.master_data.CityMaster;

import java.util.Optional;

@Repository
public interface CityMasterRepo extends JpaRepository<CityMaster, Long> {

    Page<CityMaster> findAllByDeliveryEnabled(PageRequest pageRequest, boolean deliveryEnabled);

    Optional<CityMaster> findById(Long cityId);

}
