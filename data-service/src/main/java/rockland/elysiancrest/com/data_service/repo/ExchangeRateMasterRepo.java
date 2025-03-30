package rockland.elysiancrest.com.data_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rockland.elysiancrest.com.data_service.entity.master_data.ExchangeRateMaster;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ExchangeRateMasterRepo extends JpaRepository<ExchangeRateMaster, Long> {
    @Query("SELECT e FROM ExchangeRateMaster e WHERE e.exchangeRateType = :exchangeRateType AND e.fromCurrency = :fromCurrency AND e.toCurrency = :toCurrency AND e.startDate = :startDate")
    Optional<ExchangeRateMaster> findByKeyAttributes(
            @Param("exchangeRateType") String exchangeRateType,
            @Param("fromCurrency") String fromCurrency,
            @Param("toCurrency") String toCurrency,
            @Param("startDate") LocalDate startDate);

    Optional<ExchangeRateMaster> findTopByFromCurrencyAndToCurrencyOrderByStartDateDesc(
            String fromCurrency, String toCurrency);
}
