package com.lawrence.daddio.TeamTrack.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Beans for signing and verifying JWTs with a shared HS256 secret (symmetric key), plus the
 * {@link AuthenticationManager} used by the login endpoint. The same key signs tokens
 * ({@link JwtEncoder}) and verifies them ({@link JwtDecoder}).
 */
@Configuration
public class JwtConfig {

    /** The signing key, decoded from the Base64 {@code security.jwt.secret-key} property (needs at least 256 bits). */
    @Bean
    public SecretKey jwtSecretKey(@Value("${security.jwt.secret-key}") String secret) {
        return new SecretKeySpec(Base64.getDecoder().decode(secret), "HmacSHA256");
    }

    /** Creates signed tokens; used by {@code JwtService} at login. */
    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }

    /**
     * Verifies the signature and expiry of incoming tokens. Picked up automatically by the resource-server
     * filter in {@code SecurityConfig}. Restricted to HS256 so tokens signed with any other algorithm are rejected.
     */
    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
        return NimbusJwtDecoder.withSecretKey(jwtSecretKey).macAlgorithm(MacAlgorithm.HS256).build();
    }

    /**
     * Exposes Spring Boot's auto-configured manager (a DaoAuthenticationProvider backed by
     * {@code EmployeeUserDetailsService} and the {@code PasswordEncoder} bean) so the login endpoint can inject it.
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
