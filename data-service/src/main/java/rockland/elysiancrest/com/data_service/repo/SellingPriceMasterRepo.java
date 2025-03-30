package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.master_data.SellingPriceMaster;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SellingPriceMasterRepo extends JpaRepository<SellingPriceMaster, Long> {

    @Query("SELECT s FROM SellingPriceMaster s " +
            "WHERE s.sapMaterialCode = :sapMaterialCode " +
            "AND s.unitOfMeasure = :unitOfMeasure " +
            "AND s.startDate = :startDate " +
            "AND (:endDate IS NULL OR s.endDate = :endDate)")
    Optional<SellingPriceMaster> findByKeyAttributes(
            @Param("sapMaterialCode") String sapMaterialCode,
            @Param("unitOfMeasure") String unitOfMeasure,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<SellingPriceMaster> findByIsActiveTrue();
}
