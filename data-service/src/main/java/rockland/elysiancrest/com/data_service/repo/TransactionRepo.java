package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.payment.Transaction;

import java.util.Optional;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByOrderId(Long orderId);
}
