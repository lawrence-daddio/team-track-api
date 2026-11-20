package com.lawrence.daddio.TeamTrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * HTTP security rules. The API is a stateless OAuth2 resource server: clients log in at
 * {@code POST /auth/login}, receive a JWT, and send it as {@code Authorization: Bearer <token>}.
 * Authorization (roles) is not enforced yet; any authenticated employee can call any endpoint.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Public: the login endpoint (no token exists yet) and the dev-only H2 console.
                        .requestMatchers("/h2-console/**", "/auth/login").permitAll()
                        .anyRequest().authenticated())
                // CSRF protects cookie/session auth; bearer tokens in a header are not sent automatically
                // by browsers, so it is safe to disable for this stateless API.
                .csrf(AbstractHttpConfigurer::disable)
                // The H2 console renders in frames, so allow same-origin framing.
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                // No HTTP session: every request must carry its own token.
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Reads the Bearer token and validates signature and expiry using the JwtDecoder bean
                // (see JwtConfig). An invalid or missing token results in 401.
                .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
