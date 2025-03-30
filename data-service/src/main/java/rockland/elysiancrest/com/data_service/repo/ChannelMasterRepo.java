package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.master_data.ChannelMaster;

import java.util.List;

@Repository
public interface ChannelMasterRepo extends JpaRepository<ChannelMaster, Long> {

}
