-- V2: User 인증 관련 필드 추가 (이메일 인증 + 소셜 로그인)

ALTER TABLE users
    ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN auth_provider  VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    ADD COLUMN provider_id    VARCHAR(255);

-- 같은 소셜 provider 내에서 provider_id 중복 가입 방지 (LOCAL 계정은 대상 아님)
CREATE UNIQUE INDEX uq_users_provider ON users(auth_provider, provider_id) WHERE provider_id IS NOT NULL;

CREATE TABLE email_verification_tokens (
    id         UUID PRIMARY KEY DEFAULT uuidv7(),
    user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token      VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_email_verification_tokens_user_id ON email_verification_tokens(user_id);
