package com.handwritingnote.user.dto;

import com.handwritingnote.user.AuthProvider;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String name,
        boolean emailVerified,
        AuthProvider authProvider,
        Instant createdAt
) {
}
