package rockland.elysiancrest.com.data_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rockland.elysiancrest.com.data_service.dto.clarity.ClarityDateRange;
import rockland.elysiancrest.com.data_service.dto.clarity.ClarityDimension;
import rockland.elysiancrest.com.data_service.dto.clarity.ClarityMetric;
import rockland.elysiancrest.com.data_service.dto.clarity.InsightsRequest;
import rockland.elysiancrest.com.data_service.service.ClarityInsightsService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/clarity")
@CrossOrigin
public class ClarityInsightsController {
    private static final Logger logger = LoggerFactory.getLogger(ClarityInsightsController.class);

    private final ClarityInsightsService clarityInsightsService;

    @Autowired
    public ClarityInsightsController(ClarityInsightsService clarityInsightsService) {
        this.clarityInsightsService = clarityInsightsService;
    }

    @GetMapping("/project-live-insights")
    public ResponseEntity<?> getProjectLiveInsights(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        try {
            logger.info("Received request for insights from {} to {}", startDate, endDate);
            
            InsightsRequest request = buildRequest(startDate, endDate);
            ResponseEntity<?> response = clarityInsightsService.getLiveInsights(request);
            
            return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());
                    
        } catch (Exception e) {
            logger.error("Error processing insights request: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    private InsightsRequest buildRequest(LocalDate startDate, LocalDate endDate) {
        return InsightsRequest.builder()
                .dateRanges(List.of(new ClarityDateRange(startDate, endDate)))
                .dimensions(List.of(new ClarityDimension("country")))
                .metrics(List.of(new ClarityMetric("activeUsers")))
                .build();
    }
} 