package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.order.OrderItemMaster;
import rockland.elysiancrest.com.data_service.entity.order.OrderMaster;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemMasterRepository extends JpaRepository<OrderItemMaster, Long>, JpaSpecificationExecutor<OrderItemMaster> {
    Optional<List<OrderItemMaster>> findByOrder(OrderMaster order);

    @Query("SELECT COUNT(o) FROM OrderItemMaster o WHERE o.order = :order")
    Integer findCountByOrder(OrderMaster order);
}
