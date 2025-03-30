package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import rockland.elysiancrest.com.data_service.entity.cart.CartItem;

import java.util.List;

public interface CartItemRepository  extends JpaRepository<CartItem, Long> {

    List<CartItem> findAllByCartId(Long id);
}
