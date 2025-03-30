package rockland.elysiancrest.com.data_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import rockland.elysiancrest.com.data_service.util.AuthenticatedUserUtil;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class AuditConfig {

    private final AuthenticatedUserUtil authenticatedUserUtil;

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(authenticatedUserUtil.getCurrentUsername()); // Replace with actual user retrieval logic
    }
}