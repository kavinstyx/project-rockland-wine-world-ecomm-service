package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.cart.addon.CartAddon;

import java.util.List;

@Repository
public interface CartAddonRepository extends JpaRepository<CartAddon, Long> {
    List<CartAddon> findAllByCartId(Long id);


}
