package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.quotation.QuotationMaster;

@Repository
public interface QuotationMasterRepo extends JpaRepository<QuotationMaster, Long> {

}
