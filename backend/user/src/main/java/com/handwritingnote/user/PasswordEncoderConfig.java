package com.handwritingnote.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // OWASP 권장 파라미터로 미리 튜닝된 Spring Security 기본값 사용
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
