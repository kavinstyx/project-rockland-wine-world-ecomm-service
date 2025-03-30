package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.cart.InventoryFreezeMaster;

import java.util.List;

@Repository
public interface InventoryFreezeRepo extends JpaRepository<InventoryFreezeMaster, Long> {

    List<InventoryFreezeMaster> findByProductIdAndOrderIdAndReleasedIsFalse(Long productId, Long orderId);
}
