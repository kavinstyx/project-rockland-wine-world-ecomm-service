package rockland.elysiancrest.com.data_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rockland.elysiancrest.com.data_service.entity.ClarityInsightsCache;

import java.util.Optional;

public interface ClarityInsightsCacheRepository extends JpaRepository<ClarityInsightsCache, Long> {
    @Query("SELECT c FROM ClarityInsightsCache c WHERE c.fetchTime = (SELECT MAX(cc.fetchTime) FROM ClarityInsightsCache cc)")
    Optional<ClarityInsightsCache> findLatestCache();
} 