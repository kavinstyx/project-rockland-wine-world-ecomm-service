package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rockland.elysiancrest.com.data_service.entity.sales.SalesTarget;

import java.util.List;

public interface SalesTargetRepo extends JpaRepository<SalesTarget, Long> {
    @Query("SELECT st FROM SalesTarget st WHERE " +
           "(st.year = :startYear AND st.year = :endYear AND st.month >= :startMonth AND st.month <= :endMonth) OR " +
           "(st.year = :startYear AND st.year < :endYear AND st.month >= :startMonth) OR " +
           "(st.year > :startYear AND st.year < :endYear) OR " +
           "(st.year = :endYear AND st.year > :startYear AND st.month <= :endMonth)")
    List<SalesTarget> findByYearAndMonthBetween(
            Integer startYear,
            Integer startMonth,
            Integer endYear,
            Integer endMonth
    );
}
