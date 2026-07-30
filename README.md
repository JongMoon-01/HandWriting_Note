# HandWriting_Note

논문·문서를 웹에서 보면서 하이라이트, 볼드, 텍스트 색상 변경, 메모 등 인터랙티브한 주석(annotation)을 달 수 있는 서비스.

설계 배경 및 전체 아키텍처 결정 사항은 [`docs/design.md`](docs/design.md), 기술 스택 버전 명세는 [`docs/versions.md`](docs/versions.md), 초기 셋업 재현 과정은 [`docs/setup-log.md`](docs/setup-log.md) 참고.

## 기술 스택

- 프론트엔드: React
- 메인 백엔드: Java (모듈러 모놀리식 — Document / Annotation / User / Export)
- 비동기 워커: Go (OCR·PDF 렌더링, Kafka consumer)
- DB: PostgreSQL
- 메시징: Kafka
- 오브젝트 스토리지: MinIO (1차) + Cloudflare R2/Backblaze B2 (백업)
- OCR: PaddleOCR PP-OCRv5 (한글 모델)
- 배포: k3s + Traefik + cert-manager, GitHub Actions CI/CD
- 로깅: Grafana Loki

## 디렉토리 구조

```
.
├── backend/            # Java 모듈러 모놀리식
│   ├── document/
│   ├── annotation/
│   ├── user/
│   └── export/
├── worker/              # Go 비동기 워커 (OCR·PDF 렌더링)
├── frontend/            # React 클라이언트
├── infra/               # k8s 매니페스트, Traefik/cert-manager 설정
├── docs/                # 설계 문서
└── .github/workflows/   # CI/CD 파이프라인
```

## 로컬 개발

### 사전 준비 (한 번만)
```bash
cd backend
gradle wrapper --gradle-version 9.5.1   # gradle이 로컬에 없으면 IntelliJ로 backend/ 열어서 자동 생성해도 됨
```

### backend (Java, Gradle 멀티모듈: app/document/annotation/user/export)
```bash
docker compose up -d postgres   # 로컬 Postgres (Flyway가 부팅 시 자동으로 마이그레이션 적용)
cd backend
./gradlew test    # 모듈별 유닛 테스트
./gradlew :app:bootRun
```

### worker (Go)
```bash
cd worker
go get github.com/segmentio/kafka-go@latest && go mod tidy   # 최초 1회
go run ./cmd/worker
```

### frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
```

Postgres는 `docker-compose.yml`로 로컬 구동 가능 (루트에서 `docker compose up -d postgres`). Kafka/MinIO는 해당 모듈(Annotation/Export, Document) 작업 시 추가 예정.

## 배포

로컬에서 단위 테스트 → GitHub Actions CI(모듈별 유닛 테스트 → 통합 테스트) → 이미지 빌드/푸시 → Vultr(서울 리전) VPS의 k3s 클러스터로 배포.
