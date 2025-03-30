package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.Complaint;

@Repository
public interface ComplaintRepo extends JpaRepository<Complaint, Long> {

}
