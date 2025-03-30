package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.ComplaintLog;

import java.util.List;

@Repository
public interface ComplaintLogRepo extends JpaRepository<ComplaintLog, Long> {
    List<ComplaintLog> findByComplaintIdOrderByCreatedAtDesc(Long complaintId);
} 