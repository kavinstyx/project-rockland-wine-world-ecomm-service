package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.cart.CartMaster;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartMaster, Long> {

    boolean existsByUserIdAndCartType(Long userId, String cartType);

    Page<CartMaster> findByUserIdAndCartType(Long userId, String cartType, Pageable pageable);

    Optional<CartMaster> findByUserIdAndId(Long userId, Long cartId);

    Optional<CartMaster> findByUserIdOrSessionIdAndCartType(Long actualUserId, String actualSessionId, String cartType);

    CartMaster findByUserIdAndCartType(Long userId, String cartType);

    CartMaster findBySessionIdAndCartType(String sessionId, String cartType);

    CartMaster findBySessionId(String sessionId);

    List<CartMaster> findAllByUserIdAndCartType(Long userId, String cartType);
}