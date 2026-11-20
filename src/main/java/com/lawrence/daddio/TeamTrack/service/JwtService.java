package com.lawrence.daddio.TeamTrack.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

/**
 * Issues and reads JWTs. Tokens are signed with HS256; the subject is the employee's email (the username).
 * Incoming requests are validated by the resource-server filter in {@code SecurityConfig}, so this class is
 * only needed directly for creating tokens at login and for reading a token's subject.
 */
@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final long jwtExpiration;

    public JwtService(JwtEncoder encoder,
                      JwtDecoder decoder,
                      @Value("${security.jwt.expiration-time}") long jwtExpiration) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.jwtExpiration = jwtExpiration;
    }

    /** Creates a token for the user with only the standard claims (sub, iat, exp). */
    public String generateToken(UserDetails userDetails) {
        return generateToken(Map.of(), userDetails);
    }

    /**
     * Creates a token for the user, adding {@code extraClaims} (for example roles) to the payload.
     * The token expires {@code security.jwt.expiration-time} milliseconds after it is issued.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .claims(c -> c.putAll(extraClaims))
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusMillis(jwtExpiration))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    /**
     * Returns the token's subject (the employee's email) after verifying its signature and expiry.
     *
     * @throws org.springframework.security.oauth2.jwt.JwtException if the token is malformed, tampered with or expired
     */
    public String extractUsername(String token) {
        return decoder.decode(token).getSubject();
    }

    /** Token lifetime in milliseconds, returned to clients as {@code expiresIn}. */
    public long getExpirationTime() {
        return jwtExpiration;
    }
}
