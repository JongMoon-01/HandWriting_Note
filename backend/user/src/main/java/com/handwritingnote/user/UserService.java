package com.handwritingnote.user;

import com.handwritingnote.user.dto.RegisterRequest;
import com.handwritingnote.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
public class UserService {

    private static final Duration VERIFICATION_TOKEN_TTL = Duration.ofHours(24);

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final SecureRandom secureRandom = new SecureRandom();

    public UserService(
            UserRepository userRepository,
            EmailVerificationTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            EmailSender emailSender) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다: " + request.email());
        }

        User user = User.localSignup(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.name());
        user = userRepository.save(user);

        issueVerificationToken(user);

        return toResponse(user);
    }

    @Transactional
    public void verifyEmail(String rawToken) {
        EmailVerificationToken token = tokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 인증 토큰입니다"));

        if (token.isExpired()) {
            throw new IllegalStateException("만료된 인증 토큰입니다");
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다"));

        user.markEmailVerified();
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
    }

    private void issueVerificationToken(User user) {
        String rawToken = generateToken();
        EmailVerificationToken token = new EmailVerificationToken(
                user.getId(), rawToken, Instant.now().plus(VERIFICATION_TOKEN_TTL));
        tokenRepository.save(token);
        emailSender.sendVerificationEmail(user.getEmail(), rawToken);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(), user.getEmail(), user.getName(),
                user.isEmailVerified(), user.getAuthProvider(), user.getCreatedAt());
    }
}
