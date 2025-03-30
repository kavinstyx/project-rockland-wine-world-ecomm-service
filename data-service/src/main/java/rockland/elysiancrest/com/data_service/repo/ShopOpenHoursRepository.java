package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.config.ShopOpenHours;

@Repository
public interface ShopOpenHoursRepository  extends JpaRepository<ShopOpenHours, Long> {
}
