package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.inventory.Inventory;

import java.util.Optional;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Long> {

//    Inventory findByProductId(Long productId);
    Optional<Inventory> findByProductId(Long productId);

    Optional<Inventory> findByProductIdAndPlantMasterId(Long id, Long plantMasterId);
}
