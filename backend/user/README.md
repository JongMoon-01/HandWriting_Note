# backend/user

User 모듈 (모듈러 모놀리식 내부 모듈, 모듈 간 DB 직접 접근 금지 — 명시적 인터페이스로만 통신).

## 구현된 것
- 이메일/비밀번호 회원가입 + 이메일 인증 (`/api/users/register`, `/api/users/verify-email`)
- 로그인 → JWT 발급 (`/api/users/login`)
- Google/Naver/Kakao 소셜 로그인 (Spring Security OAuth2 Client, `CustomOAuth2UserService`에서 provider별 응답 구조 파싱)
- 비밀번호 해싱: Argon2 (Spring Security 기본 파라미터)
- Stateless JWT 인증 필터 (`JwtAuthFilter`)

## TODO
- `ConsoleEmailSender`는 실제 이메일 발송 대신 로그만 찍는 임시 구현 — SMTP/SES 등으로 교체 필요
- `SocialProfile`의 provider별 attribute 파싱은 실제 OAuth2 호출로 아직 검증 안 됨 (특히 Naver/Kakao) — 실제 client-id/secret 발급받아 로컬에서 소셜 로그인 플로우 직접 테스트 필요
- `UserExceptionHandler`는 지금 user 모듈에만 있음 — 다른 모듈도 예외 처리 필요해지면 공통 모듈로 분리 고려
- 통합 테스트(Testcontainers로 실제 Postgres 띄워서 회원가입→인증→로그인 전체 플로우 검증) 아직 없음, 지금은 UserServiceTest 유닛 테스트만 존재
