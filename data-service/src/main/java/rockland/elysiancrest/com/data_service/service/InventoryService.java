package rockland.elysiancrest.com.data_service.service;

import rockland.elysiancrest.com.data_service.dto.InventoryDTO;
import rockland.elysiancrest.com.data_service.entity.inventory.Inventory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public interface InventoryService extends BaseService<Inventory, InventoryDTO> {
    InventoryDTO convertToDto(Inventory inventory);
    Inventory convertToEntity(InventoryDTO inventoryDTO);

    boolean reserveQuantity(Long productId, int quantity, Long plantMasterId);
    void releaseReservedQuantity(Long productId, int quantity, Long plantMasterId);
    void finalizePurchase(Long productId, int quantity, Long plantMasterId);

    Optional<Inventory> findByProductId(Long id);

    void createInventoryFreeze(Long productId, int quantity, Long userId, Long orderId, Long plantMasterId);

    void releaseInventoryFreeze(Long id, int i, Long cartId);

    Optional<Inventory> findByProductIdAndPlantMasterId(Long id, Long cityId);

    boolean isAvailableForReservation(Long productId, int requestedQuantity, Long plantMasterId);

    void processInventoryFiles() throws IOException, SQLException;
}
