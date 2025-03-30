package rockland.elysiancrest.com.data_service.service;

import rockland.elysiancrest.com.data_service.dto.InventoryFreezeDTO;
import rockland.elysiancrest.com.data_service.entity.cart.InventoryFreezeMaster;

public interface InventoryFreezeService extends BaseService<InventoryFreezeMaster, InventoryFreezeDTO>{
    InventoryFreezeDTO convertToDto(InventoryFreezeMaster inventoryFreezeMaster);
    InventoryFreezeMaster convertToEntity(InventoryFreezeDTO freezeDTO);
}
