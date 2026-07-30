package com.handwritingnote.user;

import com.handwritingnote.user.dto.AuthResponse;
import com.handwritingnote.user.dto.LoginRequest;
import com.handwritingnote.user.dto.RegisterRequest;
import com.handwritingnote.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.findByEmail(request.email());

        if (user.getPasswordHash() == null
                || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다");
        }
        if (!user.isEmailVerified()) {
            throw new IllegalStateException("이메일 인증이 필요합니다");
        }

        String accessToken = jwtService.issue(user.getId(), user.getEmail());
        UserResponse userResponse = new UserResponse(
                user.getId(), user.getEmail(), user.getName(),
                user.isEmailVerified(), user.getAuthProvider(), user.getCreatedAt());

        return ResponseEntity.ok(new AuthResponse(accessToken, userResponse));
    }
}
