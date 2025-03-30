package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.cart.addon.Addon;

@Repository
public interface AddonRepo extends JpaRepository<Addon, Long> {

}
