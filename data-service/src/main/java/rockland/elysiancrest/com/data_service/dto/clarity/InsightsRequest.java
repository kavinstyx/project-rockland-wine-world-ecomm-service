package rockland.elysiancrest.com.data_service.dto.clarity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InsightsRequest {
    private List<ClarityDateRange> dateRanges;
    private List<ClarityDimension> dimensions;
    private List<ClarityMetric> metrics;
}

