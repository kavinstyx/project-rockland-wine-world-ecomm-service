package rockland.elysiancrest.com.data_service.service.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import rockland.elysiancrest.com.data_service.dto.CartAddonDTO;
import rockland.elysiancrest.com.data_service.entity.cart.addon.CartAddon;
import rockland.elysiancrest.com.data_service.service.CartAddonService;

@Service
public class CartAddonServiceImpl extends BaseServiceImpl<CartAddon, CartAddonDTO> implements CartAddonService {

    public CartAddonServiceImpl(JpaRepository<CartAddon, Long> repository) {
        super(repository);
    }

}
