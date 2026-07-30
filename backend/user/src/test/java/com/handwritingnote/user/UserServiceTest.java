package com.handwritingnote.user;

import com.handwritingnote.user.dto.RegisterRequest;
import com.handwritingnote.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EmailVerificationTokenRepository tokenRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    EmailSender emailSender;

    @Test
    void registerCreatesUserAndSendsVerificationEmail() {
        UserService service = new UserService(userRepository, tokenRepository, passwordEncoder, emailSender);

        when(userRepository.existsByEmail("a@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = service.register(new RegisterRequest("a@test.com", "password123", "Tester"));

        assertEquals("a@test.com", response.email());
        assertFalse(response.emailVerified());
        verify(emailSender).sendVerificationEmail(eq("a@test.com"), any());
    }

    @Test
    void registerRejectsDuplicateEmail() {
        UserService service = new UserService(userRepository, tokenRepository, passwordEncoder, emailSender);
        when(userRepository.existsByEmail("dup@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                service.register(new RegisterRequest("dup@test.com", "password123", "Tester")));
    }
}
