package com.handwritingnote.user.dto;

public record AuthResponse(String accessToken, UserResponse user) {
}
