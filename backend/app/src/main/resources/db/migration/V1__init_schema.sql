-- V1: 초기 스키마 (docs/design.md ERD 반영)
-- PostgreSQL 18 native uuidv7() 사용 (시간 순서 보장, B-tree 인덱스 지역성 개선)

CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT uuidv7(),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    name          VARCHAR(100) NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE documents (
    id          UUID PRIMARY KEY DEFAULT uuidv7(),
    owner_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title       VARCHAR(500) NOT NULL,
    file_type   VARCHAR(20) NOT NULL,
    source_key  VARCHAR(1024) NOT NULL,
    status      VARCHAR(30) NOT NULL DEFAULT 'UPLOADED',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_documents_owner_id ON documents(owner_id);

CREATE TABLE ocr_jobs (
    id            UUID PRIMARY KEY DEFAULT uuidv7(),
    document_id   UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    status        VARCHAR(30) NOT NULL DEFAULT 'QUEUED',
    error_message TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at  TIMESTAMPTZ
);
CREATE INDEX idx_ocr_jobs_document_id ON ocr_jobs(document_id);

-- CQRS 쓰기 모델: append-only 이벤트 로그 (source of truth)
CREATE TABLE annotation_events (
    id            UUID PRIMARY KEY DEFAULT uuidv7(),
    document_id   UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    annotation_id UUID NOT NULL,
    user_id       UUID NOT NULL REFERENCES users(id),
    event_type    VARCHAR(50) NOT NULL,
    payload       JSONB NOT NULL,
    seq           BIGINT GENERATED ALWAYS AS IDENTITY,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_annotation_events_document_id ON annotation_events(document_id);
CREATE INDEX idx_annotation_events_annotation_id ON annotation_events(annotation_id);
CREATE INDEX idx_annotation_events_payload_gin ON annotation_events USING GIN (payload);

-- CQRS 읽기 모델: 이벤트를 리플레이해서 만든 프로젝션 (뷰어가 실제 조회)
-- id는 annotation_events.annotation_id와 동일한 값을 사용 (새로 생성하지 않음)
CREATE TABLE annotations (
    id           UUID PRIMARY KEY,
    document_id  UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    created_by   UUID NOT NULL REFERENCES users(id),
    type         VARCHAR(30) NOT NULL,
    target       JSONB NOT NULL,
    body         JSONB,
    deleted      BOOLEAN NOT NULL DEFAULT false,
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_annotations_document_id ON annotations(document_id);
CREATE INDEX idx_annotations_target_gin ON annotations USING GIN (target);
CREATE INDEX idx_annotations_body_gin ON annotations USING GIN (body);

CREATE TABLE export_jobs (
    id            UUID PRIMARY KEY DEFAULT uuidv7(),
    document_id   UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    requested_by  UUID NOT NULL REFERENCES users(id),
    status        VARCHAR(30) NOT NULL DEFAULT 'QUEUED',
    result_key    VARCHAR(1024),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at  TIMESTAMPTZ
);
CREATE INDEX idx_export_jobs_document_id ON export_jobs(document_id);
