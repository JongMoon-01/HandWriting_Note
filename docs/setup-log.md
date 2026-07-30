# 초기 셋업 재현 과정

빈 레포(`HandWriting_Note`, MIT 라이선스)에서 지금 상태까지 만든 과정을 순서대로 기록. 새로 밀어야 하거나 다른 환경에서 재현할 때 참고.

## 1. 레포 준비

- GitHub에서 빈 레포 생성 (MIT 라이선스로 초기화)
- 로컬에 클론
  ```bash
  git clone https://github.com/JongMoon-01/HandWriting_Note.git
  cd HandWriting_Note
  ```
- Push용 Personal Access Token 발급: **Settings → Developer settings → Personal access tokens**
  - `repo` 스코프 + **`workflow` 스코프까지 반드시 체크** — `workflow` 빠지면 `.github/workflows/*.yml` push 시
    `refusing to allow a Personal Access Token to create or update workflow` 에러로 거부됨
  - HTTPS로 push 시 비밀번호 자리에 이 토큰을 그대로 입력 (GitHub는 password 인증 자체를 막아둠)

## 2. 최상위 디렉토리 구조

```
.
├── backend/            # Java 모듈러 모놀리식
│   ├── document/
│   ├── annotation/
│   ├── user/
│   └── export/
├── worker/              # Go 비동기 워커
├── frontend/            # React 클라이언트
├── infra/               # k8s 매니페스트, Traefik/cert-manager 설정
├── docs/                # 설계 문서
└── .github/workflows/   # CI/CD 파이프라인
```

## 3. 프론트엔드 스캐폴딩

실제 `npm create vite`로 생성 (버전은 `docs/versions.md` 참고):

```bash
npm create vite@latest frontend -- --template react-ts
```

`package.json`의 `name` 필드만 `handwriting-note-frontend`로 수정.

## 4. 백엔드 스캐폴딩 (Gradle 멀티모듈)

수동 작성 (로컬에 Gradle/JDK가 없어 `gradle wrapper` 커맨드는 직접 실행하지 못함 — 아래 "다음 액션" 참고).

- `backend/settings.gradle.kts`: `app`, `document`, `annotation`, `user`, `export` 5개 모듈 include
- `backend/build.gradle.kts` (root): Java 21 toolchain, Spring Boot 4.1.0 + io.spring.dependency-management 1.1.7 BOM을 `subprojects`에 공통 적용
- `document` / `annotation` / `user` / `export` 각 모듈: 도메인별 `build.gradle.kts` + 마커 클래스(`XxxModule.java`) + 스모크 테스트 1개씩
  - `annotation`, `export` 모듈에는 `spring-kafka` 추가 (이벤트 발행/구독 대비)
  - `user` 모듈에는 `spring-boot-starter-security` + `spring-security-oauth2-client` 추가 (소셜 로그인 대비)
  - `document` 모듈에는 `io.minio:minio` 추가
- `app` 모듈: `Application.java`(`@SpringBootApplication`), `application.yml`(datasource/kafka/minio 환경변수 placeholder), 4개 도메인 모듈 의존성 추가, Spring Boot Gradle 플러그인 적용

## 5. Go 워커 스캐폴딩

```bash
# go.mod 수동 작성 (go 바이너리가 없어 go mod init 대신 직접 작성)
module github.com/JongMoon-01/HandWriting_Note/worker
go 1.26
```

`cmd/worker/main.go` + `internal/kafka`, `internal/ocr` 패키지 스텁만 작성 (아직 kafka-go 미설치, 로직 없음).

## 6. CI 워크플로우

`.github/workflows/ci.yml`: 모듈별 유닛 테스트(matrix) → Go 워커 테스트 → 통합 테스트 → GHCR 이미지 푸시 → k3s 배포, 5단계 job으로 구성. 실제 명령은 전부 `TODO` — 백엔드/워커 코드가 실제로 생기는 시점에 채워 넣을 것.

## 7. DB 스키마 (Flyway)

- `docs/design.md`의 ERD를 그대로 `backend/app/src/main/resources/db/migration/V1__init_schema.sql`로 작성
  - PK는 PostgreSQL 18의 네이티브 `uuidv7()` 사용 (시간순 정렬, 인덱스 지역성 개선 — extension 불필요)
  - `annotation_events`(쓰기/이벤트 로그), `annotations`(읽기 프로젝션) 분리, JSONB 컬럼엔 GIN 인덱스
- `app/build.gradle.kts`에 Flyway를 **12.9.0으로 명시적 고정**
  - 이유: Spring Boot 4.0.x가 관리하는 Flyway 11.14.1이 `Unsupported Database: PostgreSQL 18.1` 에러를 내는
    알려진 버그가 있음 ([spring-boot#49012](https://github.com/spring-projects/spring-boot/issues/49012))
- `docker-compose.yml` 루트에 추가 (Postgres 18.4 단독, Kafka/MinIO는 해당 모듈 작업 시 추가 예정)

## 8. 첫 커밋 & 푸시

```bash
git add .
git commit -m "chore: 초기 프로젝트 스캐폴딩 및 설계 문서 추가"
git push origin main
```

첫 시도에서 `workflow` 스코프 없는 토큰으로 실패 → 토큰 재발급 후 성공 (1단계 참고).

## 9. User 모듈 구현

합의된 순서(DB → User → Document → Annotation → Export)에 따라 User 모듈부터 실제 구현:

- `V2__user_auth_fields.sql`: users 테이블에 email_verified/auth_provider/provider_id 추가, email_verification_tokens 테이블 신설
- User/EmailVerificationToken 엔티티, Repository
- Argon2 비밀번호 해싱, JWT 발급(JwtService, jjwt 0.13.0)
- 이메일 인증 플로우: 회원가입 시 토큰 생성 → (임시로 로그만 찍는 ConsoleEmailSender) → `/api/users/verify-email`로 검증
- Google/Naver/Kakao OAuth2 소셜 로그인: CustomOAuth2UserService에서 provider별 사용자 정보 구조를 SocialProfile로 통일 파싱, 로그인 성공 시 JWT 발급 후 프론트로 리다이렉트
- Stateless SecurityConfig: `/api/users/register|login|verify-email`, `/oauth2/**`만 permitAll, 나머지는 JwtAuthFilter로 인증

## 다음 액션 (아직 안 한 것)

- `cd backend && gradle wrapper --gradle-version 9.5.1` (로컬에 Gradle 설치되어 있거나 IntelliJ로 열면 자동 생성)
- `cd worker && go get github.com/segmentio/kafka-go@latest && go mod tidy`
- User 모듈부터 실제 엔티티/API 구현 시작 (합의된 순서: DB → User → Document → Annotation → Export, Annotation은 최소 기능 나오는 대로 프론트와 조기 통합)
