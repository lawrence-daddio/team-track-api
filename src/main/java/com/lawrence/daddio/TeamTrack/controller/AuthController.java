package com.lawrence.daddio.TeamTrack.controller;

import com.lawrence.daddio.TeamTrack.service.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Public authentication endpoint that exchanges an employee's credentials for a JWT. */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    /** Login payload; the email is the employee's username. */
    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    /** @param token signed JWT to send as a Bearer token; @param expiresIn token lifetime in milliseconds */
    public record LoginResponse(String token, long expiresIn) {}

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Verifies the credentials and returns a JWT.
     *
     * @return 200 with the token, or 401 if the email or password is wrong
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Delegates to Spring Security: loads the Employee by email (EmployeeUserDetailsService)
            // and checks the password against its stored hash (PasswordEncoder). Throws on failure.
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(request.email(), request.password()));
            String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());
            return ResponseEntity.ok(new LoginResponse(token, jwtService.getExpirationTime()));
        } catch (AuthenticationException e) {
            // Same 401 for unknown email and wrong password, so callers can't tell which accounts exist.
            log.debug("Login failed for {}", request.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
