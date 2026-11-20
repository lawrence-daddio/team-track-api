package com.lawrence.daddio.TeamTrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    /**
     * Hashes new passwords with bcrypt and stores them with an id prefix such as {@code {bcrypt}...}.
     * The prefix tells the encoder which algorithm to use when checking a login, so stored hashes
     * without a prefix can never match.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
