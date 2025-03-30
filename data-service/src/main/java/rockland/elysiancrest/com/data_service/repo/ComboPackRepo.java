package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.ComboPack;

@Repository
public interface ComboPackRepo extends JpaRepository<ComboPack, Long> {
}
