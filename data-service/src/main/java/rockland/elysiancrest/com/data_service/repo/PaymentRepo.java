package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.payment.PaymentRequest;

import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<PaymentRequest, Long> {
    Optional<PaymentRequest> findByOrderId(Long orderId);
}
