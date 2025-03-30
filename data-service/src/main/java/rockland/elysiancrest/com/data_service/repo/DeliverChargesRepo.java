package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import rockland.elysiancrest.com.data_service.entity.master_data.DeliveryChargesMaster;

public interface DeliverChargesRepo extends JpaRepository<DeliveryChargesMaster, Long> {

    DeliveryChargesMaster findByCityMasterId(Long cityId);
}
