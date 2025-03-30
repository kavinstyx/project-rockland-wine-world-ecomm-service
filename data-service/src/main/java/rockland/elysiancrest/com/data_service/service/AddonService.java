package rockland.elysiancrest.com.data_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rockland.elysiancrest.com.data_service.dto.AddonDTO;
import rockland.elysiancrest.com.data_service.dto.CartDTO;
import rockland.elysiancrest.com.data_service.entity.cart.CartMaster;
import rockland.elysiancrest.com.data_service.entity.cart.addon.Addon;

import java.util.List;

public interface AddonService extends BaseService<Addon, AddonDTO>{

    AddonDTO convertToDto(Addon addon);
    Addon convertToEntity(AddonDTO addonDTO);

    List<Addon> findAll();
}
