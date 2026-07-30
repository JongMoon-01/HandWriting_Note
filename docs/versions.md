# 기술 스택 버전 명세

기준일: 2026-07-30. 배포 전에 재확인 권장 (특히 Kafka/PostgreSQL/cert-manager는 릴리즈 주기가 빠름).

## 백엔드 (Java / Gradle 멀티모듈)

| 구성요소 | 버전 | 비고 |
|---|---|---|
| Java | 21 (LTS) | Gradle toolchain으로 고정 (`backend/build.gradle.kts`) |
| Spring Boot | 4.1.0 | Spring Framework 7 기반, Java 17+ 요구 |
| Gradle | 9.5.1 | Spring Boot Gradle 플러그인이 8.14+ / 9.x 공식 지원 확인됨 |
| io.spring.dependency-management (plugin) | 1.1.7 | Spring Boot BOM import용 |
| PostgreSQL JDBC 드라이버 | Spring Boot BOM이 버전 관리 | 별도 고정 불필요 |
| PostgreSQL 서버 | 18.4 | |
| Flyway (core / database-postgresql) | 12.9.0 (명시적 오버라이드) | Spring Boot 4.0.x가 관리하는 11.14.1은 PostgreSQL 18 감지 실패 버그 있음 ([spring-boot#49012](https://github.com/spring-projects/spring-boot/issues/49012)) |
| jjwt (api / impl / jackson) | 0.13.0 | User 모듈 JWT 발급/검증 |

## 비동기 워커 (Go)

| 구성요소 | 버전 | 비고 |
|---|---|---|
| Go | 1.26 (1.26.5 이상 권장, 보안 패치 포함) | |
| segmentio/kafka-go | 미고정 (`go get ...@latest` 시 고정 예정) | 순수 Go, cgo 의존성 없음 |

## 프론트엔드 (React, Vite로 스캐폴딩됨)

| 구성요소 | 버전 | 비고 |
|---|---|---|
| React / react-dom | 19.2.7 | |
| Vite | 8.1.1 | |
| TypeScript | 6.0.2 | |
| @vitejs/plugin-react | 6.0.3 | |
| oxlint | 1.71.0 | 기본 린터 (vite 템플릿 기본값) |

## 메시징 / 인프라

| 구성요소 | 버전 | 비고 |
|---|---|---|
| Kafka | 4.2.0 이상 | KRaft 모드 (ZooKeeper 완전 제거됨) |
| k3s | v1.36.2+k3s1 | Vultr VPS 배포 대상 |
| cert-manager | v1.21.0 | Let's Encrypt HTTP-01 |
| Traefik | k3s 번들 버전 그대로 사용 | 별도 설치 불필요 |
| MinIO | RELEASE 최신(rolling release) | 배포 시점 태그 고정 권장 |
| Grafana Loki / Promtail / Grafana | 최신 안정 버전 | 배포 시점에 고정 예정 |

## 아직 버전 미확정

- PaddleOCR PP-OCRv5 정확한 릴리즈 태그 (모델 파인튜닝 착수 시 고정)
- Grafana Loki / Promtail 구체 버전
- MinIO 서버 릴리즈 태그
