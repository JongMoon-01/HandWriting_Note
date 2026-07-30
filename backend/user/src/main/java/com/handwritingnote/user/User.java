package com.handwritingnote.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(nullable = false)
    private String name;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false)
    private AuthProvider authProvider;

    @Column(name = "provider_id")
    private String providerId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected User() {
        // JPA
    }

    private User(String email, String passwordHash, String name, AuthProvider authProvider,
                  String providerId, boolean emailVerified) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.authProvider = authProvider;
        this.providerId = providerId;
        this.emailVerified = emailVerified;
    }

    public static User localSignup(String email, String passwordHash, String name) {
        return new User(email, passwordHash, name, AuthProvider.LOCAL, null, false);
    }

    public static User socialSignup(String email, String name, AuthProvider provider, String providerId) {
        return new User(email, null, name, provider, providerId, true);
    }

    public void markEmailVerified() {
        this.emailVerified = true;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getName() {
        return name;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public String getProviderId() {
        return providerId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
