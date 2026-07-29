# backend/annotation

annotation 모듈 (모듈러 모놀리식 내부 모듈, 모듈 간 DB 직접 접근 금지 — 명시적 인터페이스로만 통신)

CQRS + Event Sourcing 적용 모듈. ANNOTATION_EVENTS(쓰기/이벤트 로그)와 ANNOTATIONS(읽기 프로젝션)를 분리 관리.
