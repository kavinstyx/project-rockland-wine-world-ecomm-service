package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rockland.elysiancrest.com.data_service.entity.SubscribeEmail;

public interface SubscriberEmailRepo extends JpaRepository<SubscribeEmail, Long> {
    Page<SubscribeEmail> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}
