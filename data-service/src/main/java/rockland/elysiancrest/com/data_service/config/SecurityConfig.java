package rockland.elysiancrest.com.data_service.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rockland.elysiancrest.com.data_service.authFilter.JwtAuthenticationFilter;
import rockland.elysiancrest.com.data_service.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${jwt.auth.enabled}")
    private boolean jwtAuthEnabled; // This will read the value of jwt.auth.enabled from application.properties

    @Value("${sap.integration.auth.token}")
    private String authToken;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CustomUserDetailsService customUserDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        if (jwtAuthEnabled) {
            // If JWT authentication is enabled, apply security with JWT authentication
            http.authorizeHttpRequests(auth -> auth
                            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Allow Swagger UI access
                            .requestMatchers("/api/carts/**").authenticated() // Secure cart-related endpoints
                            .requestMatchers("/api/favorites/**").authenticated()
                            .requestMatchers("/api/orders/**").authenticated()
                            .requestMatchers("/api/complaints/**").authenticated()
                            .requestMatchers("/api/payment/**").authenticated()
                            .requestMatchers("/api/profile/**").authenticated()
                            .requestMatchers("/api/sap-integration/**").access(customAuthorizationManager()) // SAP Integration
                            .requestMatchers("/api/quotations/**").access(customAuthorizationManager()) // SAP Integration
                            .requestMatchers(HttpMethod.GET).permitAll() // Allow all GET requests for other endpoints
                            .anyRequest().authenticated() // Secure all non-GET requests().permitAll() // Allow all other requests
                    )
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        } else {
            // If JWT authentication is disabled, allow all requests
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/sap-integration/**").access(customAuthorizationManager())
                    .anyRequest().permitAll() // Allow all requests
            );
        }

        http.exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint()) // Custom unauthorized response
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/api/users/login?logout") // Redirect to login after logout
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthorizationManager<RequestAuthorizationContext> customAuthorizationManager() {
        return (authentication, context) -> {
            HttpServletRequest request = context.getRequest();
            String requestToken = request.getHeader("Authorization");
            boolean isAuthorized = requestToken != null && requestToken.equals("Bearer " + authToken);
            return new AuthorizationDecision(isAuthorized);
        };
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid or missing token");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}

