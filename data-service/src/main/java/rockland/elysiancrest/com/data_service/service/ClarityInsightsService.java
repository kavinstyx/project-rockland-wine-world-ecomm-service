package rockland.elysiancrest.com.data_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rockland.elysiancrest.com.data_service.dto.clarity.ClarityDateRange;
import rockland.elysiancrest.com.data_service.dto.clarity.ClarityDimension;
import rockland.elysiancrest.com.data_service.dto.clarity.ClarityMetric;
import rockland.elysiancrest.com.data_service.dto.clarity.InsightsRequest;
import rockland.elysiancrest.com.data_service.entity.ClarityInsightsCache;
import rockland.elysiancrest.com.data_service.repository.ClarityInsightsCacheRepository;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClarityInsightsService {
    private static final Logger logger = LoggerFactory.getLogger(ClarityInsightsService.class);
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ClarityInsightsCacheRepository cacheRepository;

    @Value("${clarity.api.url}")
    private String clarityApiUrl;

    @Value("${clarity.api.token}")
    private String authToken;

    public ClarityInsightsService(ObjectMapper objectMapper, ClarityInsightsCacheRepository cacheRepository) {
        this.objectMapper = objectMapper;
        this.cacheRepository = cacheRepository;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @SuppressWarnings("unchecked")
    public ResponseEntity<?> getLiveInsights(InsightsRequest request) {
        try {
            // Try to get cached response first
            return cacheRepository.findLatestCache()
                    .map(cache -> {
                        try {
                            Object responseBody = objectMapper.readValue(cache.getResponseData(), Object.class);
                            return ResponseEntity.ok(responseBody);
                        } catch (Exception e) {
                            logger.error("Error parsing cached response: {}", e.getMessage(), e);
                            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Error processing cached data");
                        }
                    })
                    .orElseGet(() -> fetchAndCacheInsights(request));
        } catch (Exception e) {
            logger.error("Error processing request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    @Scheduled(
        initialDelay = 2 * 60 * 60 * 1000, // 2 hours initial delay
        fixedRate = 6 * 60 * 60 * 1000     // Run every 6 hours after that
    )
    public void scheduledFetch() {
        try {
            // Create request for last 30 days
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(30);
            
            InsightsRequest request = InsightsRequest.builder()
                    .dateRanges(List.of(new ClarityDateRange(startDate.toLocalDate(), endDate.toLocalDate())))
                    .dimensions(List.of(new ClarityDimension("country")))
                    .metrics(List.of(new ClarityMetric("activeUsers")))
                    .build();

            fetchAndCacheInsights(request);
        } catch (Exception e) {
            logger.error("Scheduled fetch failed: {}", e.getMessage(), e);
        }
    }

    private ResponseEntity<Object> fetchAndCacheInsights(InsightsRequest request) {
        try {
            String requestBody = objectMapper.writeValueAsString(request);
            String encodedBody = URLEncoder.encode(requestBody, StandardCharsets.UTF_8);
            String urlWithParams = clarityApiUrl + "?request=" + encodedBody;
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(urlWithParams))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + authToken)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();

            logger.info("Making request to Clarity API: {}", urlWithParams);
            
            HttpResponse<String> response = httpClient.send(httpRequest, 
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                // Cache the response
                ClarityInsightsCache cache = ClarityInsightsCache.builder()
                        .responseData(response.body())
                        .fetchTime(LocalDateTime.now())
                        .startDate(request.getDateRanges().get(0).getStartDate().atStartOfDay())
                        .endDate(request.getDateRanges().get(0).getEndDate().atStartOfDay())
                        .build();
                
                cacheRepository.save(cache);
                
                Object responseBody = response.body().isEmpty() ? 
                        null : objectMapper.readValue(response.body(), Object.class);
                return ResponseEntity.ok(responseBody);
            } else {
                logger.error("API returned error status: {} with body: {}", 
                        response.statusCode(), response.body());
                return ResponseEntity.status(response.statusCode()).body(response.body());
            }
        } catch (Exception e) {
            logger.error("Error fetching insights: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching insights: " + e.getMessage());
        }
    }
}