package com.lawrence.daddio.TeamTrack.service;

import com.lawrence.daddio.TeamTrack.config.JwtConfig;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "mkawYCBflntJ6LpTsoFwuEo2PTo7FSrv6JrsUI9SMB0=";
    private static final UserDetails USER = User.withUsername("ann@x.com").password("pw").roles("USER").build();

    private JwtService serviceWithExpiration(long expirationMs) {
        JwtConfig config = new JwtConfig();
        SecretKey key = config.jwtSecretKey(SECRET);
        JwtEncoder encoder = config.jwtEncoder(key);
        JwtDecoder decoder = config.jwtDecoder(key);
        return new JwtService(encoder, decoder, expirationMs);
    }

    @Test
    void generatedTokenRoundTripsToUsername() {
        JwtService service = serviceWithExpiration(60_000);

        String token = service.generateToken(Map.of("role", "admin"), USER);

        assertThat(service.extractUsername(token)).isEqualTo("ann@x.com");
    }

    @Test
    void expiredTokenIsRejected() {
        JwtConfig config = new JwtConfig();
        SecretKey key = config.jwtSecretKey(SECRET);
        JwtEncoder encoder = config.jwtEncoder(key);
        JwtService service = new JwtService(encoder, config.jwtDecoder(key), 60_000);

        Instant issued = Instant.now().minus(Duration.ofMinutes(10));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject("ann@x.com")
                .issuedAt(issued)
                .expiresAt(issued.plus(Duration.ofMinutes(5)))
                .build();
        String token = encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();

        assertThatThrownBy(() -> service.extractUsername(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtService service = serviceWithExpiration(60_000);

        String token = service.generateToken(USER);
        String tampered = token.substring(0, token.length() - 2) + (token.endsWith("AA") ? "BB" : "AA");

        assertThatThrownBy(() -> service.extractUsername(tampered)).isInstanceOf(JwtException.class);
    }
}
